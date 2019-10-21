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
package com.xci.zenkey.sdk.internal.model.error

import android.support.annotation.VisibleForTesting

import com.xci.zenkey.sdk.AuthorizationError

internal enum class OAuth2Error(
        val error: String
) {

    /**
     * The RP has constructed a bad request. This may be due to any of the
     * parameters submitted. The RP should visit the service portal to confirm
     * the parameters they should use.
     */
    INVALID_REQUEST("invalid_request"),
    /**
     * The RP has constructed a bad request. This may be due to any of the
     * parameters submitted. The RP should visit the service portal to confirm
     * the parameters they should use.
     */
    UNAUTHORIZED_CLIENT("unauthorized_client"),
    /**
     * The user or carrier has denied the request.
     * Cases: user has denied context, user has canceled request
     */
    ACCESS_DENIED("access_denied"),
    /**
     * The RP has constructed a bad request. This may be due to any of the
     * parameters submitted. The RP should only use code or async-token.
     */
    UNSUPPORTED_RESPONSE_TYPE("unsupported_response_type"),
    /**
     * The RP has constructed a bad request. The RP should visit the service
     * portal to confirm the scopes allowed
     */
    INVALID_SCOPE("invalid_scope"),
    /**
     * There is a problem with the ZenKey solution or application.
     * An SP may assume that a retry at a later time may be successful.
     */
    TEMPORARILY_UNAVAILABLE("temporarily_unavailable");

    var description: String? = null
        private set

    fun withDescription(description: String?): OAuth2Error {
        this.description = description
        return this
    }

    fun asExposed(): AuthorizationError {
        return createExposed(this)
    }

    companion object {

        fun fromValue(value: String, description: String?): OAuth2Error? {
            for (error in values()) {
                if (error.error == value)
                    return error.withDescription(description)
            }
            return null
        }

        @VisibleForTesting
        internal fun createExposed(error: OAuth2Error): AuthorizationError {
            return when (error) {
                INVALID_REQUEST -> AuthorizationError.INVALID_CONFIGURATION.withDescription(error.description)
                UNAUTHORIZED_CLIENT -> AuthorizationError.INVALID_CONFIGURATION.withDescription(error.description)
                UNSUPPORTED_RESPONSE_TYPE -> AuthorizationError.UNKNOWN.withDescription(error.description)
                INVALID_SCOPE -> AuthorizationError.INVALID_REQUEST.withDescription(error.description)
                ACCESS_DENIED -> AuthorizationError.REQUEST_DENIED.withDescription(error.description)
                TEMPORARILY_UNAVAILABLE -> AuthorizationError.SERVER_ERROR.withDescription(error.description)
            }
        }
    }

}
