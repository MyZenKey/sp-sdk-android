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
package com.xci.zenkey.sdk.internal.model.error

import com.xci.zenkey.sdk.AuthorizationError

import com.xci.zenkey.sdk.AuthorizationError.INVALID_REQUEST
import com.xci.zenkey.sdk.AuthorizationError.UNKNOWN

internal enum class OIDCError(
        val error: String
) {

    /**
     * The RP should not be using display=none
     */
    INTERACTION_REQUIRED("interaction_required"),
    /**
     * The RP should not be using display=none
     */
    LOGIN_REQUIRED("login_required"),
    /**
     * This error should never be returned.
     */
    ACCOUNT_SELECTION_REQUIRED("account_selection_required"),
    /**
     * This error should never be returned.
     */
    CONSENT_REQUIRED("consent_required"),
    /**
     *
     */
    INVALID_REQUEST_URI("invalid_request_uri"),
    /**
     * This error may be returned while carriers are still adding support for
     * Request objects
     */
    INVALID_REQUEST_OBJECT("invalid_request_object"),
    /**
     * This error may be returned while carriers are still adding support for
     * Request objects
     */
    REQUEST_NOT_SUPPORTED("request_not_supported"),
    /**
     * Request URI’s won’t be supported.
     */
    REQUEST_URI_NOT_SUPPORTED("request_uri_not_supported"),
    /**
     * Dynamic registration will not be supported.
     */
    REGISTRATION_NOT_SUPPORTED("registration_not_supported");

    var description: String? = null
        private set

    fun withDescription(description: String?): OIDCError {
        this.description = description
        return this
    }

    fun asExposed(): AuthorizationError {
        return createExposedError(this)
    }

    companion object {

        fun fromValue(value: String, description: String?): OIDCError? {
            for (error in values()) {
                if (error.error == value)
                    return error.withDescription(description)
            }
            return null
        }

        internal fun createExposedError(oidcError: OIDCError): AuthorizationError {
            return if (oidcError == INVALID_REQUEST_OBJECT) {
                INVALID_REQUEST.withDescription(oidcError.description)
            } else UNKNOWN.withDescription(oidcError.description)
        }
    }
}
