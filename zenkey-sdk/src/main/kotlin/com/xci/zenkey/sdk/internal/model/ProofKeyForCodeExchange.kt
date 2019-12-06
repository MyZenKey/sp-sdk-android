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