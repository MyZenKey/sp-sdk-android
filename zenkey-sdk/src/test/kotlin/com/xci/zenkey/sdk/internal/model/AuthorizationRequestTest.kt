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
package com.xci.zenkey.sdk.internal.model

import android.net.Uri
import android.os.Parcel

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.xci.zenkey.sdk.internal.ktx.*

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

@SmallTest
@RunWith(AndroidJUnit4::class)
class AuthorizationRequestTest {

    private lateinit var request: AuthorizationRequest

    @Before
    fun setUp() {
        request = AuthorizationRequest(CLIENT_ID, REDIRECT, SCOPE, STATE, ACR, NONCE, PROMPT, CORRELATION_ID, CONTEXT, PKCE_CHALLENGE)
    }

    @Test
    fun shouldReadRequestFromParcel() {
        val parcel = Parcel.obtain()
        request.writeToParcel(parcel, request.describeContents())

        parcel.setDataPosition(0)

        val createdFromParcel = AuthorizationRequest.CREATOR.createFromParcel(parcel)

        assertEquals(CLIENT_ID, createdFromParcel.clientId)
        assertEquals(REDIRECT, createdFromParcel.redirectUri)
        assertEquals(CODE_CHALLENGE, createdFromParcel.proofKeyForCodeExchange.codeChallenge)
        assertEquals(CODE_CHALLENGE_METHOD, createdFromParcel.proofKeyForCodeExchange.codeChallengeMethod)
        assertEquals(CODE_VERIFIER, createdFromParcel.proofKeyForCodeExchange.codeVerifier)
        assertEquals(SCOPE, createdFromParcel.scope)
        assertEquals(STATE, createdFromParcel.state)
        assertEquals(ACR, createdFromParcel.acr)
        assertEquals(NONCE, createdFromParcel.nonce)
        assertEquals(PROMPT, createdFromParcel.prompt)
        assertEquals(CORRELATION_ID, createdFromParcel.correlationId)
        assertEquals(CONTEXT, createdFromParcel.context)

        val array = AuthorizationRequest.CREATOR.newArray(2)
        assertEquals(2, array.size.toLong())
    }

    @Test
    fun shouldReadRequestFromParcelWithoutOptionalValues() {
        request = AuthorizationRequest(CLIENT_ID,
                REDIRECT, null, null, null, null, null, null, null, PKCE_CHALLENGE)
        val parcel = Parcel.obtain()
        request.writeToParcel(parcel, request.describeContents())

        parcel.setDataPosition(0)

        val createdFromParcel = AuthorizationRequest.CREATOR.createFromParcel(parcel)

        assertEquals(CLIENT_ID, createdFromParcel.clientId)
        assertEquals(REDIRECT, createdFromParcel.redirectUri)
        assertEquals(CODE_CHALLENGE, createdFromParcel.proofKeyForCodeExchange.codeChallenge)
        assertEquals(CODE_CHALLENGE_METHOD, createdFromParcel.proofKeyForCodeExchange.codeChallengeMethod)
        assertEquals(CODE_VERIFIER, createdFromParcel.proofKeyForCodeExchange.codeVerifier)
        assertNull(createdFromParcel.scope)
        assertNull(createdFromParcel.state)
        assertNull(createdFromParcel.acr)
        assertNull(createdFromParcel.nonce)
        assertNull(createdFromParcel.prompt)
        assertNull(createdFromParcel.correlationId)
        assertNull(createdFromParcel.context)

        val array = AuthorizationRequest.CREATOR.newArray(2)
        assertEquals(2, array.size.toLong())
    }

    @Test
    fun shouldCreateUriFromRequest() {
        val uri = AuthorizationRequest(CLIENT_ID, REDIRECT, SCOPE, STATE, ACR, NONCE, PROMPT, CORRELATION_ID, CONTEXT, PKCE_CHALLENGE)
                .toAuthorizationUri(ENDPOINT)

        assertEquals(SCHEME, uri.scheme)
        assertEquals(AUTHORITY, uri.authority)
        assertEquals(PATH, uri.pathSegments[0])
        assertEquals(CLIENT_ID, uri.clientId)
        assertEquals(REDIRECT.toString(), uri.redirectUri)
        assertEquals(CODE_CHALLENGE, uri.codeChallenge)
        assertEquals(CODE_CHALLENGE_METHOD.value, uri.codeChallengeMethod)
        assertEquals(SCOPE, uri.scope)
        assertEquals(STATE, uri.state)
        assertEquals(ACR, uri.acr)
        assertEquals(NONCE, uri.nonce)
        assertEquals(PROMPT, uri.prompt)
        assertEquals(CORRELATION_ID, uri.correlationId)
        assertEquals(CONTEXT, uri.context)
    }

    companion object {
        private const val CLIENT_ID = "CLIENT_ID"
        private const val SCOPE = "SCOPE"
        private const val STATE = "STATE"
        private const val ACR = "ACR"
        private const val NONCE = "NONCE"
        private const val CORRELATION_ID = "CORRELATION_ID"
        private const val PROMPT = "PROMPT"
        private const val CONTEXT = "CONTEXT"
        private const val SCHEME = "https"
        private const val AUTHORITY = "example.com"
        private const val PATH = "test"
        private const val CODE_CHALLENGE = "codeChallenge"
        private const val CODE_VERIFIER = "codeVerifier"
        private const val ENDPOINT = "${SCHEME}://${AUTHORITY}/${PATH}"
        private val CODE_CHALLENGE_METHOD = CodeChallengeMethod.PLAIN
        private val REDIRECT = Uri.parse("app://redirect")
        private val PKCE_CHALLENGE: ProofKeyForCodeExchange = ProofKeyForCodeExchange(CODE_VERIFIER, CODE_CHALLENGE, CODE_CHALLENGE_METHOD)
    }
}
