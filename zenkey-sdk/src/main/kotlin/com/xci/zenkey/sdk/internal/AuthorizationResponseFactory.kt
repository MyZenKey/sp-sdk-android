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

    override fun uri(
            mcc_mnc: String,
            request: AuthorizationRequest,
            uri: Uri
    ): AuthorizationResponse =
            if (uri.containError) {
                error(mcc_mnc, request, createError(uri.error, uri.errorDescription))
            } else if (uri.containCode) {
                if (!request.state.isMatching(uri.state)) {
                    error(mcc_mnc, request, STATE_MISMATCHED)
                } else {
                    AuthorizationResponse.success(mcc_mnc, request, uri.code!!)
                }
            } else {
                error(mcc_mnc, request, UNKNOWN)
            }

    override fun throwable(
            mcc_mnc: String?,
            request: AuthorizationRequest,
            throwable: Throwable
    ): AuthorizationResponse =
            when (throwable) {
                is AssetsNotFoundException ->
                    error(mcc_mnc, request, DISCOVERY_STATE.withDescription(throwable.message))
                is HttpException ->
                    error(mcc_mnc, request, createError(throwable))
                is SSLException ->
                    error(mcc_mnc, request, DISCOVERY_STATE.withDescription(
                            "Your device doesn't support TLS 1.2 " +
                                    "which is required for ZenKey to work." +
                                    " Please make sure the Play Services are available " +
                                    "and the Play Services updates are installed."))
                else ->
                    error(mcc_mnc, request, UNKNOWN.withDescription(throwable.message))
        }

    override fun error(
            mcc_mnc: String?,
            request: AuthorizationRequest,
            error: AuthorizationError
    ): AuthorizationResponse = AuthorizationResponse.failure(mcc_mnc, request, error)

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
        when {
            error == null -> {
                exposedError = UNKNOWN.withDescription(description)
            }
            { enumError = OAuth2Error.fromValue(error, description); enumError }() != null -> {
                exposedError = (enumError as OAuth2Error).asExposed()
            }
            { enumError = OIDCError.fromValue(error, description); enumError }() != null -> {
                exposedError = (enumError as OIDCError).asExposed()
            }
            { enumError = ZenKeyError.fromValue(error, description); enumError }() != null -> {
                exposedError = (enumError as ZenKeyError).asExposed()
            }
            else -> {
                exposedError = UNKNOWN.withDescription(description)
            }
        }
        Logger.get().e("Exposed as: $exposedError")
        return exposedError
    }
}
