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
package com.xci.zenkey.sdk.internal.network.stack

import android.support.annotation.VisibleForTesting

import org.json.JSONException

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection

import javax.net.ssl.HttpsURLConnection

internal class HttpResponseFactory<T>
    : HttpResponse.Factory<T> {

    /**
     * Get an [InputStreamReader] from an [java.net.URLConnection]
     *
     * @param inputStream the [InputStream]
     * @return the associated [InputStreamReader]
     */
    @VisibleForTesting
    internal fun getInputStreamReader(inputStream: InputStream): InputStreamReader {
        return InputStreamReader(inputStream)
    }

    /**
     * Get a [BufferedReader] from and [InputStreamReader]
     * @param streamReader the [InputStreamReader]
     * @return a [BufferedReader]
     */
    @VisibleForTesting
    internal fun getBufferedReader(streamReader: InputStreamReader): BufferedReader {
        return BufferedReader(streamReader)
    }

    @Throws(IOException::class, JSONException::class)
    override fun create(connection: HttpsURLConnection, converter: JsonConverter<T>): HttpResponse<T> {
        val responseCode = connection.responseCode
        return if ((responseCode >= HttpURLConnection.HTTP_OK) and (responseCode < HttpURLConnection.HTTP_MULT_CHOICE)) {
            val rawBody = readBody(getBufferedReader(getInputStreamReader(connection.inputStream)))
            HttpResponse.success(connection, rawBody, converter.convert(rawBody))
        } else {
            HttpResponse.error(connection,
                    readBody(getBufferedReader(getInputStreamReader(connection.errorStream))))
        }
    }

    /**
     * Read a Json string response from an [InputStreamReader]
     *
     * @param reader        the [BufferedReader]
     * @return the Json String
     * @throws IOException if it happen on the [java.io.InputStream]
     */
    @VisibleForTesting
    @Throws(IOException::class)
    internal fun readBody(reader: BufferedReader): String {
        val stringBuilder = StringBuilder()
        var inputLine: String? = null
        while ({ inputLine = reader.readLine(); inputLine }() != null) {
            stringBuilder.append(inputLine)
        }
        reader.close()
        return stringBuilder.toString()
    }
}
