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

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.security.MessageDigest

@RunWith(AndroidJUnit4::class)
@SmallTest
class MessageDigestTest {

    @Test
    fun `should get plain PKCE challenge if MessageDigest is null`() {
        val digest: MessageDigest? = null
        val codeVerifier = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk"

        val PKCE = digest.createProofKeyForCodeExchange(codeVerifier)

        assertEquals(codeVerifier, PKCE.codeChallenge)
        assertEquals(CODE_CHALLENGE_METHOD_PLAIN, PKCE.codeChallengeMethod)
    }

    @Test
    fun `should build proper PKCE challenge with SHA-256 algorithm`() {
        val codeVerifier = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk"

        val PKCE = MessageDigest.getInstance("SHA-256").createProofKeyForCodeExchange(codeVerifier)

        assertEquals("E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM", PKCE.codeChallenge)
        assertEquals(CODE_CHALLENGE_METHOD_SHA_256, PKCE.codeChallengeMethod)
    }

    @Test
    fun `should build proper PKCE challenge without SHA-256 algorithm`() {
        val codeVerifier = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk"

        val PKCE = MessageDigest.getInstance("SHA-1").createProofKeyForCodeExchange(codeVerifier)

        assertEquals(codeVerifier, PKCE.codeChallenge)
        assertEquals(CODE_CHALLENGE_METHOD_PLAIN, PKCE.codeChallengeMethod)
    }
}