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


package com.xci.zenkey.sdk.internal.ktx

import android.util.Base64
import java.nio.charset.Charset
import java.util.*

internal fun String.encodeToString(
        charset: Charset = Charsets.UTF_8,
        flags: Int = Base64.DEFAULT
): String {
    return toByteArray(charset).encodeToString(flags)
}

internal val codeVerifier: String
    get() {
        val candidateChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-._~"
        val sb = StringBuilder()
        val random = Random()
        for (i in 0 until 128) {
            sb.append(candidateChars[random.nextInt(candidateChars.length)])
        }
        return sb.toString()
    }