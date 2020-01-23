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
import java.security.SecureRandom
import java.util.*

private const val DEFAULT_LENGTH = 16
private const val MX_VALUE = 0xFF
private const val DEFAULT_EMPTY_CHAR = "0"

internal fun ByteArray.hash(
        separator: String? = null,
        upperCase: Boolean = false
): String {
    val hash = StringBuilder()
    for (aPublicKeyByte in this) {
        val hexValue = Integer.toHexString(MX_VALUE and aPublicKeyByte.toInt())
        if (hexValue.length == 1) hash.append(DEFAULT_EMPTY_CHAR)
        hash.append(if(upperCase) hexValue.toUpperCase(Locale.getDefault()) else hexValue)
        separator?.let { hash.append(it) }
    }
    return separator?.let { hash.deleteCharAt(hash.length - 1).toString() } ?: hash.toString()
}

internal fun random(length: Int = DEFAULT_LENGTH): ByteArray {
    val sr = SecureRandom()
    val random = ByteArray(length)
    sr.nextBytes(random)
    return random
}

internal fun ByteArray.encodeToString(flags: Int = Base64.DEFAULT): String {
    return Base64.encodeToString(this, flags)
}