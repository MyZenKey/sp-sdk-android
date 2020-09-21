/**
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
package com.xci.zenkey.example.client.model

import com.squareup.moshi.Json

data class SignInRequest (
    /* Client ID */
    @Json(name = "client_id")
    val clientId: String,
    /* code from ZenKey SDK AuthorizedResponse */
    @Json(name = "code")
    val code: String,
    /* Redirect URL */
    @Json(name = "redirect_uri")
    val redirectUri: String,
    /* MCCMNC of the user's carrier */
    @Json(name = "mccmnc")
    val mccmnc: String,
    /* codeVerifier from ZenKey SDK AuthorizedResponse */
    @Json(name = "code_verifier")
    val codeVerifier: String? = null,
    /* the same correlation ID passed in the auth request. Used to correlate requests */
    @Json(name = "correlation_id")
    val correlationId: String? = null,
    /* the nonce passed in the auth request. If present, the backend will validate the nonce returned in the ID token to make sure it matches */
    @Json(name = "nonce")
    val nonce: String? = null,
    /* the context passed in the auth request. If present, the backend will validate the context returned in the ID token to make sure it matches */
    @Json(name = "context")
    val context: String? = null,
    /* the ACR values passed in the auth request. If present, the backend will validate the ACR values returned in the ID token to make sure they match. This should be a series of strings separated by spaces. */
    @Json(name = "acr_values")
    val acrValues: String? = null
)