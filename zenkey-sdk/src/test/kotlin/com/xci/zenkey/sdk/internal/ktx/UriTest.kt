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
package com.xci.zenkey.sdk.internal.ktx

import android.content.Intent
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.xci.zenkey.sdk.internal.Json
import com.xci.zenkey.sdk.internal.model.AuthorizationRequest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class UriTest {

    private val mockRequest = mock<AuthorizationRequest>()
    private val mockRedirectUri = mock<Uri>()

    @Test
    fun shouldGetLoginHintToken() {
        assertEquals(VALUE, Uri.parse(BASE_URI)
                .buildUpon()
                .appendQueryParameter(Json.KEY_LOGIN_HINT_TOKEN, VALUE)
                .build().loginHintToken)
    }

    @Test
    fun shouldNotGetLoginHintToken() {
        assertNull(Uri.parse(BASE_URI).loginHintToken)
    }

    @Test
    fun shouldGetState() {
        assertEquals(VALUE, Uri.parse(BASE_URI)
                .buildUpon()
                .appendQueryParameter(Json.KEY_STATE, VALUE)
                .build().state)
    }

    @Test
    fun shouldNotGetState() {
        assertNull(Uri.parse(BASE_URI).state)
    }

    @Test
    fun shouldGetMccMnc() {
        assertEquals(VALUE, Uri.parse(BASE_URI)
                .buildUpon()
                .appendQueryParameter(Json.KEY_MCC_MNC, VALUE)
                .build().mccMnc)
    }

    @Test
    fun shouldNotGetMccMnc() {
        assertNull(Uri.parse(BASE_URI).mccMnc)
    }

    @Test
    fun shouldGetCode() {
        assertEquals(VALUE, Uri.parse(BASE_URI)
                .buildUpon()
                .appendQueryParameter(Json.KEY_CODE, VALUE)
                .build().code)
    }

    @Test
    fun shouldNotGetCode() {
        assertNull(Uri.parse(BASE_URI).code)
    }

    @Test
    fun shouldGetError() {
        assertEquals(VALUE, Uri.parse(BASE_URI)
                .buildUpon()
                .appendQueryParameter(Json.KEY_ERROR, VALUE)
                .build().error)
    }

    @Test
    fun shouldNotGetError() {
        assertNull(Uri.parse(BASE_URI).error)
    }

    @Test
    fun shouldGetErrorDescription() {
        assertEquals(VALUE, Uri.parse(BASE_URI)
                .buildUpon()
                .appendQueryParameter(Json.KEY_ERROR_DESCRIPTION, VALUE)
                .build().errorDescription)
    }

    @Test
    fun shouldNotGetErrorDescription() {
        assertNull(Uri.parse(BASE_URI).errorDescription)
    }

    @Test
    fun shouldGetClientId() {
        assertEquals(VALUE, Uri.parse(BASE_URI)
                .buildUpon()
                .appendQueryParameter(Json.KEY_CLIENT_ID, VALUE)
                .build().clientId)
    }

    @Test
    fun shouldNotGetClientId() {
        assertNull(Uri.parse(BASE_URI).clientId)
    }

    @Test
    fun shouldGetRedirectUri() {
        assertEquals(VALUE, Uri.parse(BASE_URI)
                .buildUpon()
                .appendQueryParameter(Json.KEY_REDIRECT_URI, VALUE)
                .build().redirectUri)
    }

    @Test
    fun shouldNotGetRedirectUri() {
        assertNull(Uri.parse(BASE_URI).redirectUri)
    }

    @Test
    fun shouldGetScope() {
        assertEquals(VALUE, Uri.parse(BASE_URI)
                .buildUpon()
                .appendQueryParameter(Json.KEY_SCOPE, VALUE)
                .build().scope)
    }

    @Test
    fun shouldNotGetScope() {
        assertNull(Uri.parse(BASE_URI).scope)
    }

    @Test
    fun shouldGetACR() {
        assertEquals(VALUE, Uri.parse(BASE_URI)
                .buildUpon()
                .appendQueryParameter(Json.KEY_ACR_VALUES, VALUE)
                .build().acr)
    }

    @Test
    fun shouldNotGetACR() {
        assertNull(Uri.parse(BASE_URI).acr)
    }

    @Test
    fun shouldGetPrompt() {
        assertEquals(VALUE, Uri.parse(BASE_URI)
                .buildUpon()
                .appendQueryParameter(Json.KEY_PROMPT, VALUE)
                .build().prompt)
    }

    @Test
    fun shouldNotGetPrompt() {
        assertNull(Uri.parse(BASE_URI).prompt)
    }

    @Test
    fun shouldGetCorrelationId() {
        assertEquals(VALUE, Uri.parse(BASE_URI)
                .buildUpon()
                .appendQueryParameter(Json.KEY_CORRELATION_ID, VALUE)
                .build().correlationId)
    }

    @Test
    fun shouldNotGetCorrelationId() {
        assertNull(Uri.parse(BASE_URI).correlationId)
    }

    @Test
    fun shouldGetContext() {
        assertEquals(VALUE, Uri.parse(BASE_URI)
                .buildUpon()
                .appendQueryParameter(Json.KEY_CONTEXT, VALUE)
                .build().context)
    }

    @Test
    fun shouldNotGetContext() {
        assertNull(Uri.parse(BASE_URI).context)
    }

    @Test
    fun shouldGetNonce() {
        assertEquals(VALUE, Uri.parse(BASE_URI)
                .buildUpon()
                .appendQueryParameter(Json.KEY_NONCE, VALUE)
                .build().nonce)
    }

    @Test
    fun shouldNotGetNonce() {
        assertNull(Uri.parse(BASE_URI).nonce)
    }

    @Test
    fun shouldContainError() {
        assertTrue(Uri.parse(BASE_URI)
                .buildUpon()
                .appendQueryParameter(Json.KEY_ERROR, VALUE)
                .build().containError)
    }

    @Test
    fun shouldNotContainError() {
        assertFalse(Uri.parse(BASE_URI).containError)
    }

    @Test
    fun shouldContainCode() {
        assertTrue(Uri.parse(BASE_URI)
                .buildUpon()
                .appendQueryParameter(Json.KEY_CODE, VALUE)
                .build().containCode)
    }

    @Test
    fun shouldNotContainCode() {
        assertFalse(Uri.parse(BASE_URI).containCode)
    }

    @Test
    fun shouldBeUserNotFound() {
        assertTrue(Uri.parse(BASE_URI)
                .buildUpon()
                .appendQueryParameter("error", "user_not_found")
                .build().isUserNotFoundError)
    }

    @Test
    fun shouldNotBeUserNotFound() {
        assertFalse(Uri.parse(BASE_URI)
                .buildUpon()
                .appendQueryParameter("error", "any")
                .build().isUserNotFoundError)
    }

    @Test
    fun shouldNotBeUserNotFoundIfNoError() {
        assertFalse(Uri.parse(BASE_URI).buildUpon().build().isUserNotFoundError)
    }

    @Test
    fun stateShouldMatch() {
        val state = "state"
        whenever(mockRequest.state).thenReturn(state)
        assertFalse(Uri.parse(BASE_URI)
                .buildUpon()
                .appendQueryParameter(Json.KEY_STATE, state)
                .build().isNotMatchingStateIn(mockRequest))
    }

    @Test
    fun stateShouldNotMatchIfNullInRequest() {
        val state = "state"
        whenever(mockRequest.state).thenReturn(null)
        assertTrue(Uri.parse(BASE_URI)
                .buildUpon()
                .appendQueryParameter(Json.KEY_STATE, state)
                .build().isNotMatchingStateIn(mockRequest))
    }

    @Test
    fun stateShouldNotMatchIfDifferent() {
        whenever(mockRequest.state).thenReturn("value1")
        assertTrue(Uri.parse(BASE_URI)
                .buildUpon()
                .appendQueryParameter(Json.KEY_STATE, "value2")
                .build().isNotMatchingStateIn(mockRequest))
    }

    @Test
    fun shouldCreateDefaultIntentWithUri() {
        val uri = Uri.parse("https://test.xci.com/authorize")
        val intent = uri.intent
        assertNotNull(intent)
        assertTrue(intent.categories.contains(Intent.CATEGORY_DEFAULT))
        assertEquals(1, intent.categories.size.toLong())
        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals(uri, intent.data)
    }

    companion object {
        private const val BASE_URI = "http://example.com"
        private const val VALUE = "value"
    }
}