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


import android.util.Base64
import com.xci.zenkey.sdk.internal.model.ProofKeyForCodeExchange
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

internal const val CODE_CHALLENGE_METHOD_SHA_256 = "S256"
internal const val CODE_CHALLENGE_METHOD_PLAIN = "plain"
internal const val SHA_256_MESSAGE_DIGEST_ALGORITHM = "SHA-256"

/**
 * Get instance of a SHA-256 MessageDigest
 */
internal val SHA256MessageDigest: MessageDigest?
    get() {
        return try {
            MessageDigest.getInstance(SHA_256_MESSAGE_DIGEST_ALGORITHM)
        } catch (e: NoSuchAlgorithmException){
            null
        }
    }

/**
 * Generate a ProofKeyForCodeExchange.
 */
internal val MessageDigest?.proofKeyForCodeExchange: ProofKeyForCodeExchange
    get() {
        return createProofKeyForCodeExchange(codeVerifier)
    }

/**
 * Generate a ProofKeyForCodeExchange.
 */
internal fun MessageDigest?.createProofKeyForCodeExchange(
        codeVerifier: String
): ProofKeyForCodeExchange = this?.let {
    if(algorithm == SHA_256_MESSAGE_DIGEST_ALGORITHM){
        update(codeVerifier.toByteArray())
        ProofKeyForCodeExchange(
                codeVerifier,
                digest().encodeToString(flags = Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING),
                CODE_CHALLENGE_METHOD_SHA_256)
    } else {
        codeVerifier.plainProofKeyForCodeExchange
    }
} ?: codeVerifier.plainProofKeyForCodeExchange
