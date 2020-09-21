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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsService
import com.xci.zenkey.sdk.internal.Json.KEY_ACR_VALUES
import com.xci.zenkey.sdk.internal.Json.KEY_CLIENT_ID
import com.xci.zenkey.sdk.internal.Json.KEY_CODE
import com.xci.zenkey.sdk.internal.Json.KEY_CODE_CHALLENGE
import com.xci.zenkey.sdk.internal.Json.KEY_CODE_CHALLENGE_METHOD
import com.xci.zenkey.sdk.internal.Json.KEY_CONTEXT
import com.xci.zenkey.sdk.internal.Json.KEY_CORRELATION_ID
import com.xci.zenkey.sdk.internal.Json.KEY_ERROR
import com.xci.zenkey.sdk.internal.Json.KEY_ERROR_DESCRIPTION
import com.xci.zenkey.sdk.internal.Json.KEY_LOGIN_HINT_TOKEN
import com.xci.zenkey.sdk.internal.Json.KEY_MCC_MNC
import com.xci.zenkey.sdk.internal.Json.KEY_NONCE
import com.xci.zenkey.sdk.internal.Json.KEY_OPTIONS
import com.xci.zenkey.sdk.internal.Json.KEY_PROMPT
import com.xci.zenkey.sdk.internal.Json.KEY_REDIRECT_URI
import com.xci.zenkey.sdk.internal.Json.KEY_SCOPE
import com.xci.zenkey.sdk.internal.Json.KEY_STATE
import com.xci.zenkey.sdk.internal.model.AuthorizationRequest
import com.xci.zenkey.sdk.internal.model.error.ZenKeyError.USER_NOT_FOUND
import java.util.*

internal val Uri.containError
    get() = queryParameterNames.contains(KEY_ERROR)

internal val Uri.containCode
    get() =  queryParameterNames.contains(KEY_CODE)

internal val Uri.loginHintToken: String?
    get() = getQueryParameter(KEY_LOGIN_HINT_TOKEN)

internal val Uri.state: String?
    get() = getQueryParameter(KEY_STATE)

internal val Uri.mccMnc: String?
    get() = getQueryParameter(KEY_MCC_MNC)

internal val Uri.code: String?
    get() = getQueryParameter(KEY_CODE)

internal val Uri.codeChallenge: String?
    get() = getQueryParameter(KEY_CODE_CHALLENGE)

internal val Uri.codeChallengeMethod: String?
    get() = getQueryParameter(KEY_CODE_CHALLENGE_METHOD)

internal val Uri.error: String?
    get() = getQueryParameter(KEY_ERROR)

internal val Uri.errorDescription: String?
    get() = getQueryParameter(KEY_ERROR_DESCRIPTION)

internal val Uri.clientId: String?
    get() = getQueryParameter(KEY_CLIENT_ID)

internal val Uri.redirectUri: String?
    get() = getQueryParameter(KEY_REDIRECT_URI)

internal val Uri.scope: String?
    get() = getQueryParameter(KEY_SCOPE)

internal val Uri.acr: String?
    get() = getQueryParameter(KEY_ACR_VALUES)

internal val Uri.prompt: String?
    get() = getQueryParameter(KEY_PROMPT)

internal val Uri.correlationId: String?
    get() = getQueryParameter(KEY_CORRELATION_ID)

internal val Uri.context: String?
    get() = getQueryParameter(KEY_CONTEXT)

internal val Uri.options: String?
    get() = getQueryParameter(KEY_OPTIONS)

internal val Uri.nonce: String?
    get() = getQueryParameter(KEY_NONCE)

internal val Uri.isUserNotFoundError: Boolean
    get() = error?.let { it == USER_NOT_FOUND.error } ?: false

internal val Uri.intent: Intent
    get() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.data = this
        return intent
    }

internal fun Uri.isNotMatchingStateIn(
        authorizationRequest: AuthorizationRequest
): Boolean = (authorizationRequest.state == null) and (state != null) ||
            authorizationRequest.state != null && authorizationRequest.state != state

internal fun Array<out Uri>.toCustomTabBundles(
        startIndex: Int
): List<Bundle> {
    require(startIndex >= 0) { "startIndex must be positive" }

    if (size <= startIndex) {
        return emptyList()
    }

    val uriBundles = ArrayList<Bundle>(size - startIndex)
    for (i in startIndex until size) {

        val uriBundle = Bundle()
        uriBundle.putParcelable(CustomTabsService.KEY_URL, this[i])
        uriBundles.add(uriBundle)
    }

    return uriBundles
}