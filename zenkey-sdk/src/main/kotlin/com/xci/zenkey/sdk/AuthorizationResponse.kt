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
import android.os.Parcelable
import com.xci.zenkey.sdk.internal.model.AuthorizationRequest

/**
 * A model class representing an authorization response.
 * The result of an authorization request can be obtained from a result [Intent] data.
 * In the [android.app.Activity.onActivityResult]
 * or [android.app.Fragment.onActivityResult]
 * of the [android.app.Activity]/[android.app.Fragment] receiving the result.
 * You can do [AuthorizationResponse.fromIntent] and
 * get the [AuthorizationResponse].
 */
class AuthorizationResponse : Parcelable {
    /**
     * Get the authorization code.
     *
     * @return the authorization code of a successful authorization request, null is the request failed.
     * [AuthorizationResponse.isSuccessful]
     */
    val authorizationCode: String?
    /**
     * Get the MCC/MNC used to perform a discovery.
     *
     * @return the MCC/MNC used for the discovery.
     */
    val mccMnc: String?
    /**
     * Get the [AuthorizationError]
     *
     * @return the [AuthorizationError] if exist or null if the request was successful
     * [AuthorizationResponse.isSuccessful]
     */
    val error: AuthorizationError?
    /**
     * Get the redirect [Uri] used for the request.
     * @return the redirect [Uri] used for the request.
     */
    val redirectUri: Uri?

    /**
     * The ZenKey SDK version.
     */
    val sdkVersion: String = BuildConfig.VERSION_NAME

    /**
     * Check if an Authorization Request was successful.
     *
     * @return true it the request was successful, false else.
     */
    val isSuccessful: Boolean
        get() = (error == null) and (authorizationCode != null)

    /**
     * The PKCE challenge code_verifier
     */
    val codeVerifier: String?

    /**
     * Constructor for Successful [AuthorizationResponse]
     *
     * @param authorizationCode the authorization code
     * @param mcc_mnc           the MCC/MNC used for the request.
     */
    constructor(mcc_mnc: String,
                request: AuthorizationRequest,
                authorizationCode: String) {
        this.authorizationCode = authorizationCode
        this.mccMnc = mcc_mnc
        this.redirectUri = request.redirectUri
        this.error = null
        this.codeVerifier = request.proofKeyForCodeExchange.codeVerifier
    }

    /**
     * Constructor for failing [AuthorizationResponse]
     *
     * @param error   the [AuthorizationError]
     * @param mcc_mnc the MCC/MNC used for the request.
     */
    constructor(mcc_mnc: String?,
                redirectUri: Uri,
                error: AuthorizationError) {
        this.authorizationCode = null
        this.mccMnc = mcc_mnc
        this.redirectUri = redirectUri
        this.error = error
        this.codeVerifier = null
    }

    /**
     * Constructor for [Parcelable] implementation.
     *
     * @param in the parcel to extract.
     */
    private constructor(`in`: Parcel) {
        redirectUri = `in`.readParcelable(javaClass.classLoader)

        mccMnc = if (`in`.readInt() == NON_NULL_VALUE) {
            `in`.readString()
        } else {
            null
        }

        authorizationCode = if (`in`.readInt() == NON_NULL_VALUE) {
            `in`.readString()
        } else {
            null
        }

        error = if (`in`.readInt() == NON_NULL_VALUE) {
            AuthorizationError.valueOf(`in`.readString()!!).withDescription(`in`.readString())
        } else {
            null
        }

        codeVerifier = if (`in`.readInt() == NON_NULL_VALUE) {
            `in`.readString()
        } else {
            null
        }
    }

    /**
     * Describe the content of a Parcel.
     * [Parcelable.describeContents]
     *
     * @return an [Integer] describing the [Parcelable].
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Write the data to a [Parcel] object [Parcelable.writeToParcel]
     *
     * @param dest  the destination [Parcel]
     * @param flags the flags to use.
     */
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(redirectUri, 0)

        if (mccMnc != null) {
            dest.writeInt(NON_NULL_VALUE)
            dest.writeString(mccMnc)
        } else {
            dest.writeInt(NULL_VALUE)
        }

