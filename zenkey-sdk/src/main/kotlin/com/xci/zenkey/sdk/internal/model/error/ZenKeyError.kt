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
import com.xci.zenkey.sdk.AuthorizationError.*

internal enum class ZenKeyError(
        val error: String
) {

    /**
     * This may be returned if the carrier does not support the user identity.
     * (this may be that the phone number is not currently on this carrier, or
     * that the subscriber ID is for a user that has ported out).
     * The RP should re-try discovery to locate this user.
     */
    USER_NOT_FOUND("user_not_found"),
    /**
     * Formatting is incorrect. Sp should try to use an extracted sub as login_hint.
     */
    INVALID_LOGIN_HINT_TOKEN("invalid_login_hint_token"),
    /**
     * The user indicated by the requested login_hint is not the user of the device.
     * Note: an otherwise invalid login_hint is not a fatal error except for server-initiated requests.
     */
    INVALID_LOGIN_HINT("invalid_login_hint"),
    /**
     * The user may not have access to their phone and therefore the transaction may have failed.
     * Or the user did not notice the request. This is applicable for server-initiated requests.
     * Once the Zenkey application is open with a user request, the application may be cycled out of the foreground and the user may return at an arbitrary delay.
     * NOTE: the request did make it to the device (state:delivered)
     */
    AUTHENTICATION_TIMED_OUT("authentication_timed_out"),
    /**
     * A server initiated request has been unavailable to be delivered to the device.
     * (state:!=delivered).
     * This may occur after the carrier has been unable retrying a push message to a device <x> times. Over <x> min.
     * An SP may attempt to retry at a later date.
     */
    DEVICE_UNAVAILABLE("device_unavailable"),
    /**
     * The users device has been unsuccessful with authentication.
     * This may happen if the user has changed sim cards, or just reset their device.
     * This may also happen if the users device is currently without a network connection though the discovery request should have been the initial call for the SP to discovery the network is unavailable.
     * An SP may retry authentication at a later date.
     */
    NETWORK_FAILURE("network_failure"),
    /**
     * A user that does not have the PV application,
     * and or has decided not to install the PV application.
     * This error is likely on Server initiated responses where the user does not have the app.
     * Or may occur if the user had PV App but then changed devices, or uninstalled the app.
     */
    USER_UNSUPPORTED("user_unsupported"),
    /**
     * The error isn't a known error.
     */
    UNKNOWN_ERROR("");

    var description: String? = null
        private set

    fun withDescription(description: String?): ZenKeyError {
        this.description = description
        return this
    }

    fun asExposed(): AuthorizationError {
        return createExposed(this)
    }

    companion object {

        fun fromValue(value: String, description: String?): ZenKeyError? {
            for (error in values()) {
                if (error.error == value)
                    return error.withDescription(description)
            }
            return null
        }

        @VisibleForTesting
        internal fun createExposed(error: ZenKeyError): AuthorizationError {
            return when(error){
                USER_NOT_FOUND -> DISCOVERY_STATE.withDescription(error.error)
                INVALID_LOGIN_HINT -> DISCOVERY_STATE.withDescription(error.description)
                DEVICE_UNAVAILABLE -> REQUEST_DENIED.withDescription(error.description)
                INVALID_LOGIN_HINT_TOKEN -> DISCOVERY_STATE.withDescription(error.description)
                USER_UNSUPPORTED -> DISCOVERY_STATE.withDescription(error.description)
                AUTHENTICATION_TIMED_OUT -> REQUEST_TIMEOUT.withDescription(error.description)
                NETWORK_FAILURE -> AuthorizationError.NETWORK_FAILURE.withDescription(error.description)
                UNKNOWN_ERROR -> UNKNOWN.withDescription(error.description)
            }
        }
    }
}
