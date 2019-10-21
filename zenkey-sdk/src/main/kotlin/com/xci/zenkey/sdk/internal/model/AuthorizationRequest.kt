package com.xci.zenkey.sdk.internal.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.VisibleForTesting

import com.xci.zenkey.sdk.internal.Json
import com.xci.zenkey.sdk.internal.ktx.appendQueryParameterIfNotNull
import com.xci.zenkey.sdk.param.ResponseType

class AuthorizationRequest
    : Parcelable {

    @VisibleForTesting
    val clientId: String?
    @VisibleForTesting
    val redirectUri: Uri
    @VisibleForTesting
    val scope: String?
    @VisibleForTesting
    val state: String?
    @VisibleForTesting
    val acr: String?
    @VisibleForTesting
    val nonce: String?
    @VisibleForTesting
    val prompt: String?
    @VisibleForTesting
    val correlationId: String?
    @VisibleForTesting
    val context: String?
    @VisibleForTesting
    var loginHintToken: String? = null
        private set

    internal constructor(clientId: String,
                         redirectUri: Uri,
                         scope: String?,
                         state: String?,
                         acr: String?,
                         nonce: String?,
                         prompt: String?,
                         correlationId: String?,
                         context: String?) {
        this.clientId = clientId
        this.scope = scope
        this.state = state
        this.redirectUri = redirectUri
        this.acr = acr
        this.nonce = nonce
        this.prompt = prompt
        this.correlationId = correlationId
        this.context = context
    }

    private constructor(`in`: Parcel) {
        clientId = `in`.readString()!!
        redirectUri = `in`.readParcelable(javaClass.classLoader)!!

        scope = if (`in`.readInt() == NON_NULL_VALUE) {
            `in`.readString()
        } else {
            null
        }

        state = if (`in`.readInt() == NON_NULL_VALUE) {
            `in`.readString()
        } else {
            null
        }

        acr = if (`in`.readInt() == NON_NULL_VALUE) {
            `in`.readString()
        } else {
            null
        }

        nonce = if (`in`.readInt() == NON_NULL_VALUE) {
            `in`.readString()
        } else {
            null
        }

        prompt = if (`in`.readInt() == NON_NULL_VALUE) {
            `in`.readString()
        } else {
            null
        }

        correlationId = if (`in`.readInt() == NON_NULL_VALUE) {
            `in`.readString()
        } else {
            null
        }

        context = if (`in`.readInt() == NON_NULL_VALUE) {
            `in`.readString()
        } else {
            null
        }

        loginHintToken = if (`in`.readInt() == NON_NULL_VALUE) {
            `in`.readString()
        } else {
            null
        }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(clientId)
        dest.writeParcelable(redirectUri, flags)

        if (scope != null) {
            dest.writeInt(NON_NULL_VALUE)
            dest.writeString(scope)
        } else {
            dest.writeInt(NULL_VALUE)
        }

        if (state != null) {
            dest.writeInt(NON_NULL_VALUE)
            dest.writeString(state)
        } else {
            dest.writeInt(NULL_VALUE)
        }

        if (acr != null) {
            dest.writeInt(NON_NULL_VALUE)
            dest.writeString(acr)
        } else {
            dest.writeInt(NULL_VALUE)
        }

        if (nonce != null) {
            dest.writeInt(NON_NULL_VALUE)
            dest.writeString(nonce)
        } else {
            dest.writeInt(NULL_VALUE)
        }

        if (prompt != null) {
            dest.writeInt(NON_NULL_VALUE)
            dest.writeString(prompt)
        } else {
            dest.writeInt(NULL_VALUE)
        }

        if (correlationId != null) {
            dest.writeInt(NON_NULL_VALUE)
            dest.writeString(correlationId)
        } else {
            dest.writeInt(NULL_VALUE)
        }

        if (context != null) {
            dest.writeInt(NON_NULL_VALUE)
            dest.writeString(context)
        } else {
            dest.writeInt(NULL_VALUE)
        }

        if (loginHintToken != null) {
            dest.writeInt(NON_NULL_VALUE)
            dest.writeString(loginHintToken)
        } else {
            dest.writeInt(NULL_VALUE)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    fun withLoginHintToken(loginHintToken: String?): AuthorizationRequest {
        this.loginHintToken = loginHintToken
        return this
    }

    fun isNotMatching(responseState: String?): Boolean {
        return (state == null) and (responseState != null) || state != null && state != responseState
    }

    fun toDiscoverUiUri(endpoint: String)
            : Uri = Uri.parse(endpoint)
            .buildUpon()
            .appendQueryParameter(Json.KEY_CLIENT_ID, clientId)
            .appendQueryParameter(Json.KEY_REDIRECT_URI, redirectUri.toString())
            .appendQueryParameterIfNotNull(Json.KEY_STATE, state)
            .build()


    fun toAuthorizationUri(endpoint: String)
            : Uri = Uri.parse(endpoint)
            .buildUpon()
            .appendQueryParameter(Json.KEY_CLIENT_ID, clientId)
            .appendQueryParameter(Json.KEY_RESPONSE_TYPE, ResponseType.CODE.type)
            .appendQueryParameter(Json.KEY_REDIRECT_URI, redirectUri.toString())
            .appendQueryParameterIfNotNull(Json.KEY_SCOPE, scope)
            .appendQueryParameterIfNotNull(Json.KEY_STATE, state)
            .appendQueryParameterIfNotNull(Json.KEY_PROMPT, prompt)
            .appendQueryParameterIfNotNull(Json.KEY_NONCE, nonce)
            .appendQueryParameterIfNotNull(Json.KEY_ACR_VALUES, acr)
            .appendQueryParameterIfNotNull(Json.KEY_CORRELATION_ID, correlationId)
            .appendQueryParameterIfNotNull(Json.KEY_CONTEXT, context)
            .appendQueryParameterIfNotNull(Json.KEY_LOGIN_HINT_TOKEN, loginHintToken)
            .build()


    override fun toString(): String {
        return "AuthorizationRequest{" +
                "clientId='" + clientId + '\'' +
                ", redirectUri=" + redirectUri +
                ", scope='" + scope + '\'' +
                ", state='" + state + '\'' +
                ", acr='" + acr + '\'' +
                ", nonce='" + nonce + '\'' +
                ", prompt='" + prompt + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", context='" + context + '\'' +
                '}'
    }

    companion object {

        private const val NULL_VALUE = 0
        private const val NON_NULL_VALUE = 1

        @JvmField
        val CREATOR: Parcelable.Creator<AuthorizationRequest> = object : Parcelable.Creator<AuthorizationRequest> {
            override fun createFromParcel(`in`: Parcel): AuthorizationRequest {
                return AuthorizationRequest(`in`)
            }

            override fun newArray(size: Int): Array<AuthorizationRequest?> {
                return arrayOfNulls(size)
            }
        }
    }
}


