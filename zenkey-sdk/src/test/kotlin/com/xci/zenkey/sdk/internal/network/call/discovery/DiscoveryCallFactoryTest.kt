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

import com.xci.zenkey.sdk.BuildConfig
import com.xci.zenkey.sdk.internal.network.stack.HttpMethod
import com.xci.zenkey.sdk.internal.network.stack.HttpRequest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.net.MalformedURLException
import java.net.URISyntaxException

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class DiscoveryCallFactoryTest {

    private lateinit var callFactory: DiscoveryCallFactory

    @Before
    fun setUp() {
        callFactory = DiscoveryCallFactory(CLIENT_ID)
    }

    @Test
    fun shouldContainTheCorrectStaticValues() {
        if (BuildConfig.DEBUG) {
            assertEquals(EXPECTED_QA_ENDPOINT, BuildConfig.DISCOVERY_ENDPOINT)
        } else {
            assertEquals(EXPECTED_PROD_ENDPOINT, BuildConfig.DISCOVERY_ENDPOINT)
        }
        assertEquals(EXPECTED_PATH_PART_1, DiscoveryCallFactory.WELL_KNOW_PATH)
        assertEquals(EXPECTED_PATH_PART_2, DiscoveryCallFactory.OPEN_ID_CONFIG_PATH)
    }

    @Test
    fun shouldCreateHttpCall() {
        assertNotNull(callFactory.create(MCC_MNC, false))
    }

    @Test
    @Throws(URISyntaxException::class, MalformedURLException::class)
    fun shouldBuildDiscoveryHttpRequest() {
        val prompt = true
        val expectedScheme = "https"
        val expectedAuthority = "test.com"
        val endpoint = "$expectedScheme://$expectedAuthority"
        val request = callFactory.buildDiscoveryRequest(endpoint, MCC_MNC, prompt)

        assertNotNull(request!!)
        val targetUri = request.url.toURI()
        assertEquals(HttpMethod.GET, request.method)
        assertEquals(expectedScheme, targetUri.scheme)
        assertEquals(expectedAuthority, targetUri.authority)
        assertTrue(targetUri.query.contains(MCC_MNC))
        assertTrue(targetUri.query.contains(prompt.toString()))
        assertEquals(HttpRequest.DEFAULT_CONNECT_TIMEOUT.toLong(), request.connectTimeout.toLong())
        assertEquals(HttpRequest.DEFAULT_READ_TIMEOUT.toLong(), request.readTimeout.toLong())
    }

    @Test
    @Throws(URISyntaxException::class)
    fun shouldBuildDiscoveryHttpRequestWithPromptParam() {
        val expectedScheme = "https"
        val expectedAuthority = "test.com"
        val endpoint = "$expectedScheme://$expectedAuthority"
        val request = callFactory.buildDiscoveryRequest(endpoint, MCC_MNC, false)
        assertNotNull(request!!)
        assertFalse(request.url.toURI().query.contains("prompt=false"))
    }

    @Test
    fun shouldGetNullIfMalformedUrlException() {
        assertNull(callFactory.buildDiscoveryRequest("", MCC_MNC, false))
    }

    companion object {
        private const val CLIENT_ID = "CLIENT_ID"
        private const val MCC_MNC = "MCCMNC"
        private const val EXPECTED_PATH_PART_1 = ".well-known"
        private const val EXPECTED_PATH_PART_2 = "openid_configuration"
        private const val EXPECTED_QA_ENDPOINT = "https://discoveryissuer-qa.myzenkey.com/"
        private const val EXPECTED_PROD_ENDPOINT = "https://discoveryissuer.myzenkey.com/"
    }
}
