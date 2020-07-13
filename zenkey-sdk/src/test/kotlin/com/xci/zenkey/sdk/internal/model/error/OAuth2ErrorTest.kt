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

import androidx.test.filters.SmallTest

import com.xci.zenkey.sdk.AuthorizationError

import org.junit.Test

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

@SmallTest
class OAuth2ErrorTest {

    @Test
    fun shouldUseExpectedValues() {
        assertEquals(INVALID_REQUEST, OAuth2Error.INVALID_REQUEST.error)
        assertEquals(UNAUTHORIZED_CLIENT, OAuth2Error.UNAUTHORIZED_CLIENT.error)
        assertEquals(UNSUPPORTED_RESPONSE_TYPE, OAuth2Error.UNSUPPORTED_RESPONSE_TYPE.error)
        assertEquals(INVALID_SCOPE, OAuth2Error.INVALID_SCOPE.error)
        assertEquals(ACCESS_DENIED, OAuth2Error.ACCESS_DENIED.error)
        assertEquals(TEMPORARILY_UNAVAILABLE, OAuth2Error.TEMPORARILY_UNAVAILABLE.error)
    }

    @Test
    fun shouldGetInvalidRequest() {
        val error = OAuth2Error.fromValue(INVALID_REQUEST, "")
        assertEquals(OAuth2Error.INVALID_REQUEST, error)
    }

    @Test
    fun shouldGetUnauthorizedClient() {
        val error = OAuth2Error.fromValue(UNAUTHORIZED_CLIENT, "")
        assertEquals(OAuth2Error.UNAUTHORIZED_CLIENT, error)
    }

    @Test
    fun shouldGetUnsupportedResponseType() {
        val error = OAuth2Error.fromValue(UNSUPPORTED_RESPONSE_TYPE, "")
        assertEquals(OAuth2Error.UNSUPPORTED_RESPONSE_TYPE, error)
    }

    @Test
    fun shouldGetInvalidScope() {
        val error = OAuth2Error.fromValue(INVALID_SCOPE, "")
        assertEquals(OAuth2Error.INVALID_SCOPE, error)
    }

    @Test
    fun shouldGetAccessDeniedError() {
        val error = OAuth2Error.fromValue(ACCESS_DENIED, "")
        assertEquals(OAuth2Error.ACCESS_DENIED, error)
    }

    @Test
    fun shouldTemporarilyUnavailable() {
        val error = OAuth2Error.fromValue(TEMPORARILY_UNAVAILABLE, "")
        assertEquals(OAuth2Error.TEMPORARILY_UNAVAILABLE, error)
    }

    @Test
    fun shouldGetNUll() {
        assertNull(OAuth2Error.fromValue("any", ""))
    }

    @Test
    fun shouldGetDescription() {
        val error = OAuth2Error.TEMPORARILY_UNAVAILABLE.withDescription(DESCRIPTION)
        assertEquals(DESCRIPTION, error.description)
    }

    @Test
    fun shouldGetError() {
        assertEquals(TEMPORARILY_UNAVAILABLE, OAuth2Error.TEMPORARILY_UNAVAILABLE.error)
    }

    @Test
    fun shouldGetErrorAsExposed() {
        assertEquals(AuthorizationError.INVALID_REQUEST, OAuth2Error.INVALID_REQUEST.asExposed())
        assertEquals(AuthorizationError.INVALID_CONFIGURATION, OAuth2Error.UNAUTHORIZED_CLIENT.asExposed())
        assertEquals(AuthorizationError.UNKNOWN, OAuth2Error.UNSUPPORTED_RESPONSE_TYPE.asExposed())
        assertEquals(AuthorizationError.INVALID_REQUEST, OAuth2Error.INVALID_SCOPE.asExposed())
        assertEquals(AuthorizationError.REQUEST_DENIED, OAuth2Error.ACCESS_DENIED.asExposed())
        assertEquals(AuthorizationError.SERVER_ERROR, OAuth2Error.TEMPORARILY_UNAVAILABLE.asExposed())
    }

    companion object {
        private const val INVALID_REQUEST = "invalid_request"
        private const val UNAUTHORIZED_CLIENT = "unauthorized_client"
        private const val UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type"
        private const val INVALID_SCOPE = "invalid_scope"
        private const val ACCESS_DENIED = "access_denied"
        private const val TEMPORARILY_UNAVAILABLE = "temporarily_unavailable"
        private const val DESCRIPTION = "DESCRIPTION"
    }
}