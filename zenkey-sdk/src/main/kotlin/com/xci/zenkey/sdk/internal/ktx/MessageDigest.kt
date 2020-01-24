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

package com.xci.zenkey.sdk.internal.ktx

import android.util.Base64
import com.xci.zenkey.sdk.internal.model.CodeChallengeMethod
import com.xci.zenkey.sdk.internal.model.AndroidMessageDigestAlgorithm
import com.xci.zenkey.sdk.internal.model.ProofKeyForCodeExchange
import java.security.MessageDigest

/**
 * Generate a ProofKeyForCodeExchange.
 */
internal val MessageDigest.proofKeyForCodeExchange: ProofKeyForCodeExchange
    get() {
        val codeVerifier = codeVerifier
        val codeChallenge: String
        val codeChallengeMethod: CodeChallengeMethod
        if(algorithm == AndroidMessageDigestAlgorithm.SHA_256.value){
            update(codeVerifier.toByteArray())
            codeChallenge = digest().encodeToString(flags = Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            codeChallengeMethod = CodeChallengeMethod.SHA_256
        } else {
            codeChallenge = codeVerifier.encodeToString(flags = Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            codeChallengeMethod = CodeChallengeMethod.PLAIN
        }
        return ProofKeyForCodeExchange(codeVerifier, codeChallenge, codeChallengeMethod)
    }
