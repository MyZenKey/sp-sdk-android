/*
 * Copyright 2019-2020 ZenKey, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xci.zenkey.sdk.internal

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import com.xci.zenkey.sdk.AuthorizationError
import com.xci.zenkey.sdk.AuthorizationError.*
import com.xci.zenkey.sdk.AuthorizationError.Companion.MISSING_DISCOVER_UI_ENDPOINT
import com.xci.zenkey.sdk.AuthorizationError.Companion.STATE_MISMATCHED
import com.xci.zenkey.sdk.AuthorizationError.Companion.TOO_MANY_REDIRECT
import com.xci.zenkey.sdk.AuthorizationError.Companion.UNEXPECTED_DISCOVERY_RESPONSE
import com.xci.zenkey.sdk.AuthorizationResponse
import com.xci.zenkey.sdk.internal.browser.NoBrowserException
import com.xci.zenkey.sdk.internal.contract.*
import com.xci.zenkey.sdk.internal.ktx.*
import com.xci.zenkey.sdk.internal.model.AuthorizationRequest
import com.xci.zenkey.sdk.internal.model.AuthorizationState
import com.xci.zenkey.sdk.internal.model.AuthorizationState.*
import com.xci.zenkey.sdk.internal.model.OpenIdConfiguration
import com.xci.zenkey.sdk.internal.model.exception.ProviderNotFoundException

internal class DefaultAuthorizationService internal constructor(
    private val discoveryService: IDiscoveryService,
    private val intentFactory: AuthorizationIntentFactory,
    private val telephonyManager: TelephonyManager,
    private val responseFactory: AuthorizationResponse.Factory
) : AuthorizationService {

    internal var state: AuthorizationState? = null
    internal lateinit var request: AuthorizationRequest
    internal var mccMnc: String? = null
    internal var successIntent: PendingIntent? = null
    internal var failureIntent: PendingIntent? = null
    internal var completionIntent: PendingIntent? = null
    internal var cancellationIntent: PendingIntent? = null

    override fun onCreate(
        activity: AuthorizationRequestActivity,
        intent: Intent,
        savedInstanceState: Bundle?
    ) {
        if (savedInstanceState == null && intent.extras == null) {
            //TODO should setResult with error.
            activity.finish()
            return
        }
        activity.overridePendingTransition(0, 0)
        intentFactory.bindWebSession(activity)
        extractState(savedInstanceState ?: intent.extras!!)
    }

    override fun onResume(
        activity: AuthorizationRequestActivity,
        intent: Intent
    ) {
        checkState(activity, intent)
    }

    override fun onSaveInstanceState(
        outState: Bundle
    ) {
        saveState(outState)
    }

    override fun onNewIntent(
        activity: AuthorizationRequestActivity,
        intent: Intent
    ) {
        activity.intent = intent
    }

    override fun onDestroy(
        activity: AuthorizationRequestActivity
    ) {
        this.intentFactory.unbindWebSession(activity)
    }

    private fun extractState(
        savedInstanceState: Bundle
    ) {
        request = savedInstanceState.getParcelable(EXTRA_KEY_REQUEST)!!
        mccMnc = savedInstanceState.getString(Json.KEY_MCC_MNC, null)

        if (savedInstanceState.containsKey(EXTRA_KEY_STATE)) {
            state = savedInstanceState.getSerializable(EXTRA_KEY_STATE) as AuthorizationState
        } else {
            updateState(NONE)
        }

        successIntent = savedInstanceState.getParcelable(EXTRA_KEY_SUCCESS_INTENT)
        failureIntent = savedInstanceState.getParcelable(EXTRA_KEY_FAILURE_INTENT)
        completionIntent = savedInstanceState.getParcelable(EXTRA_KEY_COMPLETION_INTENT)
        cancellationIntent = savedInstanceState.getParcelable(EXTRA_KEY_CANCELLATION_INTENT)
    }

    internal fun checkState(
        activity: AuthorizationRequestActivity,
        intent: Intent
    ) {
        val resultUri = intent.data
        when {
            this.state == NONE -> onStateNone(activity)
            resultUri != null -> when (this.state) {
                DISCOVER_UI -> onStateDiscoverUI(activity, resultUri)
                AUTHORIZE -> onStateAuthorize(activity, resultUri)
                DISCOVER_USER_NOT_FOUND -> onStateDiscoverUserNotFound(activity, resultUri)
                else -> finishWithAuthorizationSuccess(activity, resultUri)
            }
            else -> setResultCanceledAndFinish(activity)
        }
    }

    internal fun onStateNone(
        activity: AuthorizationRequestActivity
    ) {
        updateMccMnc(telephonyManager.simOperatorReady)
        Logger.get().begin(request, mccMnc)
        discoverOpenIdConfiguration(false,
            { authorize(activity, it, AUTHORIZE, null) },
            { throwable ->
                handleProviderNotFoundOrFinish(activity, throwable) { discoverUiEndpoint ->
                    updateState(DISCOVER_UI)
                    onProviderNotFoundError(activity, discoverUiEndpoint)
                }
            })
    }

    internal fun onStateDiscoverUI(
        activity: AuthorizationRequestActivity,
        resultUri: Uri
    ) {
        if (!request.state.isMatching(resultUri.state)) {
            finishWithAuthorizationError(activity, STATE_MISMATCHED)
        } else {
            updateMccMnc(resultUri.mccMnc)
            discoverOpenIdConfiguration(false,
                { authorize(activity, it, AUTHORIZE, resultUri.loginHintToken) },
                {
                    handleProviderNotFoundOrFinish(activity, it) {
                        finishWithAuthorizationError(activity, TOO_MANY_REDIRECT)
                    }
                })
        }
    }

    internal fun onStateAuthorize(
        activity: AuthorizationRequestActivity,
        resultUri: Uri
    ) {
        if (resultUri.isUserNotFoundError) {
            discoverOpenIdConfiguration(true,
                {
                    //With the prompt=true parameter, this case can't happen.
                    //This parameter force the endpoint to return a ProviderNotFoundException
                    finishWithAuthorizationError(activity, UNEXPECTED_DISCOVERY_RESPONSE)
                },
                { throwable ->
                    handleProviderNotFoundOrFinish(activity, throwable) { discoverUiEndpoint ->
                        updateState(DISCOVER_USER_NOT_FOUND)
                        onProviderNotFoundError(activity, discoverUiEndpoint)
                    }
                })
        } else {
            finishWithAuthorizationSuccess(activity, resultUri)
        }
    }

    internal fun onStateDiscoverUserNotFound(
        activity: AuthorizationRequestActivity,
        resultUri: Uri
    ) {
        if (!request.state.isMatching(resultUri.state)) {
            finishWithAuthorizationError(activity, STATE_MISMATCHED)
        } else {
            updateMccMnc(resultUri.mccMnc)
            discoverOpenIdConfiguration(false,
                { authorize(activity, it, AUTHORIZE_USER_NOT_FOUND, resultUri.loginHintToken) },
                {
                    handleProviderNotFoundOrFinish(activity, it) {
                        finishWithAuthorizationError(activity, TOO_MANY_REDIRECT)
                    }
                })
        }
    }

    private fun discoverOpenIdConfiguration(
        prompt: Boolean,
        onSuccess: (OpenIdConfiguration) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        this.discoveryService.discoverConfiguration(mccMnc, prompt, onSuccess, onError)
    }

    internal fun authorize(
        activity: AuthorizationRequestActivity,
        configuration: OpenIdConfiguration,
        state: AuthorizationState,
        loginHintToken: String?
    ) {
        updateMccMncIfNotNull(configuration.mccMnc)
        updateState(state)
        startAuthorize(activity, configuration, loginHintToken)
    }

    internal fun saveState(
        outState: Bundle
    ) {
        outState.putSerializable(EXTRA_KEY_STATE, state)
        outState.putParcelable(EXTRA_KEY_REQUEST, request)
        outState.putString(Json.KEY_MCC_MNC, mccMnc)
        outState.putParcelable(EXTRA_KEY_SUCCESS_INTENT, successIntent)
        outState.putParcelable(EXTRA_KEY_FAILURE_INTENT, failureIntent)
        outState.putParcelable(EXTRA_KEY_COMPLETION_INTENT, completionIntent)
        outState.putParcelable(EXTRA_KEY_CANCELLATION_INTENT, cancellationIntent)
    }

    internal fun startAuthorize(
        activity: AuthorizationRequestActivity,
        openIdConfiguration: OpenIdConfiguration,
        loginHintToken: String?
    ) {
        val authUri = request.withLoginHintToken(loginHintToken)
            .toAuthorizationUri(openIdConfiguration.authorizationEndpoint)
        Logger.get().request(authUri)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            startAuthorize16(activity, openIdConfiguration, authUri)
        } else {
            startAuthorize30(activity, authUri)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    internal fun startAuthorize30(
        activity: AuthorizationRequestActivity,
        uri: Uri
    ) {
        activity.startZenKeyApp(
            uri.intent.apply {
                flags = FLAG_ACTIVITY_REQUIRE_DEFAULT or FLAG_ACTIVITY_REQUIRE_NON_BROWSER
            }
        ) {
            startBrowser(activity, uri)
        }
    }

    @Deprecated("Deprecated since Android 30 (R)")
    private fun startAuthorize16(
        activity: AuthorizationRequestActivity,
        openIdConfiguration: OpenIdConfiguration,
        uri: Uri
    ) {
        val intent = try {
            intentFactory.createAuthorizeIntent(uri, openIdConfiguration.packages)
        } catch (e: NoBrowserException) {
            finishWithNoBrowserAvailable(activity)
            return
        }
        activity.startZenKeyApp(intent) {
            finishWithNoBrowserAvailable(activity)
        }
    }

    internal fun startBrowser(
        activity: AuthorizationRequestActivity,
        uri: Uri
    ) {
        val intent = try {
            intentFactory.createBrowserIntent(uri)
        } catch (e: NoBrowserException) {
            finishWithNoBrowserAvailable(activity)
            return
        }
        activity.startBrowser(intent) {
            finishWithNoBrowserAvailable(activity)
        }
    }

    private fun handleProviderNotFoundOrFinish(
        activity: Activity,
        throwable: Throwable,
        onProviderNotFound: (String?) -> Unit
    ) {
        Logger.get().throwable(throwable)
        when {
            throwable is ProviderNotFoundException -> onProviderNotFound.invoke(throwable.discoverUiEndpoint)
            throwable.isNetworkFailure ->
                finishWithAuthorizationError(activity, NETWORK_FAILURE.withDescription(throwable.message))
            throwable.isTimeout ->
                finishWithAuthorizationError(activity, SERVER_ERROR.withDescription(throwable.message))
            else -> finishWithThrowableError(activity, throwable)
        }
    }

    internal fun onProviderNotFoundError(
        activity: AuthorizationRequestActivity,
        discoverUiEndpoint: String?
    ) {
        if (discoverUiEndpoint != null) {
            startBrowser(activity, request.toDiscoverUiUri(discoverUiEndpoint))
        } else {
            finishWithAuthorizationError(activity, MISSING_DISCOVER_UI_ENDPOINT)
        }
    }

    internal fun updateState(
        state: AuthorizationState
    ) {
        this.state = state
        Logger.get().state(state)
    }

    private fun updateMccMncIfNotNull(
        mccMnc: String?
    ) {
        mccMnc?.let { updateMccMnc(it) }
    }

    private fun updateMccMnc(
        mccMnc: String?
    ) {
        this.mccMnc = mccMnc
        Logger.get().d("Update MCC_MNC --> $mccMnc")
    }

    private fun finishWithNoBrowserAvailable(
        activity: Activity
    ) {
        val message = "No browser available"
        Logger.get().e(message)
        finishWithAuthorizationError(activity, DISCOVERY_STATE.withDescription(message))
    }

    private fun finishWithAuthorizationError(
        activity: Activity,
        authorizationError: AuthorizationError
    ) {
        setResultOKAndFinish(activity, responseFactory.error(mccMnc, request, authorizationError))
    }

    private fun finishWithThrowableError(
        activity: Activity,
        throwable: Throwable
    ) {
        setResultOKAndFinish(activity, responseFactory.throwable(mccMnc, request, throwable))
    }

    private fun finishWithAuthorizationSuccess(
        activity: Activity,
        resultUri: Uri
    ) {
        setResultOKAndFinish(activity, responseFactory.uri(mccMnc!!, request, resultUri))
    }

    internal fun setResultOKAndFinish(
        activity: Activity,
        response: AuthorizationResponse
    ) {
        Logger.get().end(request, response)
        if (response.isSuccessful && successIntent != null) {
            try {
                successIntent!!.send(activity, Activity.RESULT_OK, response.toIntent())
            } catch (e: PendingIntent.CanceledException) {
                Logger.get().e("Unable to start success pending intent")
                activity.setResult(Activity.RESULT_OK, response.toIntent())
                activity.finish()
            }

        } else if (!response.isSuccessful && failureIntent != null) {
            try {
                failureIntent!!.send(activity, Activity.RESULT_OK, response.toIntent())
            } catch (e: PendingIntent.CanceledException) {
                Logger.get().e("Unable to start failure pending intent")
                activity.setResult(Activity.RESULT_OK, response.toIntent())
                activity.finish()
            }

        } else if (completionIntent != null) {
            try {
                completionIntent!!.send(activity, Activity.RESULT_OK, response.toIntent())
            } catch (e: PendingIntent.CanceledException) {
                Logger.get().e("Unable to start completion pending intent")
                activity.setResult(Activity.RESULT_OK, response.toIntent())
                activity.finish()
            }

        } else {
            activity.setResult(Activity.RESULT_OK, response.toIntent())
            activity.finish()
        }
    }

    internal fun setResultCanceledAndFinish(
        activity: Activity
    ) {
        Logger.get().end(request, null)
        if (cancellationIntent != null) {
            try {
                cancellationIntent!!.send(activity, Activity.RESULT_CANCELED, null)
            } catch (e: PendingIntent.CanceledException) {
                Logger.get().e("Unable to start cancellation pending intent")
                activity.setResult(Activity.RESULT_CANCELED)
                activity.finish()
            }

        } else {
            activity.setResult(Activity.RESULT_CANCELED)
            activity.finish()
        }
    }

    companion object {
        internal const val EXTRA_KEY_REQUEST = "EXTRA_KEY_REQUEST"
        internal const val EXTRA_KEY_STATE = "EXTRA_KEY_STATE"
        internal const val EXTRA_KEY_SUCCESS_INTENT = "EXTRA_KEY_SUCCESS_INTENT"
        internal const val EXTRA_KEY_FAILURE_INTENT = "EXTRA_KEY_FAILURE_INTENT"
        internal const val EXTRA_KEY_COMPLETION_INTENT = "EXTRA_KEY_COMPLETION_INTENT"
        internal const val EXTRA_KEY_CANCELLATION_INTENT = "EXTRA_KEY_CANCELLATION_INTENT"
    }
}
