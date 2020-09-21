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
package com.xci.zenkey.sdk.internal.network.call.assetlinks

import com.xci.zenkey.sdk.internal.network.stack.JsonConverter
import org.json.JSONArray
import org.json.JSONException
import java.util.*

internal class AssetLinksResponseConverter: JsonConverter<Map<String, List<String>>> {

    @Throws(JSONException::class)
    override fun convert(json: String): Map<String, List<String>> {
        val packages = mutableMapOf<String, List<String>>()
        val responseArray = JSONArray(json)
        for (i in 0 until responseArray.length()) {
            val target = responseArray.getJSONObject(i).getJSONObject(TARGET_KEY)
            val packageName = target.getString(PACKAGE_NAME_KEY)
            val fingerprintsArray = target.getJSONArray(FINGERPRINTS_KEY)
            val fingerprints = ArrayList<String>()
            for (j in 0 until fingerprintsArray.length()) {
                fingerprints.add(fingerprintsArray.getString(j))
            }
            packages[packageName] = fingerprints
        }
        return packages
    }

    companion object {
        internal const val TARGET_KEY = "target"
        internal const val FINGERPRINTS_KEY = "sha256_cert_fingerprints"
        internal const val PACKAGE_NAME_KEY = "package_name"
    }
}
