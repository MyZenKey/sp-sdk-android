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

data class TokenResponse (
    /* a JWT linked to the session. This JWT acts as an access token and must be passed in the Authorization header for authenticated requests made to the API backend */
    @Json(name = "token")
    val token: String,
    /* a refresh token to be used to renew the token */
    @Json(name = "refresh_token")
    val refreshToken: String,
    /* the type of the token */
    @Json(name = "token_type")
    val tokenType: String,
    /* seconds remaining until the token expires */
    @Json(name = "expires")
    val expires: Long
) {
    override fun toString(): String {
        return "TokenResponse(token=$token, refreshToken=$refreshToken, tokenType=$tokenType, expires=$expires)"
    }
}