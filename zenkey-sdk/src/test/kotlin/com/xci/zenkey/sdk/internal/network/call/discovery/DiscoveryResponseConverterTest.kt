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
package com.xci.zenkey.sdk.internal.network.call.discovery

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest

import org.json.JSONException
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import com.xci.zenkey.sdk.util.JsonUtil.getJsonResponse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull

@RunWith(AndroidJUnit4::class)
@SmallTest
class DiscoveryResponseConverterTest {

    private var converter: DiscoveryResponseConverter? = null

    private val attFoundDiscoveryResponse: String
        get() = getJsonResponse(ClassLoader.getSystemClassLoader(), ATT_FOUND_FILE_NAME)

    private val failingDiscoveryResponse: String
        get() = getJsonResponse(ClassLoader.getSystemClassLoader(), PROVIDER_ERROR_FILE_NAME)

    private val invalidDiscoveryResponse: String
        get() = getJsonResponse(ClassLoader.getSystemClassLoader(), INVALID_DISCOVERY_FILE_NAME)

    @Before
    fun setUp() {
        converter = DiscoveryResponseConverter()
    }

    @Test(expected = Exception::class)
    @Throws(Exception::class)
    fun shouldGetExceptionWhenParsingDiscoveryResponse() {
        converter!!.convert("{}")
    }

    @Test
    @Throws(JSONException::class)
    fun shouldGetMatchedCarrierConfigurationWhenParsingDiscoveryResponse() {
        val response = converter!!.convert(attFoundDiscoveryResponse)
        assertNotNull(response)
        assertNotNull(response.configuration)
    }

    @Test
    @Throws(JSONException::class)
    fun shouldGetDiscoverUIEndpointWhenParsingDiscoveryResponse() {
        val response = converter!!.convert(failingDiscoveryResponse)
        assertNotNull(response)
        assertNotNull(response.discoverUIEndpoint)
    }

    @Test(expected = JSONException::class)
    @Throws(JSONException::class)
    fun shouldGetJSONException() {
        assertNull(converter!!.convert(invalidDiscoveryResponse))
    }

    companion object {
        const val ATT_FOUND_FILE_NAME = "att_found_response.json"
        const val PROVIDER_ERROR_FILE_NAME = "provider_error_response.json"
        const val INVALID_DISCOVERY_FILE_NAME = "invalid_discovery_response.json"
    }
}
