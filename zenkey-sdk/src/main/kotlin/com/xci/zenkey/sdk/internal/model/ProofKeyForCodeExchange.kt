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

package com.xci.zenkey.sdk.internal.model

import android.os.Parcel
import android.os.Parcelable

internal data class ProofKeyForCodeExchange(
        val codeVerifier: String,
        val codeChallenge: String,
        val codeChallengeMethod: CodeChallengeMethod
): Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            CodeChallengeMethod.fromValue(parcel.readString()!!))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(codeVerifier)
        parcel.writeString(codeChallenge)
        parcel.writeString(codeChallengeMethod.value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProofKeyForCodeExchange> {
        override fun createFromParcel(parcel: Parcel): ProofKeyForCodeExchange {
            return ProofKeyForCodeExchange(parcel)
        }

        override fun newArray(size: Int): Array<ProofKeyForCodeExchange?> {
            return arrayOfNulls(size)
        }
    }
}