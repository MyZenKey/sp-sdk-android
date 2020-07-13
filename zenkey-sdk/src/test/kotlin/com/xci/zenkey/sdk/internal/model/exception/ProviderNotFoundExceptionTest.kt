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
package com.xci.zenkey.sdk.internal.model.exception

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.xci.zenkey.sdk.internal.model.DiscoveryResponse
import com.xci.zenkey.sdk.internal.network.stack.HttpResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class ProviderNotFoundExceptionTest {

    private val mockHttpResponse = mock<HttpResponse<DiscoveryResponse>>()

    @Test
    fun shouldGetDiscoverUIEndpoint() {
        val discoverUiEndpoint = "DISCOVER_UI_ENDPOINT"
        val e = ProviderNotFoundException(discoverUiEndpoint)
        assertEquals(discoverUiEndpoint, e.discoverUiEndpoint)
    }

    @Test
    fun shouldGetProviderNotFoundExceptionWithDiscoverUIEndpoint() {
        whenever(mockHttpResponse.rawBody).thenReturn("{ \"redirect_uri\": \"redirect\" }")
        val exception = ProviderNotFoundException.from(mockHttpResponse)

        assertEquals("redirect", exception.discoverUiEndpoint)
    }

    @Test
    fun shouldGetProviderNotFoundExceptionWithoutDiscoverUIEndpoint() {
        whenever(mockHttpResponse.rawBody).thenReturn("{}")
        val exception = ProviderNotFoundException.from(mockHttpResponse)

        assertNull(exception.discoverUiEndpoint)
    }

    @Test
    fun shouldGetProviderNotFoundExceptionWithoutDiscoverUIEndpointIfJsonException() {
        whenever(mockHttpResponse.rawBody).thenReturn("")
        val exception = ProviderNotFoundException.from(mockHttpResponse)

        assertNull(exception.discoverUiEndpoint)
    }
}
