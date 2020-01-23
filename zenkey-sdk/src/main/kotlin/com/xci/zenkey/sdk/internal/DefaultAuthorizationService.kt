/*
 * Copyright 2019 ZenKey, LLC.
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
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
        private val simDataProvider: SimDataProvider,
        internal val responseFactory: AuthorizationResponse.Factory
) : AuthorizationService {

    internal var state: AuthorizationState? = null
    internal lateinit var request: AuthorizationRequest
    internal var mccMnc: String? = null
    internal var successIntent: PendingIntent? = null
    internal var failureIntent: PendingIntent? = null
    internal var completionIntent: PendingIntent? = null
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

    internal fun checkState(activity: Activity, intent: Intent) {
        val resultUri = intent.data
        when {
            this.state == NONE -> onStateNone(activity)
            resultUri != null -> when {
                this.state == DISCOVER_UI -> onStateDiscoverUI(activity, resultUri)
                this.state == AUTHORIZE -> onStateAuthorize(activity, resultUri)
                this.state == DISCOVER_USER_NOT_FOUND -> onStateDiscoverUserNotFound(activity, resultUri)
                else -> finishWithAuthorizationSuccess(activity, resultUri)
            }
            else -> setResultCanceledAndFinish(activity)
        }
    }

    internal fun onStateNone(activity: Activity) {
        updateMccMnc(simDataProvider.simOperator)
        Logger.get().begin(request, mccMnc)
        discoverOpenIdConfiguration(false,
                { authorize(activity, it, AUTHORIZE, null) },
                { throwable ->
                    handleProviderNotFoundOrFinish(activity, throwable) {
                        discoverUiEndpoint ->
                        updateState(DISCOVER_UI)
                        onProviderNotFoundError(activity, discoverUiEndpoint)
                    }
                })
    }

    internal fun onStateDiscoverUI(activity: Activity, resultUri: Uri) {
        if(request.isNotMatching(resultUri.state)){
            finishWithStateMissMatchError(activity)
        } else {
            updateMccMnc(resultUri.mccMnc)
            discoverOpenIdConfiguration(false,
                    { authorize(activity, it, AUTHORIZE, resultUri.loginHintToken) },
                    {
                        handleProviderNotFoundOrFinish(activity, it) {
                            finishWithTooManyRedirectError(activity)
                        }
                    })
        }
    }

    internal fun onStateAuthorize(activity: Activity, resultUri: Uri) {
        if (resultUri.isUserNotFoundError) {
            discoverOpenIdConfiguration( true,
                    {
                        //With the prompt=true parameter, this case can't happen.
                        //This parameter force the endpoint to return a ProviderNotFoundException
                        finishWithUnexpectedOIDC(activity)
                    },
                    { throwable ->
                        handleProviderNotFoundOrFinish(activity, throwable) {
                            discoverUiEndpoint ->
                            updateState(DISCOVER_USER_NOT_FOUND)
                            onProviderNotFoundError(activity, discoverUiEndpoint)
                        }
                    })
        } else {
            finishWithAuthorizationSuccess(activity, resultUri)
        }
    }

    internal fun onStateDiscoverUserNotFound(activity: Activity,
                                             resultUri: Uri) {
        if(request.isNotMatching(resultUri.state)){
            finishWithStateMissMatchError(activity)
        } else {
            updateMccMnc(resultUri.mccMnc)
            discoverOpenIdConfiguration(false,
                    { authorize(activity, it, AUTHORIZE_USER_NOT_FOUND, resultUri.loginHintToken) },
                    {
                        handleProviderNotFoundOrFinish(activity, it) {
                            finishWithTooManyRedirectError(activity)
                        }
                    })
        }
    }

    private fun discoverOpenIdConfiguration(prompt: Boolean,
                                            onSuccess: (OpenIdConfiguration) -> Unit,
                                            onError: (Throwable) -> Unit){
        this.discoveryService.discoverConfiguration(mccMnc, prompt, onSuccess, onError)
    }

    internal fun authorize(activity: Activity,
                           configuration: OpenIdConfiguration,
                           state: AuthorizationState,
                           loginHintToken: String?) {
        updateMccMncIfNotNull(configuration.mccMnc)
        updateState(state)
        startAuthorize(activity, configuration, loginHintToken)
    }

    internal fun saveState(outState: Bundle) {
        outState.putSerializable(EXTRA_KEY_STATE, state)
        outState.putParcelable(EXTRA_KEY_REQUEST, request)
        outState.putString(Json.KEY_MCC_MNC, mccMnc)
        outState.putParcelable(EXTRA_KEY_SUCCESS_INTENT, successIntent)
        outState.putParcelable(EXTRA_KEY_FAILURE_INTENT, failureIntent)
        outState.putParcelable(EXTRA_KEY_COMPLETION_INTENT, completionIntent)
        outState.putParcelable(EXTRA_KEY_CANCELLATION_INTENT, cancellationIntent)
    }

    internal fun startAuthorize(activity: Activity,
                                openIdConfiguration: OpenIdConfiguration,
                                loginHintToken: String?) {
        val authUri = request.withLoginHintToken(loginHintToken).toAuthorizationUri(openIdConfiguration.authorizationEndpoint)
        Logger.get().request(authUri)
        val intent = try {
            intentFactory.createAuthorizeIntent(authUri, openIdConfiguration.packages)
        } catch (e: NoBrowserException){
            finishWithNoBrowserAvailable(activity)
            return
        }
        activity.startAuthorizationFlowActivity(intent){
            finishWithNoBrowserAvailable(activity)
        }
    }

    internal fun startDiscoverUI(activity: Activity,
                                 discoverUIEndpoint: String) {
        val intent = try {
            intentFactory.createDiscoverUIIntent(request.toDiscoverUiUri(discoverUIEndpoint))
        } catch (e: NoBrowserException){
            finishWithNoBrowserAvailable(activity)
            return
        }
        activity.startAuthorizationFlowActivity(intent){
            finishWithNoBrowserAvailable(activity)
        }
    }

    internal fun handleProviderNotFoundOrFinish(activity: Activity,
                                               throwable: Throwable,
                                               onProviderNotFound: (String?) -> Unit){
        Logger.get().throwable(throwable)
        when {
            throwable is ProviderNotFoundException -> onProviderNotFound.invoke(throwable.discoverUiEndpoint)
            throwable.isNetworkFailure -> finishWithNetworkFailure(activity, throwable.message)
            else -> finishWithThrowableError(activity, throwable)
        }
    }

    internal fun onProviderNotFoundError(activity: Activity,
                                         discoverUiEndpoint: String?) {
        if (discoverUiEndpoint != null) {
            startDiscoverUI(activity, discoverUiEndpoint)
        } else {
            finishWithMissingDiscoverUIEndpoint(activity)
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
