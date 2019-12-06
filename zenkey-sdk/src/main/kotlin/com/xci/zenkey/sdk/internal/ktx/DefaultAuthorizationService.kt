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

package com.xci.zenkey.sdk.internal.ktx

import android.app.Activity
import android.app.PendingIntent
import android.net.Uri
import com.xci.zenkey.sdk.AuthorizationError
import com.xci.zenkey.sdk.AuthorizationError.*
import com.xci.zenkey.sdk.AuthorizationResponse
import com.xci.zenkey.sdk.internal.DefaultAuthorizationService
import com.xci.zenkey.sdk.internal.contract.Logger
import com.xci.zenkey.sdk.internal.model.AuthorizationState

internal fun DefaultAuthorizationService.updateState(
        state: AuthorizationState
) {
    this.state = state
    Logger.get().state(state)
}

internal fun DefaultAuthorizationService.updateMccMncIfNotNull(
        mccMnc: String?
){
    mccMnc?.let { updateMccMnc(it) }
}

internal fun DefaultAuthorizationService.updateMccMnc(
        mccMnc: String?
){
    this.mccMnc = mccMnc
    Logger.get().d("Update MCC_MNC --> $mccMnc")
}

internal fun DefaultAuthorizationService.finishWithMissingDiscoverUIEndpoint(
        activity: Activity
){
    finishWithAuthorizationError(activity,
            DISCOVERY_STATE.withDescription("Provider Not Found : Missing DiscoverUI endpoint"))
}

internal fun DefaultAuthorizationService.finishWithUnexpectedOIDC(
        activity: Activity
){
    finishWithAuthorizationError(activity,
            DISCOVERY_STATE.withDescription("Received OIDC with prompt=true"))
}

internal fun DefaultAuthorizationService.finishWithNoBrowserAvailable(
        activity: Activity
){
    val message = "No browser available"
    Logger.get().e(message)
    finishWithAuthorizationError(activity, UNKNOWN.withDescription(message))
}

internal fun DefaultAuthorizationService.finishWithStateMissMatchError(
        activity: Activity
){
    finishWithAuthorizationError(activity, INVALID_REQUEST.withDescription("state miss-match"))
}

internal fun DefaultAuthorizationService.finishWithTooManyRedirectError(
        activity: Activity
){
    finishWithAuthorizationError(activity, DISCOVERY_STATE.withDescription("too many discoverUi redirects"))
}

internal fun DefaultAuthorizationService.finishWithNetworkFailure(
        activity: Activity,
        description: String?
){
    finishWithAuthorizationError(activity, NETWORK_FAILURE.withDescription(description))
}

internal fun DefaultAuthorizationService.finishWithAuthorizationError(
        activity: Activity,
        authorizationError: AuthorizationError
){
    setResultOKAndFinish(activity, responseFactory.create(mccMnc, request.redirectUri, authorizationError))
}

internal fun DefaultAuthorizationService.finishWithThrowableError(
        activity: Activity,
        throwable: Throwable
){
    setResultOKAndFinish(activity, responseFactory.create(mccMnc, request.redirectUri, throwable))
}

internal fun DefaultAuthorizationService.finishWithAuthorizationSuccess(
        activity: Activity,
        resultUri: Uri
){
    setResultOKAndFinish(activity, responseFactory.create(mccMnc!!, request, resultUri))
}

internal fun DefaultAuthorizationService.setResultOKAndFinish(
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

internal fun DefaultAuthorizationService.setResultCanceledAndFinish(
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