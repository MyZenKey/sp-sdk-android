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

import android.net.Uri
import com.xci.zenkey.sdk.AuthorizationError
import com.xci.zenkey.sdk.AuthorizationError.*
import com.xci.zenkey.sdk.AuthorizationError.Companion.STATE_MISMATCHED
import com.xci.zenkey.sdk.AuthorizationResponse
import com.xci.zenkey.sdk.internal.contract.Logger
import com.xci.zenkey.sdk.internal.ktx.*
import com.xci.zenkey.sdk.internal.model.AuthorizationRequest
import com.xci.zenkey.sdk.internal.model.error.OAuth2Error
import com.xci.zenkey.sdk.internal.model.error.OIDCError
import com.xci.zenkey.sdk.internal.model.error.ZenKeyError
import com.xci.zenkey.sdk.internal.model.exception.AssetsNotFoundException
import com.xci.zenkey.sdk.internal.network.stack.HttpException
import org.json.JSONException
import org.json.JSONObject
import javax.net.ssl.SSLException

internal class AuthorizationResponseFactory
    : AuthorizationResponse.Factory {

    override fun create(mcc_mnc: String, request: AuthorizationRequest, uri: Uri)
            : AuthorizationResponse {
        return if (uri.containError) {
            create(mcc_mnc, request.redirectUri, createError(uri.error, uri.errorDescription))
        } else if (uri.containCode) {
            if (!request.state.isMatching(uri.state)) {
                create(mcc_mnc, request.redirectUri, STATE_MISMATCHED)
            } else {
                AuthorizationResponse(mcc_mnc, request, uri.code!!)
            }
        } else {
            create(mcc_mnc, request.redirectUri, UNKNOWN)
        }
    }

    override fun create(mcc_mnc: String?, redirectUri: Uri, throwable: Throwable)
            : AuthorizationResponse {
        return when (throwable) {
            is AssetsNotFoundException -> create(mcc_mnc, redirectUri, DISCOVERY_STATE.withDescription(throwable.message))
            is HttpException -> create(mcc_mnc, redirectUri, createError(throwable))
            is SSLException -> create(mcc_mnc, redirectUri, DISCOVERY_STATE
                    .withDescription(
                            "Your device doesn't support TLS 1.2 " +
                                    "which is required for ZenKey to work." +
                                    " Please make sure the Play Services are available " +
                                    "and the Play Services updates are installed."))
            else -> create(mcc_mnc, redirectUri, UNKNOWN.withDescription(throwable.message))
        }
    }

    override fun create(mcc_mnc: String?, redirectUri: Uri, error: AuthorizationError)
            : AuthorizationResponse {
        return AuthorizationResponse(mcc_mnc, redirectUri, error)
    }

    internal fun createError(exception: HttpException)
            : AuthorizationError {
        Logger.get().exception(exception)
        try {
            val jsonObject = JSONObject(exception.body)
            if (jsonObject.has(Json.KEY_ERROR)) {
                val error = jsonObject.getString(Json.KEY_ERROR)
                var errorDescription: String? = null
                if (jsonObject.has(Json.KEY_ERROR_DESCRIPTION)) {
                    errorDescription = jsonObject.getString(Json.KEY_ERROR_DESCRIPTION)
                }
                return createError(error, errorDescription)
            }
            return UNKNOWN
        } catch (e: JSONException) {
            e.printStackTrace()
            return UNKNOWN.withDescription("org.json.JSONException: " + e.message)
        }
    }

    internal fun createError(error: String?, description: String?)
            : AuthorizationError {
        Logger.get().e("AuthorizationError: $error $description")
        val exposedError: AuthorizationError
        var enumError: Enum<*>? = null
        if (error == null) {
            exposedError = UNKNOWN
        } else if ({ enumError = OAuth2Error.fromValue(error, description); enumError }() != null) {
            exposedError = (enumError as OAuth2Error).asExposed()
        } else if ({ enumError = OIDCError.fromValue(error, description); enumError }() != null) {
            exposedError = (enumError as OIDCError).asExposed()
        } else if ({ enumError = ZenKeyError.fromValue(error, description); enumError }() != null) {
            exposedError = (enumError as ZenKeyError).asExposed()
        } else {
            exposedError = UNKNOWN
        }
        Logger.get().e("Exposed as: $exposedError")
        return exposedError
    }
}
