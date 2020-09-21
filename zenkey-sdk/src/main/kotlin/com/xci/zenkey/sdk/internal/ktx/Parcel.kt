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

import android.os.Parcel

private const val NULL_VALUE = 0
private const val NON_NULL_VALUE = 1

internal fun Parcel.readNullableString(
): String? {
    return if (readInt() == NON_NULL_VALUE) {
        readString()
    } else {
        null
    }
}

internal fun Parcel.writeNullableString(
        value: String?
){
    if (value != null) {
        writeInt(NON_NULL_VALUE)
        writeString(value)
    } else {
        writeInt(NULL_VALUE)
    }
}