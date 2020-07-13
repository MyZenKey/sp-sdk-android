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

import org.junit.Test
import org.junit.runner.RunWith

import java.net.MalformedURLException

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest

import org.junit.Assert.assertEquals

@SmallTest
@RunWith(AndroidJUnit4::class)
class HttpRequestBuilderTest {

    @Test
    @Throws(MalformedURLException::class)
    fun shouldBuildRequestWithSpecifiedValues() {
        val baseUrl = "https://test.com"
        val builder = HttpRequest.Builder(METHOD, baseUrl)

        builder.connectTimeout(CONNECT_TIMEOUT)
        builder.readTimeout(READ_TIMEOUT)

        val response = builder.build()
        assertEquals(CONNECT_TIMEOUT.toLong(), response.connectTimeout.toLong())
        assertEquals(READ_TIMEOUT.toLong(), response.readTimeout.toLong())
        assertEquals(CONNECT_TIMEOUT.toLong(), response.connectTimeout.toLong())
        assertEquals(baseUrl, response.baseUrl)
        assertEquals(METHOD, response.method)
    }

    companion object {
        private const val CONNECT_TIMEOUT = 10
        private const val READ_TIMEOUT = 5
        private val METHOD = HttpMethod.GET
    }
}
