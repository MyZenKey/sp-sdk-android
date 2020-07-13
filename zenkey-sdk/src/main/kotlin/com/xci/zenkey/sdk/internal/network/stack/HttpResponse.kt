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
package com.xci.zenkey.sdk.internal.network.stack

import org.json.JSONException

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

import javax.net.ssl.HttpsURLConnection

/**
 * A model class representing a generic Http response.
 */
internal class HttpResponse<T> @Throws(IOException::class) internal constructor(
        private val connection: HttpsURLConnection,
        val rawBody: String,
        val body: T?,
        val url: URL = connection.url,
        val code: Int = connection.responseCode,
        val headers: Map<String, List<String>> =  connection.headerFields,
        val isSuccessful: Boolean = (code >= HttpURLConnection.HTTP_OK) and (code < HttpURLConnection.HTTP_MULT_CHOICE)
) {
    /**
     * An factory contract for [HttpResponse]
     * @param <T> the body type of the response.
     */
    interface Factory<T> {

        /**
         * Create an [HttpResponse]
         * @param connection the [HttpsURLConnection] used for the request.
         * @param converter the converter to use to parse the json [String] into the body type T
         * @return The [HttpResponse]
         * @throws IOException if the factory fail to open the [java.io.InputStream] of the [HttpsURLConnection]
         * @throws JSONException if the converted is unable to parse the json [String].
         */
        @Throws(IOException::class, JSONException::class)
        fun create(connection: HttpsURLConnection, converter: JsonConverter<T>): HttpResponse<T>

    }

    companion object {

        @Throws(IOException::class)
        internal fun <T> error(connection: HttpsURLConnection, rawBody: String): HttpResponse<T> {
            return HttpResponse(connection, rawBody, null)
        }

        @Throws(IOException::class)
        internal fun <T> success(connection: HttpsURLConnection, rawBody: String, body: T): HttpResponse<T> {
            return HttpResponse(connection, rawBody, body)
        }
    }
}
