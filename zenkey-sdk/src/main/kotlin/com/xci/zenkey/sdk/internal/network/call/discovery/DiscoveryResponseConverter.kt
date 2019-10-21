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
package com.xci.zenkey.sdk.internal.network.call.discovery

import com.xci.zenkey.sdk.internal.Json
import com.xci.zenkey.sdk.internal.model.DiscoveryResponse
import com.xci.zenkey.sdk.internal.model.OpenIdConfiguration
import com.xci.zenkey.sdk.internal.network.stack.JsonConverter

import org.json.JSONException
import org.json.JSONObject

internal class DiscoveryResponseConverter
    : JsonConverter<DiscoveryResponse> {

    @Throws(JSONException::class)
    override fun convert(json: String): DiscoveryResponse {
        val responseObject = JSONObject(json)
        return if (responseObject.has(Json.KEY_ERROR)) {
            DiscoveryResponse(responseObject.optString(Json.KEY_REDIRECT_URI))
        } else {
            DiscoveryResponse(OpenIdConfiguration(
                    responseObject.getString(Json.KEY_ISSUER),
                    responseObject.getString(Json.KEY_AUTHORIZATION_ENDPOINT),
                    responseObject.optString(Json.KEY_MCC_MNC)))
        }
    }
}
