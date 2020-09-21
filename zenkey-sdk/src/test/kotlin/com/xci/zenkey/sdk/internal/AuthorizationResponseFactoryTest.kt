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
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.xci.zenkey.sdk.AuthorizationError
import com.xci.zenkey.sdk.internal.ktx.code
import com.xci.zenkey.sdk.internal.ktx.error
import com.xci.zenkey.sdk.internal.ktx.state
import com.xci.zenkey.sdk.internal.model.AuthorizationRequest
import com.xci.zenkey.sdk.internal.model.ProofKeyForCodeExchange
import com.xci.zenkey.sdk.internal.model.error.OIDCError
import com.xci.zenkey.sdk.internal.model.exception.AssetsNotFoundException
import com.xci.zenkey.sdk.internal.network.stack.HttpException
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthorizationResponseFactoryTest {

    private val mockUri = mock<Uri>()
    private val mockRequest = mock<AuthorizationRequest>()
    private val mockRedirectUri = mock<Uri>()
    private val mockPKCEChallenge = mock<ProofKeyForCodeExchange>()
    private val factory = AuthorizationResponseFactory()

    @Before
    fun setUp() {
        whenever(mockRequest.clientId).thenReturn(CLIENT_ID)
        whenever(mockRequest.redirectUri).thenReturn(mockRedirectUri)
    }

    @Test
    fun shouldGetSuccessfulResponse() {
        val code = "code"
        val state = "state"
        whenever(mockUri.queryParameterNames).thenReturn(setOf(Json.KEY_CODE, Json.KEY_STATE))
        whenever(mockUri.code).thenReturn(code)
        whenever(mockUri.state).thenReturn(state)
        whenever(mockRequest.state).thenReturn(state)
        whenever(mockPKCEChallenge.codeVerifier).thenReturn(CODE_VERIFIER)
        whenever(mockRequest.proofKeyForCodeExchange).thenReturn(mockPKCEChallenge)

        val response = factory.uri(MCC_MNC, mockRequest, mockUri)

        assertNotNull(response)
        assertEquals(MCC_MNC, response.mccMnc)
        assertEquals(CLIENT_ID, response.clientId)
        assertTrue(response.isSuccessful)
        assertEquals(code, response.authorizationCode)
        assertEquals(CODE_VERIFIER, response.codeVerifier)
    }

    @Test
    fun shouldGetInvalidRequestErrorResponse() {
        whenever(mockUri.queryParameterNames).thenReturn(setOf(Json.KEY_ERROR))
        whenever(mockUri.error).thenReturn(OIDCError.INVALID_REQUEST_OBJECT.error)

        val response = factory.uri(MCC_MNC, mockRequest, mockUri)

        assertNotNull(response)
        assertEquals(MCC_MNC, response.mccMnc)
        assertEquals(CLIENT_ID, response.clientId)
        assertFalse(response.isSuccessful)
        assertEquals(AuthorizationError.INVALID_REQUEST, response.error)
    }

    @Test
    fun shouldGetInvalidRequestErrorResponseIfUnexpectedState() {
        val code = "code"
        val state = "state"
        whenever(mockUri.queryParameterNames).thenReturn(setOf(Json.KEY_CODE, Json.KEY_STATE))
        whenever(mockUri.code).thenReturn(code)
        whenever(mockUri.state).thenReturn(state)

        val response = factory.uri(MCC_MNC, mockRequest, mockUri)

        assertNotNull(response)
        assertEquals(MCC_MNC, response.mccMnc)
        assertEquals(CLIENT_ID, response.clientId)
        assertFalse(response.isSuccessful)
        assertEquals(AuthorizationError.INVALID_REQUEST, response.error)
    }

    @Test
    fun shouldGetInvalidRequestErrorResponseIfStateMissMatch() {
        val code = "code"
        val stateRequest = "state_request"
        val stateResult = "state_result"
        whenever(mockUri.queryParameterNames).thenReturn(setOf(Json.KEY_CODE, Json.KEY_STATE))
        whenever(mockUri.code).thenReturn(code)
        whenever(mockUri.state).thenReturn(stateResult)
        whenever(mockRequest.state).thenReturn(stateRequest)

        val response = factory.uri(MCC_MNC, mockRequest, mockUri)

        assertNotNull(response)
        assertEquals(MCC_MNC, response.mccMnc)
        assertEquals(CLIENT_ID, response.clientId)
        assertFalse(response.isSuccessful)
        assertEquals(AuthorizationError.INVALID_REQUEST, response.error)
    }

    @Test
    fun shouldGetUnknownErrorResponse() {
        whenever(mockUri.queryParameterNames).thenReturn(emptySet())

        val response = factory.uri(MCC_MNC, mockRequest, mockUri)
        assertNotNull(response)
        assertEquals(MCC_MNC, response.mccMnc)
        assertEquals(CLIENT_ID, response.clientId)
        assertFalse(response.isSuccessful)
        assertEquals(AuthorizationError.UNKNOWN, response.error)
    }

    @Test
    fun shouldGetDiscoveryStateErrorResponseForAssetsNotFoundException() {
        val response = factory.throwable(MCC_MNC, mockRequest, AssetsNotFoundException("message"))

        assertNotNull(response)
        assertEquals(MCC_MNC, response.mccMnc)
        assertEquals(CLIENT_ID, response.clientId)
        assertFalse(response.isSuccessful)
        assertEquals(AuthorizationError.DISCOVERY_STATE, response.error)
    }

    @Test
    fun shouldGetInvalidConfigurationErrorResponseForHttpException() {
        val response = factory.throwable(MCC_MNC, mockRequest, HttpException(300, "{ \"error\":\"invalid_request\" }"))

        assertNotNull(response)
        assertEquals(MCC_MNC, response.mccMnc)
        assertEquals(CLIENT_ID, response.clientId)
        assertFalse(response.isSuccessful)
        assertEquals(AuthorizationError.INVALID_REQUEST, response.error)
    }

    @Test
    fun shouldGetUnknownErrorResponseForException() {
        val response = factory.throwable(MCC_MNC, mockRequest, Exception())

        assertNotNull(response)
        assertEquals(MCC_MNC, response.mccMnc)
        assertEquals(CLIENT_ID, response.clientId)
        assertFalse(response.isSuccessful)
        assertEquals(AuthorizationError.UNKNOWN, response.error)
    }

    @Test
    fun shouldGetUnknownAuthorizationErrorFromHttpExceptionIfJsonException() {
        val error = factory.createError(HttpException(0, ""))
        assertEquals(AuthorizationError.UNKNOWN, error)
    }

    @Test
    fun shouldGetUnknownAuthorizationErrorFromHttpExceptionIfNoErrorPresent() {
        val error = factory.createError(HttpException(0, "{}"))
        assertEquals(AuthorizationError.UNKNOWN, error)
    }

    @Test
    fun shouldGetAuthorizationErrorErrorFromHttpExceptionWithoutDescription() {
        val error = factory.createError(HttpException(0, "{\"error\":\"invalid_request\"}"))
        assertEquals(AuthorizationError.INVALID_REQUEST, error)
        assertNull(error.description)
    }

    @Test
    fun shouldGetAuthorizationErrorErrorFromHttpExceptionWithDescription() {
        val description = "description"
        val error = factory.createError(HttpException(0, "{\"error\":\"invalid_request\",\"error_description\":\"$description\"}"))
        assertEquals(AuthorizationError.INVALID_REQUEST, error)
        assertEquals(description, error.description)
    }

    @Test
    fun shouldGetAuthorizationErrorFromOAuth2Error() {
        assertEquals(AuthorizationError.INVALID_REQUEST,
                factory.createError("invalid_request", null))
    }

    @Test
    fun shouldGetAuthorizationErrorFromOIDCError() {
        val description = "description"
        assertEquals(AuthorizationError.INVALID_REQUEST.withDescription(description),
                factory.createError("invalid_request_object", description))
    }

    @Test
    fun shouldGetAuthorizationErrorFromVerifyError() {
        val description = "description"
        assertEquals(AuthorizationError.DISCOVERY_STATE.withDescription(description),
                factory.createError("user_not_found", description))
    }

    @Test
    fun shouldGetUnknownError() {
        val description = "description"
        assertEquals(AuthorizationError.UNKNOWN.withDescription(description),
                factory.createError("any", description))
    }

    companion object {
        private const val MCC_MNC = "MCC_MNC"
        private const val CLIENT_ID = "CLIENT_ID"
        private const val CODE_VERIFIER = "CODE_VERIFIER"
    }
}