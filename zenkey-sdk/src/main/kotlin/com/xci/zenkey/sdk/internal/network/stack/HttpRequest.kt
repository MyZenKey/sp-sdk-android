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
package com.xci.zenkey.sdk.internal.network.stack

import android.net.Uri

import java.net.MalformedURLException
import java.net.URL
import java.util.HashMap

/**
 * A model class representing a generic Http request.
 */
internal class HttpRequest internal constructor(
        builder: Builder
) {

    /**
     * Get the [HttpMethod] to use.
     *
     * @return the [HttpMethod] to use
     */
    internal val method: HttpMethod
    /**
     * Get the [URL] to use.
     *
     * @return the [URL] to use.
     */
    internal val baseUrl: String
    internal val url: URL
    /**
     * Get the buildConnectionFor timeout to use.
     *
     * @return the buildConnectionFor timeout to use.
     */
    internal val connectTimeout: Int
    /**
     * Get the read timeout to use.
     *
     * @return the read timeout to use.
     */
    internal val readTimeout: Int
    /**
     * Get the headers for this request.
     * @return a [HashMap] representing the headers of this request.
     */
    internal val headers: HashMap<String, String>
    private val params: HashMap<String, String>
    private val useCaches: Boolean

    init {
        this.method = builder.method
        this.connectTimeout = builder.connectTimeout
        this.readTimeout = builder.readTimeout
        this.baseUrl = builder.baseUrl
        this.headers = builder.headers
        this.params = builder.params
        this.useCaches = builder.useCaches
        this.url = builder.url
    }

    /**
     * This class is used to Build [HttpRequest]
     */
    class Builder
    /**
     * Constructor for [Builder]
     *
     * @param method the [HttpMethod] for this request.
     * @param baseUrl the base Url for this request.
     */
    (internal val method: HttpMethod, internal val baseUrl: String) {
        internal var connectTimeout = DEFAULT_CONNECT_TIMEOUT
        internal var readTimeout = DEFAULT_READ_TIMEOUT
        internal var useCaches = true
        internal lateinit var url: URL
        internal val headers = HashMap<String, String>()
        internal val params = HashMap<String, String>()

        /**
         * Set a buildConnectionFor time out for the request.
         *
         * @param connectTimeout the time out value.
         * @return this [Builder]
         */
        fun connectTimeout(connectTimeout: Int): Builder {
            this.connectTimeout = connectTimeout
            return this
        }

        /**
         * Set a read time out for the request.
         *
         * @param readTimeout the time out value.
         * @return this [Builder]
         */
        fun readTimeout(readTimeout: Int): Builder {
            this.readTimeout = readTimeout
            return this
        }

        /**
         * Add a Header to the request.
         * @param key the header key.
         * @param value the header value.
         * @return this [Builder]
         */
        fun withHeader(key: String, value: String): Builder {
            this.headers[key] = value
            return this
        }

        /**
         * Add a Query parameter
         * @param key the parameter key
         * @param value the parameter value
         * @return this [Builder]
         */
        fun withQueryParam(key: String, value: String): Builder {
            this.params[key] = value
            return this
        }

        /**
         * Add a Query parameter if value isn't null
         * @param key the parameter key
         * @param value the parameter value
         * @return this [Builder]
         */
        fun withOptionalQueryParam(key: String, value: String?): Builder {
            if (value != null) this.withQueryParam(key, value)
            return this
        }

        /**
         * Skip the cache for this request.
         * @return this [Builder]
         */
        fun skipCache(): Builder {
            this.useCaches = false
            return this
        }

        /**
         * Build the [HttpRequest]
         *
         * @return the built [HttpRequest]
         */
        @Throws(MalformedURLException::class)
        fun build(): HttpRequest {
            val builder = Uri.parse(baseUrl).buildUpon()
            for (key in params.keys) {
                builder.appendQueryParameter(key, params[key])
            }
            url = URL(builder.build().toString())
            return HttpRequest(this)
        }
    }

    companion object {

        const val DEFAULT_CONNECT_TIMEOUT = 1500
        const val DEFAULT_READ_TIMEOUT = 1500

        fun get(baseUrl: String): Builder {
            return Builder(HttpMethod.GET, baseUrl)
        }
    }
}
