/*
 * Copyright 2019 XCI JV, LLC.
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
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import com.xci.zenkey.sdk.AuthorizationError
import com.xci.zenkey.sdk.AuthorizationResponse
import com.xci.zenkey.sdk.internal.contract.*
import com.xci.zenkey.sdk.internal.ktx.isUserNotFoundError
import com.xci.zenkey.sdk.internal.ktx.loginHintToken
import com.xci.zenkey.sdk.internal.ktx.mccMnc
import com.xci.zenkey.sdk.internal.ktx.state
import com.xci.zenkey.sdk.internal.model.AuthorizationRequest
import com.xci.zenkey.sdk.internal.model.AuthorizationState
import com.xci.zenkey.sdk.internal.model.AuthorizationState.*
import com.xci.zenkey.sdk.internal.model.OpenIdConfiguration
import com.xci.zenkey.sdk.internal.model.exception.ProviderNotFoundException

internal class DefaultAuthorizationService internal constructor(
        private val discoveryService: IDiscoveryService,
        private val intentFactory: AuthorizationIntentFactory,
        private val simDataProvider: SimDataProvider,
        private val responseFactory: AuthorizationResponse.Factory
) : AuthorizationService {

    @VisibleForTesting
    internal var state: AuthorizationState? = null
    @VisibleForTesting
    internal lateinit var request: AuthorizationRequest
    @VisibleForTesting
    internal var mccMnc: String? = null
    @VisibleForTesting
    internal var successIntent: PendingIntent? = null
    @VisibleForTesting
    internal var failureIntent: PendingIntent? = null
    @VisibleForTesting
    internal var completionIntent: PendingIntent? = null
    @VisibleForTesting
    internal var cancellationIntent: PendingIntent? = null

    override fun onCreate(activity: Activity, intent: Intent, savedInstanceState: Bundle?) {
        if (savedInstanceState == null && intent.extras == null) {
            //TODO should setResult with error.
            activity.finish()
            return
        }
        activity.overridePendingTransition(0, 0)
        intentFactory.bindWebSession(activity)
        extractState(savedInstanceState ?: intent.extras!!)
    }

    override fun onResume(activity: Activity, intent: Intent) {
        checkState(activity, intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        saveState(outState)
    }

    override fun onNewIntent(activity: Activity, intent: Intent) {
        activity.intent = intent
    }

    override fun onDestroy(activity: Activity) {
        this.intentFactory.unbindWebSession(activity)
    }

    private fun extractState(savedInstanceState: Bundle) {
        request = savedInstanceState.getParcelable(EXTRA_KEY_REQUEST)!!
        mccMnc = savedInstanceState.getString(Json.KEY_MCC_MNC, null)
        if(savedInstanceState.containsKey(EXTRA_KEY_STATE)){
            state = savedInstanceState.getSerializable(EXTRA_KEY_STATE) as AuthorizationState
        } else {
            updateState(NONE)
        }

        successIntent = savedInstanceState.getParcelable(EXTRA_KEY_SUCCESS_INTENT)
        failureIntent = savedInstanceState.getParcelable(EXTRA_KEY_FAILURE_INTENT)
        completionIntent = savedInstanceState.getParcelable(EXTRA_KEY_COMPLETION_INTENT)
        cancellationIntent = savedInstanceState.getParcelable(EXTRA_KEY_CANCELLATION_INTENT)
    }

    @VisibleForTesting
    internal fun checkState(activity: Activity, intent: Intent) {
        val resultUri = intent.data
        when {
            this.state == NONE -> onStateNone(activity)
            resultUri != null -> when {
                this.state == DISCOVER_UI -> onStateDiscoverUI(activity, resultUri)
                this.state == AUTHORIZE -> onStateAuthorize(activity, resultUri)
                this.state == DISCOVER_USER_NOT_FOUND -> onStateDiscoverUserNotFound(activity, resultUri)
                else -> setResultAndFinish(activity, responseFactory.create(mccMnc!!, request, resultUri))
            }
            else -> setCanceledAndFinish(activity)
        }
    }

    @VisibleForTesting
    internal fun onStateNone(activity: Activity) {
        updateMccMnc(simDataProvider.simOperator)
        Logger.get().begin(request, mccMnc)
        discoverOpenIdConfiguration(mccMnc, false,
                { authorize(activity, it, AUTHORIZE, null) },
                { throwable ->
                    handleProviderNotFoundOrFinish(activity, throwable) {
                        discoverUiEndpoint ->
                        updateState(DISCOVER_UI)
                        onProviderNotFoundError(activity, discoverUiEndpoint)
                    }
                })
    }

    @VisibleForTesting
    internal fun onStateDiscoverUI(activity: Activity, resultUri: Uri) {
        if(request.isNotMatching(resultUri.state)){
            finishWithStateMissMatchError(activity)
        } else {
            updateMccMnc(resultUri.mccMnc)
            discoverOpenIdConfiguration(mccMnc, false,
                    { authorize(activity, it, AUTHORIZE, resultUri.loginHintToken) },
                    {
                        handleProviderNotFoundOrFinish(activity, it) {
                            finishWithTooManyRedirectError(activity)
                        }
                    })
        }
    }

    @VisibleForTesting
    internal fun onStateAuthorize(activity: Activity, resultUri: Uri) {
        if (resultUri.isUserNotFoundError) {
            discoverOpenIdConfiguration(mccMnc, true,
                    {
                        //With the prompt=true parameter, this case can't happen.
                        //This parameter force the endpoint to return a ProviderNotFoundException
                        setResultAndFinish(activity, responseFactory.create(mccMnc!!, request.redirectUri,
                                AuthorizationError.DISCOVERY_STATE.withDescription("Received OIDC with prompt=true")))
                    },
                    { throwable ->
                        handleProviderNotFoundOrFinish(activity, throwable) {
                            discoverUiEndpoint ->
                            updateState(DISCOVER_USER_NOT_FOUND)
                            onProviderNotFoundError(activity, discoverUiEndpoint)
                        }
                    })
        } else {
            setResultAndFinish(activity, responseFactory.create(mccMnc!!, request, resultUri))
        }
    }

    @VisibleForTesting
    internal fun onStateDiscoverUserNotFound(activity: Activity,
                                             resultUri: Uri) {
        if(request.isNotMatching(resultUri.state)){
            finishWithStateMissMatchError(activity)
        } else {
            updateMccMnc(resultUri.mccMnc)
            discoverOpenIdConfiguration(mccMnc, false,
                    { authorize(activity, it, AUTHORIZE_USER_NOT_FOUND, resultUri.loginHintToken) },
                    {
                        handleProviderNotFoundOrFinish(activity, it) {
                            finishWithTooManyRedirectError(activity)
                        }
                    })
        }
    }

    private fun discoverOpenIdConfiguration(mccMnc: String?, prompt: Boolean,
                                            onSuccess: (OpenIdConfiguration) -> Unit,
                                            onError: (Throwable) -> Unit){
        this.discoveryService.discoverConfiguration(mccMnc, prompt, onSuccess, onError)
    }

    @VisibleForTesting
    internal fun updateState(state: AuthorizationState) {
        this.state = state
        Logger.get().state(state)
    }

    @VisibleForTesting
    internal fun updateMccMnc(mccMnc: String?){
        this.mccMnc = mccMnc
        Logger.get().d("Update MCC_MNC --> $mccMnc")
    }

    @VisibleForTesting
    internal fun authorize(activity: Activity,
                           configuration: OpenIdConfiguration,
                           state: AuthorizationState,
                           loginHintToken: String?) {
        if (configuration.mccMnc != null) {
            this@DefaultAuthorizationService.mccMnc = configuration.mccMnc
            Logger.get().d("Update MCC_MNC --> " + mccMnc!!)
        }
        updateState(state)
        startAuthorize(activity, configuration, loginHintToken)
    }

    @VisibleForTesting
    internal fun saveState(outState: Bundle) {
        outState.putSerializable(EXTRA_KEY_STATE, state)
        outState.putParcelable(EXTRA_KEY_REQUEST, request)
        outState.putString(Json.KEY_MCC_MNC, mccMnc)
        outState.putParcelable(EXTRA_KEY_SUCCESS_INTENT, successIntent)
        outState.putParcelable(EXTRA_KEY_FAILURE_INTENT, failureIntent)
        outState.putParcelable(EXTRA_KEY_COMPLETION_INTENT, completionIntent)
        outState.putParcelable(EXTRA_KEY_CANCELLATION_INTENT, cancellationIntent)
    }

    @VisibleForTesting
    internal fun onProviderNotFoundError(activity: Activity,
                                         discoverUiEndpoint: String?) {
        if (discoverUiEndpoint != null) {
            startDiscoverUI(activity, discoverUiEndpoint)
        } else {
            setResultAndFinish(activity,
                    responseFactory.create(mccMnc!!, request.redirectUri, AuthorizationError.DISCOVERY_STATE
                            .withDescription("Provider Not Found : Missing DiscoverUI endpoint")))
        }
    }

    @VisibleForTesting
    internal fun startAuthorize(activity: Activity,
                                openIdConfiguration: OpenIdConfiguration,
                                loginHintToken: String?) {
        activity.intent = activity.intent.setData(null)
        try {
            val authUri = request.withLoginHintToken(loginHintToken).toAuthorizationUri(openIdConfiguration.authorizationEndpoint)
            Logger.get().request(authUri)
            activity.startActivity(intentFactory.createAuthorizeIntent(authUri, openIdConfiguration.packages))
        } catch (e: ActivityNotFoundException) {
            setResultAndFinish(activity, responseFactory.create(mccMnc!!, request.redirectUri,
                    AuthorizationError.UNKNOWN.withDescription("No browser available")))
        }

    }

    @VisibleForTesting
    internal fun startDiscoverUI(activity: Activity,
                                 discoverUIEndpoint: String) {
        try {
            activity.intent = activity.intent.setData(null)
            activity.startActivity(intentFactory.createDiscoverUIIntent(request.toDiscoverUiUri(discoverUIEndpoint)))
        } catch (e: ActivityNotFoundException) {
            Logger.get().e("No browser available")
            setResultAndFinish(activity, responseFactory.create(mccMnc!!, request.redirectUri,
                    AuthorizationError.UNKNOWN.withDescription("No browser available")))
        }
    }

    @VisibleForTesting
    internal fun setCanceledAndFinish(activity: Activity) {
        Logger.get().end(request, null)
        if (cancellationIntent != null) {
            try {
                cancellationIntent!!.send(activity, RESULT_CANCELED, null)
            } catch (e: PendingIntent.CanceledException) {
                Logger.get().e("Unable to start cancellation pending intent")
                activity.setResult(RESULT_CANCELED)
                activity.finish()
            }

        } else {
            activity.setResult(RESULT_CANCELED)
            activity.finish()
        }
    }

    private fun handleProviderNotFoundOrFinish(activity: Activity,
                                               throwable: Throwable,
                                               onProviderNotFound: (String?) -> Unit){
        Logger.get().throwable(throwable)
        if (throwable is ProviderNotFoundException) {
            onProviderNotFound.invoke(throwable.discoverUiEndpoint)
        } else {
            finishWithThrowableError(activity, throwable)
        }
    }

    private fun finishWithStateMissMatchError(activity: Activity){
        setResultAndFinish(activity, responseFactory.create(mccMnc!!, request.redirectUri,
                AuthorizationError.INVALID_REQUEST.withDescription("state miss-match")))
    }

    private fun finishWithTooManyRedirectError(activity: Activity){
        setResultAndFinish(activity, responseFactory.create(mccMnc!!, request.redirectUri,
                AuthorizationError.DISCOVERY_STATE.withDescription(" too many discoverUi redirects")))
    }

    private fun finishWithThrowableError(activity: Activity, throwable: Throwable){
        setResultAndFinish(activity, responseFactory.create(mccMnc!!, request.redirectUri, throwable))
    }

    @VisibleForTesting
    internal fun setResultAndFinish(activity: Activity,
                                    response: AuthorizationResponse) {
        Logger.get().end(request, response)
        if (response.isSuccessful && successIntent != null) {
            try {
                successIntent!!.send(activity, RESULT_OK, response.toIntent())
            } catch (e: PendingIntent.CanceledException) {
                Logger.get().e("Unable to start success pending intent")
                activity.setResult(RESULT_OK, response.toIntent())
                activity.finish()
            }

        } else if (!response.isSuccessful && failureIntent != null) {
            try {
                failureIntent!!.send(activity, RESULT_OK, response.toIntent())
            } catch (e: PendingIntent.CanceledException) {
                Logger.get().e("Unable to start failure pending intent")
                activity.setResult(RESULT_OK, response.toIntent())
                activity.finish()
            }

        } else if (completionIntent != null) {
            try {
                completionIntent!!.send(activity, RESULT_OK, response.toIntent())
            } catch (e: PendingIntent.CanceledException) {
                Logger.get().e("Unable to start completion pending intent")
                activity.setResult(RESULT_OK, response.toIntent())
                activity.finish()
            }

        } else {
            activity.setResult(RESULT_OK, response.toIntent())
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
