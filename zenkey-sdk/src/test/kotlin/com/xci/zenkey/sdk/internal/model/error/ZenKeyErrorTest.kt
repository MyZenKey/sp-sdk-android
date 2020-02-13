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
package com.xci.zenkey.sdk.internal.model.error

import androidx.test.filters.SmallTest

import com.xci.zenkey.sdk.AuthorizationError

import org.junit.Test

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

@SmallTest
class ZenKeyErrorTest {

    @Test
    fun shouldUseExpectedValues() {
        assertEquals(USER_NOT_FOUND, ZenKeyError.USER_NOT_FOUND.error)
        assertEquals(INVALID_LOGIN_HINT_TOKEN, ZenKeyError.INVALID_LOGIN_HINT_TOKEN.error)
        assertEquals(INVALID_LOGIN_HINT, ZenKeyError.INVALID_LOGIN_HINT.error)
        assertEquals(AUTHENTICATION_TIMED_OUT, ZenKeyError.AUTHENTICATION_TIMED_OUT.error)
        assertEquals(DEVICE_UNAVAILABLE, ZenKeyError.DEVICE_UNAVAILABLE.error)
        assertEquals(NETWORK_FAILURE, ZenKeyError.NETWORK_FAILURE.error)
        assertEquals(USER_UNSUPPORTED, ZenKeyError.USER_UNSUPPORTED.error)
        assertEquals(UNKNOWN_ERROR, ZenKeyError.UNKNOWN_ERROR.error)
    }

    @Test
    fun shouldGetUserNotFound() {
        val error = ZenKeyError.fromValue(USER_NOT_FOUND, "")
        assertEquals(ZenKeyError.USER_NOT_FOUND, error)
    }

    @Test
    fun shouldGetInvalidLoginHintToken() {
        val error = ZenKeyError.fromValue(INVALID_LOGIN_HINT_TOKEN, "")
        assertEquals(ZenKeyError.INVALID_LOGIN_HINT_TOKEN, error)
    }

    @Test
    fun shouldGetInvalidLoginHint() {
        val error = ZenKeyError.fromValue(INVALID_LOGIN_HINT, "")
        assertEquals(ZenKeyError.INVALID_LOGIN_HINT, error)
    }

    @Test
    fun shouldGetAuthenticationTimedOut() {
        val error = ZenKeyError.fromValue(AUTHENTICATION_TIMED_OUT, "")
        assertEquals(ZenKeyError.AUTHENTICATION_TIMED_OUT, error)
    }

    @Test
    fun shouldGetDeviceUnavailable() {
        val error = ZenKeyError.fromValue(DEVICE_UNAVAILABLE, "")
        assertEquals(ZenKeyError.DEVICE_UNAVAILABLE, error)
    }

    @Test
    fun shouldGetNetworkFailure() {
        val error = ZenKeyError.fromValue(NETWORK_FAILURE, "")
        assertEquals(ZenKeyError.NETWORK_FAILURE, error)
    }

    @Test
    fun shouldGetUnknownError() {
        val error = ZenKeyError.fromValue(UNKNOWN_ERROR, "")
        assertEquals(ZenKeyError.UNKNOWN_ERROR, error)
    }

    @Test
    fun shouldGetNUll() {
        assertNull(ZenKeyError.fromValue("any", ""))
    }

    @Test
    fun shouldGetDescription() {
        val error = ZenKeyError.USER_NOT_FOUND.withDescription(DESCRIPTION)
        assertEquals(DESCRIPTION, error.description)
    }

    @Test
    fun shouldGetError() {
        assertEquals(USER_NOT_FOUND, ZenKeyError.USER_NOT_FOUND.error)
    }

    @Test
    fun shouldGetErrorAsExposed() {
        val userNotFound = ZenKeyError.USER_NOT_FOUND.asExposed()

        assertEquals(AuthorizationError.DISCOVERY_STATE, userNotFound)
        assertEquals(ZenKeyError.USER_NOT_FOUND.error, userNotFound.description)


        assertEquals(AuthorizationError.DISCOVERY_STATE, ZenKeyError.INVALID_LOGIN_HINT_TOKEN.asExposed())
        assertEquals(AuthorizationError.DISCOVERY_STATE, ZenKeyError.INVALID_LOGIN_HINT.asExposed())
        assertEquals(AuthorizationError.REQUEST_TIMEOUT, ZenKeyError.AUTHENTICATION_TIMED_OUT.asExposed())
        assertEquals(AuthorizationError.REQUEST_DENIED, ZenKeyError.DEVICE_UNAVAILABLE.asExposed())
        assertEquals(AuthorizationError.NETWORK_FAILURE, ZenKeyError.NETWORK_FAILURE.asExposed())
        assertEquals(AuthorizationError.DISCOVERY_STATE, ZenKeyError.USER_UNSUPPORTED.asExposed())
        assertEquals(AuthorizationError.UNKNOWN, ZenKeyError.UNKNOWN_ERROR.asExposed())
    }

    companion object {
        private const val USER_NOT_FOUND = "user_not_found"
        private const val INVALID_LOGIN_HINT_TOKEN = "invalid_login_hint_token"
        private const val INVALID_LOGIN_HINT = "invalid_login_hint"
        private const val AUTHENTICATION_TIMED_OUT = "authentication_timed_out"
        private const val DEVICE_UNAVAILABLE = "device_unavailable"
        private const val NETWORK_FAILURE = "network_failure"
        private const val USER_UNSUPPORTED = "user_unsupported"

        private const val UNKNOWN_ERROR = ""
        private const val DESCRIPTION = "DESCRIPTION"
    }
}