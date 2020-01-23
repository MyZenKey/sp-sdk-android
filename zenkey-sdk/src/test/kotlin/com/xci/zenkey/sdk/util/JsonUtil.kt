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
package com.xci.zenkey.sdk.util

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*

object JsonUtil {

    @Throws(IOException::class)
    fun getJsonResponse(classLoader: ClassLoader, fileName: String): String {
        val `is` = Objects.requireNonNull(classLoader).getResourceAsStream("json/$fileName")
        val size: Int
        try {
            size = `is`.available()
            val buffer = ByteArray(size)

            `is`.read(buffer)
            `is`.close()
            return String(buffer, StandardCharsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
            throw e
        }
    }
}
