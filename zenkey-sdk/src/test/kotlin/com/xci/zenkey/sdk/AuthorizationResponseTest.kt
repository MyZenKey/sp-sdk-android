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
package com.xci.zenkey.sdk

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import androidx.test.filters.SmallTest
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.xci.zenkey.sdk.internal.model.AuthorizationRequest
import com.xci.zenkey.sdk.internal.model.ProofKeyForCodeExchange
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@SmallTest
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AuthorizationResponseTest {

    private val error = AuthorizationError.UNKNOWN
    private val mockRequest = mock<AuthorizationRequest>()
    private val mockPKCEChallenge = mock<ProofKeyForCodeExchange>()
    private lateinit var successResponse: AuthorizationResponse
    private lateinit var failedResponse: AuthorizationResponse

    @Before
    fun setUp() {
        whenever(mockPKCEChallenge.codeVerifier).thenReturn(CODE_VERIFIER)
        whenever(mockRequest.proofKeyForCodeExchange).thenReturn(mockPKCEChallenge)
        whenever(mockRequest.redirectUri).thenReturn(REDIRECT_URI)
        successResponse = AuthorizationResponse(MCC_MNC, mockRequest, AUTHORIZATION_CODE)
        failedResponse = AuthorizationResponse(MCC_MNC, REDIRECT_URI, error)
    }

    @Test
    fun shouldReadSuccessResponseFromParcel() {
        val parcel = Parcel.obtain()
        successResponse.writeToParcel(parcel, successResponse.describeContents())

        parcel.setDataPosition(0)

        val createdFromParcel = AuthorizationResponse.CREATOR.createFromParcel(parcel)

        assertEquals(MCC_MNC, createdFromParcel.mccMnc)
        assertEquals(AUTHORIZATION_CODE, createdFromParcel.authorizationCode)
        assertEquals(REDIRECT_URI, createdFromParcel.redirectUri)
        assertEquals(CODE_VERIFIER, createdFromParcel.codeVerifier)
        assertNull(createdFromParcel.error)

        val array = AuthorizationResponse.CREATOR.newArray(2)
        assertEquals(2, array.size.toLong())
    }

    @Test
    fun shouldReadFailedResponseFromParcel() {
        val parcel = Parcel.obtain()
        failedResponse.writeToParcel(parcel, failedResponse.describeContents())

        parcel.setDataPosition(0)

        val createdFromParcel = AuthorizationResponse.CREATOR.createFromParcel(parcel)

        assertEquals(MCC_MNC, createdFromParcel.mccMnc)
        assertEquals(error, createdFromParcel.error)
        assertEquals(REDIRECT_URI, createdFromParcel.redirectUri)
        assertNull(createdFromParcel.authorizationCode)

        val array = AuthorizationResponse.CREATOR.newArray(2)
        assertEquals(2, array.size.toLong())
    }

    @Test
    fun shouldExtractSuccessResponseFromIntent() {
        val intent = Intent()
        val bundle = Bundle()
        bundle.putParcelable(AuthorizationResponse.EXTRA_AUTH_RESPONSE, successResponse)
        intent.putExtra(AuthorizationResponse.EXTRA_AUTH_RESPONSE, bundle)

        val fromIntent = AuthorizationResponse.fromIntent(intent)!!

        assertNotNull(fromIntent)

        assertEquals(MCC_MNC, fromIntent.mccMnc)
        assertEquals(AUTHORIZATION_CODE, fromIntent.authorizationCode)
        assertEquals(CODE_VERIFIER, fromIntent.codeVerifier)
        assertNull(fromIntent.error)
    }

    @Test
    fun shouldExtractFailedResponseFromIntent() {
        val intent = Intent()
        val bundle = Bundle()
        bundle.putParcelable(AuthorizationResponse.EXTRA_AUTH_RESPONSE, failedResponse)
        intent.putExtra(AuthorizationResponse.EXTRA_AUTH_RESPONSE, bundle)

        val fromIntent = AuthorizationResponse.fromIntent(intent)!!

        assertNotNull(fromIntent)

        assertEquals(MCC_MNC, fromIntent.mccMnc)
        assertEquals(error, fromIntent.error)
        assertNull(fromIntent.authorizationCode)
    }

    @Test
    fun shouldReturnNullIfNoResponseInIntent() {
        var fromIntent = AuthorizationResponse.fromIntent(Intent())
        assertNull(fromIntent)

        fromIntent = AuthorizationResponse.fromIntent(Intent().putExtras(Bundle()))
        assertNull(fromIntent)
    }

    @Test
    fun shouldGetIntentContainingResponse() {
        val authorizationResponse = AuthorizationResponse(MCC_MNC, mockRequest, AUTHORIZATION_CODE)
        val intent = authorizationResponse.toIntent()
        assertNotNull(intent)
        val extra = intent.extras!!
        assertNotNull(extra)
        assertTrue(extra.containsKey(AuthorizationResponse.EXTRA_AUTH_RESPONSE))
        val bundle = extra.getBundle(AuthorizationResponse.EXTRA_AUTH_RESPONSE)!!
        assertNotNull(bundle)
        assertEquals(authorizationResponse, bundle.get(AuthorizationResponse.EXTRA_AUTH_RESPONSE))
    }

    @Test
    fun shouldBeSuccessful() {
        val authorizationResponse = AuthorizationResponse(MCC_MNC, mockRequest, AUTHORIZATION_CODE)
        assertTrue(authorizationResponse.isSuccessful)
    }

    @Test
    fun shouldNotBeSuccessful() {
        val authorizationResponse = AuthorizationResponse(MCC_MNC, REDIRECT_URI, error)
        assertFalse(authorizationResponse.isSuccessful)
    }

    companion object {
        private const val MCC_MNC = "MCCMNC"
        private const val AUTHORIZATION_CODE = "1234"
        private const val CODE_VERIFIER = "qwertyuiopasdfghjklzxcvbnm"
        private val REDIRECT_URI = Uri.EMPTY
    }

    /*@Test
    public void shouldGetResponseFromError() {

        AuthorizationResponse authorizationResponse = AuthorizationResponse.fromError(MCC_MNC, AuthorizationError.UNKNOWN);

        assertNotNull(authorizationResponse);
        assertFalse(authorizationResponse.isSuccessful());
        assertNotNull(authorizationResponse.getError());
        assertEquals(AuthorizationError.UNKNOWN, authorizationResponse.getError());
    }*/
}


