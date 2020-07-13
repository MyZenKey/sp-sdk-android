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

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

import javax.net.ssl.HttpsURLConnection

internal class HttpCallExecutor<T> constructor(
        private val responseFactory: HttpResponse.Factory<T>
) : HttpCall.Executor<T> {

    internal constructor() : this(HttpResponseFactory<T>())

    /**
     * Execute and [HttpRequest]
     *
     * @param request the request to execute.
     * @return an [HttpResponse]
     */
    @Throws(IOException::class, JSONException::class)
    override fun execute(request: HttpRequest, converter: JsonConverter<T>): HttpResponse<T> {
        return responseFactory.create(buildConnectionFor(request), converter)
    }

    /**
     * Create and buildConnectionFor an [java.net.URLConnection]
     *
     * @param request the [HttpRequest] to execute
     * @return an Opened [java.net.URLConnection] setup with the [HttpRequest] parameters.
     * @throws IOException if it happen when connecting the [java.net.URLConnection]
     */
    @Throws(IOException::class)
    fun buildConnectionFor(request: HttpRequest): HttpsURLConnection {
        val connection = request.url.openConnection() as HttpsURLConnection
        connection.requestMethod = request.method.name
        connection.connectTimeout = request.connectTimeout
        connection.readTimeout = request.readTimeout
        connection.connect()
        return connection
    }

    /**
     * Read a Json string response from an [InputStreamReader]
     *
     * @param reader        the [BufferedReader]
     * @param stringBuilder the [StringBuilder] used to build the [String] response.
     * @return the Json String
     * @throws IOException if it happen on the [java.io.InputStream]
     */
    @Throws(IOException::class)
    fun readResponse(reader: BufferedReader, stringBuilder: StringBuilder): String {
        var inputLine: String? = null
        while ({ inputLine = reader.readLine(); inputLine }() != null) {
            stringBuilder.append(inputLine)
        }
        reader.close()
        return stringBuilder.toString()
    }
}
