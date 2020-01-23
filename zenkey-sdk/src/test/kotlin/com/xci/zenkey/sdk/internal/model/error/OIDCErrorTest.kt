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
class OIDCErrorTest {

    @Test
    fun shouldUseExpectedValues() {
        assertEquals(INTERACTION_REQUIRED, OIDCError.INTERACTION_REQUIRED.error)
        assertEquals(LOGIN_REQUIRED, OIDCError.LOGIN_REQUIRED.error)
        assertEquals(ACCOUNT_SELECTION_REQUIRED, OIDCError.ACCOUNT_SELECTION_REQUIRED.error)
        assertEquals(CONSENT_REQUIRED, OIDCError.CONSENT_REQUIRED.error)
        assertEquals(INVALID_REQUEST_URI, OIDCError.INVALID_REQUEST_URI.error)
        assertEquals(INVALID_REQUEST_OBJECT, OIDCError.INVALID_REQUEST_OBJECT.error)
        assertEquals(REQUEST_NOT_SUPPORTED, OIDCError.REQUEST_NOT_SUPPORTED.error)
        assertEquals(REQUEST_URI_NOT_SUPPORTED, OIDCError.REQUEST_URI_NOT_SUPPORTED.error)
        assertEquals(REGISTRATION_NOT_SUPPORTED, OIDCError.REGISTRATION_NOT_SUPPORTED.error)
    }

    @Test
    fun shouldGetInteractionRequired() {
        val error = OIDCError.fromValue(INTERACTION_REQUIRED, "")
        assertEquals(OIDCError.INTERACTION_REQUIRED, error)
    }

    @Test
    fun shouldGetLoginRequired() {
        val error = OIDCError.fromValue(LOGIN_REQUIRED, "")
        assertEquals(OIDCError.LOGIN_REQUIRED, error)
    }

    @Test
    fun shouldGetAccountSelectionRequired() {
        val error = OIDCError.fromValue(ACCOUNT_SELECTION_REQUIRED, "")
        assertEquals(OIDCError.ACCOUNT_SELECTION_REQUIRED, error)
    }

    @Test
    fun shouldGetConsentRequired() {
        val error = OIDCError.fromValue(CONSENT_REQUIRED, "")
        assertEquals(OIDCError.CONSENT_REQUIRED, error)
    }

    @Test
    fun shouldGetInvalidRequestUri() {
        val error = OIDCError.fromValue(INVALID_REQUEST_URI, "")
        assertEquals(OIDCError.INVALID_REQUEST_URI, error)
    }

    @Test
    fun shouldGetInvalidRequestObject() {
        val error = OIDCError.fromValue(INVALID_REQUEST_OBJECT, "")
        assertEquals(OIDCError.INVALID_REQUEST_OBJECT, error)
    }

    @Test
    fun shouldGetRequestNotSupported() {
        val error = OIDCError.fromValue(REQUEST_NOT_SUPPORTED, "")
        assertEquals(OIDCError.REQUEST_NOT_SUPPORTED, error)
    }

    @Test
    fun shouldGetRequestUriNotSupported() {
        val error = OIDCError.fromValue(REQUEST_URI_NOT_SUPPORTED, "")
        assertEquals(OIDCError.REQUEST_URI_NOT_SUPPORTED, error)
    }

    @Test
    fun shouldGetRegistrationNotSupported() {
        val error = OIDCError.fromValue(REGISTRATION_NOT_SUPPORTED, "")
        assertEquals(OIDCError.REGISTRATION_NOT_SUPPORTED, error)
    }

    @Test
    fun shouldGetNUll() {
        assertNull(OIDCError.fromValue("any", ""))
    }

    @Test
    fun shouldGetDescription() {
        val error = OIDCError.ACCOUNT_SELECTION_REQUIRED.withDescription(DESCRIPTION)
        assertEquals(DESCRIPTION, error.description)
    }

    @Test
    fun shouldGetError() {
        assertEquals(ACCOUNT_SELECTION_REQUIRED, OIDCError.ACCOUNT_SELECTION_REQUIRED.error)
    }

    @Test
    fun shouldGetExposedError() {
        assertEquals(AuthorizationError.INVALID_REQUEST, OIDCError.INVALID_REQUEST_OBJECT.asExposed())
        assertEquals(AuthorizationError.UNKNOWN, OIDCError.INTERACTION_REQUIRED.asExposed())
        assertEquals(AuthorizationError.UNKNOWN, OIDCError.LOGIN_REQUIRED.asExposed())
        assertEquals(AuthorizationError.UNKNOWN, OIDCError.ACCOUNT_SELECTION_REQUIRED.asExposed())
        assertEquals(AuthorizationError.UNKNOWN, OIDCError.CONSENT_REQUIRED.asExposed())
        assertEquals(AuthorizationError.UNKNOWN, OIDCError.INVALID_REQUEST_URI.asExposed())
        assertEquals(AuthorizationError.UNKNOWN, OIDCError.REQUEST_NOT_SUPPORTED.asExposed())
        assertEquals(AuthorizationError.UNKNOWN, OIDCError.REQUEST_URI_NOT_SUPPORTED.asExposed())
        assertEquals(AuthorizationError.UNKNOWN, OIDCError.REGISTRATION_NOT_SUPPORTED.asExposed())
    }

    companion object {
        private const val INTERACTION_REQUIRED = "interaction_required"
        private const val LOGIN_REQUIRED = "login_required"
        private const val ACCOUNT_SELECTION_REQUIRED = "account_selection_required"
        private const val CONSENT_REQUIRED = "consent_required"
        private const val INVALID_REQUEST_URI = "invalid_request_uri"
        private const val INVALID_REQUEST_OBJECT = "invalid_request_object"
        private const val REQUEST_NOT_SUPPORTED = "request_not_supported"
        private const val REQUEST_URI_NOT_SUPPORTED = "request_uri_not_supported"
        private const val REGISTRATION_NOT_SUPPORTED = "registration_not_supported"
        private const val DESCRIPTION = "DESCRIPTION"
    }
}