        if (authorizationCode != null) {
            dest.writeInt(NON_NULL_VALUE)
            dest.writeString(authorizationCode)
        } else {
            dest.writeInt(NULL_VALUE)
        }

        if (error != null) {
            dest.writeInt(NON_NULL_VALUE)
            dest.writeString(error.name)
            dest.writeString(error.description())
        } else {
            dest.writeInt(NULL_VALUE)
        }

        if (codeVerifier != null) {
            dest.writeInt(NON_NULL_VALUE)
            dest.writeString(codeVerifier)
        } else {
            dest.writeInt(NULL_VALUE)
        }
    }


    /**
     * Create an [Intent] containing this response.
     * @return an [Intent] containing this response.
     */
    fun toIntent(): Intent {
        val intent = Intent()
        val bundle = Bundle()
        bundle.putParcelable(EXTRA_AUTH_RESPONSE, this)
        intent.putExtra(EXTRA_AUTH_RESPONSE, bundle)
        return intent
    }

    override fun toString(): String {
        return "AuthorizationResponse(" +
                "authorizationCode=$authorizationCode, " +
                "mccMnc=$mccMnc, " +
                "error=$error, " +
                "redirectUri=$redirectUri, " +
                "codeVerifier=$codeVerifier)"
    }

    /**
     * Factory Contract for [AuthorizationResponse]
     */
    interface Factory {

        /**
         * Create a successful response from a [Uri].
         * @param mcc_mnc the MCC/MNC tuple use for the request.
         * @param request the [AuthorizationRequest]
         * @param uri the result [Uri] from this request.
         * @return a successful [AuthorizationResponse]
         */
        fun create(mcc_mnc: String,
                   request: AuthorizationRequest,
                   uri: Uri): AuthorizationResponse

        /**
         * Create a failure response from a [Throwable].
         * @param mcc_mnc the MCC/MNC tuple use for the request.
         * @param redirectUri the redirect [Uri] used for the request.
         * @param throwable the [Throwable]
         * @return an unsuccessful [AuthorizationResponse]
         */
        fun create(mcc_mnc: String?,
                   redirectUri: Uri,
                   throwable: Throwable): AuthorizationResponse

        /**
         * Create a failure response from an [AuthorizationError].
         * @param mcc_mnc the MCC/MNC tuple use for the request.
         * @param redirectUri the redirect [Uri] used for the request.
         * @param error the [AuthorizationError]
         * @return an unsuccessful [AuthorizationResponse]
         */
        fun create(mcc_mnc: String?,
                   redirectUri: Uri,
                   error: AuthorizationError): AuthorizationResponse
    }


    companion object {

        private const val NULL_VALUE = 0
        private const val NON_NULL_VALUE = 1
        internal val EXTRA_AUTH_RESPONSE = "EXTRA_AUTH_RESPONSE"

        /**
         * The [Parcelable] creator.
         */
        @JvmField
        val CREATOR: Parcelable.Creator<AuthorizationResponse> = object : Parcelable.Creator<AuthorizationResponse> {
            override fun createFromParcel(`in`: Parcel): AuthorizationResponse {
                return AuthorizationResponse(`in`)
            }

            override fun newArray(size: Int): Array<AuthorizationResponse?> {
                return arrayOfNulls(size)
            }
        }

        /**
         * Get an [AuthorizationResponse] from an [Intent] data result.
         *
         * @param intent the intent containing the [AuthorizationResponse]
         * @return the [AuthorizationResponse] is the [Intent] contain one, null else.
         */
        @JvmStatic
        fun fromIntent(intent: Intent): AuthorizationResponse? {
            val extras = intent.extras
            if (extras != null && extras.containsKey(EXTRA_AUTH_RESPONSE)) {
                val responseBundle = extras.getBundle(EXTRA_AUTH_RESPONSE)
                if (responseBundle != null) {
                    return responseBundle.getParcelable(EXTRA_AUTH_RESPONSE)
                }
            }
            return null
        }
    }
}
