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
package com.xci.zenkey.sdk.internal.discovery

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.*
import com.xci.zenkey.sdk.internal.contract.ICache
import com.xci.zenkey.sdk.internal.model.DiscoveryResponse
import com.xci.zenkey.sdk.internal.model.OpenIdConfiguration
import com.xci.zenkey.sdk.internal.model.exception.ProviderNotFoundException
import com.xci.zenkey.sdk.internal.network.call.discovery.IDiscoveryCallFactory
import com.xci.zenkey.sdk.internal.network.stack.HttpCall
import com.xci.zenkey.sdk.internal.network.stack.HttpException
import com.xci.zenkey.sdk.internal.network.stack.HttpResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyString
import java.net.HttpURLConnection
import java.net.UnknownHostException

@RunWith(AndroidJUnit4::class)
class DiscoveryService30Test {
    
    private interface SuccessCallback : (OpenIdConfiguration) -> Unit
    private interface FailureCallback : (Throwable) -> Unit

    private val mockDiscoveryCallFactory = mock<IDiscoveryCallFactory>()
    private val mockCache = mock<ICache<String, OpenIdConfiguration>>()

    private val mockSuccessUnit = mock<SuccessCallback>()
    private val mockErrorUnit = mock<FailureCallback>()

    //OIDC
    private val mockOidc = mock<OpenIdConfiguration>()
    private val mockDiscoveryResponse = mock<DiscoveryResponse>()
    private val mockOIDCHttpCall = mock<HttpCall<DiscoveryResponse>>()
    private val mockOIDCResponse = mock<HttpResponse<DiscoveryResponse>>()
    private var oidcSuccessUnitCaptor = argumentCaptor<(HttpResponse<DiscoveryResponse>) -> Unit>()
    private var oidcErrorUnitCaptor = argumentCaptor<(Exception) -> Unit>()

    private lateinit var discoveryService: DiscoveryService

    @Before
    fun setUp() {
        whenever(mockDiscoveryCallFactory.create(anyString(), anyBoolean())).thenReturn(mockOIDCHttpCall)

        doNothing().whenever(mockOIDCHttpCall).enqueue(oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        whenever(mockOIDCResponse.body).thenReturn(mockDiscoveryResponse)

        whenever(mockOidc.authorizationEndpoint).thenReturn(AUTHORIZATION_URI.toString())
        discoveryService = DiscoveryService(mockCache, mockDiscoveryCallFactory)
    }

    @Test
    fun shouldGetOIDC() {
        whenever(mockDiscoveryResponse.configuration).thenReturn(mockOidc)
        whenever(mockOIDCResponse.isSuccessful).thenReturn(true)

        discoveryService.discoverConfiguration(MCC_MNC, false, mockSuccessUnit, mockErrorUnit)

        oidcSuccessUnitCaptor.firstValue.invoke(mockOIDCResponse)

        verify(mockSuccessUnit).invoke(mockOidc)
    }

    @Test
    fun shouldGetProviderNotFoundException() {
        whenever(mockOIDCResponse.isSuccessful).thenReturn(false)
        whenever(mockOIDCResponse.code).thenReturn(HttpURLConnection.HTTP_NOT_FOUND)
        whenever(mockOIDCResponse.rawBody).thenReturn("")

        discoveryService.discoverConfiguration(MCC_MNC, false, mockSuccessUnit, mockErrorUnit)

        oidcSuccessUnitCaptor.firstValue.invoke(mockOIDCResponse)

        verify(mockErrorUnit).invoke(isA<ProviderNotFoundException>())
    }

    @Test
    fun shouldGetHttpException() {
        whenever(mockOIDCResponse.isSuccessful).thenReturn(false)
        whenever(mockOIDCResponse.code).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST)
        whenever(mockOIDCResponse.rawBody).thenReturn("")

        discoveryService.discoverConfiguration(MCC_MNC, false, mockSuccessUnit, mockErrorUnit)

        oidcSuccessUnitCaptor.firstValue.invoke(mockOIDCResponse)

        verify(mockErrorUnit).invoke(isA<HttpException>())
    }

    @Test
    fun shouldGetCachedOIDCIfNotExpired() {
        whenever(mockOidc.isExpired).thenReturn(false)

        discoveryService.getOIDCInternal(MCC_MNC, false, mockSuccessUnit, mockErrorUnit, mockOidc)

        verify(mockSuccessUnit).invoke(mockOidc)
    }

    @Test
    fun shouldGetNotGetCachedOIDCIfNothingInCache() {
        val exception = Exception()
        discoveryService.getOIDCInternal(MCC_MNC, false, mockSuccessUnit, mockErrorUnit, null)

        oidcErrorUnitCaptor.firstValue.invoke(exception)

        verify(mockErrorUnit).invoke(exception)
    }

    @Test
    fun shouldGetNotGetCachedOIDCIfExpired() {
        whenever(mockOidc.isExpired).thenReturn(true)

        discoveryService.getOIDCInternal(MCC_MNC, false, mockSuccessUnit, mockErrorUnit, mockOidc)

        verify(mockSuccessUnit, never()).invoke(mockOidc)
    }

    @Test
    fun shouldGetOIDCAndCacheItOnReceiveOpenIdConfigurationResponse() {
        whenever(mockDiscoveryResponse.configuration).thenReturn(mockOidc)
        whenever(mockOIDCResponse.isSuccessful).thenReturn(true)

        discoveryService.onReceiveOpenIdConfigurationResponse(MCC_MNC, mockOIDCResponse, mockSuccessUnit, mockErrorUnit)

        verify(mockSuccessUnit).invoke(mockOidc)
        verify(mockCache).put(MCC_MNC, mockOidc)
    }

    @Test
    fun shouldGetProviderNotFoundExceptionOnReceiveOpenIdConfigurationResponse() {
        whenever(mockOIDCResponse.isSuccessful).thenReturn(false)
        whenever(mockOIDCResponse.code).thenReturn(HttpURLConnection.HTTP_NOT_FOUND)
        whenever(mockOIDCResponse.rawBody).thenReturn("")
        discoveryService.onReceiveOpenIdConfigurationResponse(MCC_MNC, mockOIDCResponse, mockSuccessUnit, mockErrorUnit)

        verify(mockErrorUnit).invoke(isA<ProviderNotFoundException>())
    }

    @Test
    fun shouldGetHttpExceptionOnReceiveOpenIdConfigurationResponse() {
        whenever(mockOIDCResponse.isSuccessful).thenReturn(false)
        whenever(mockOIDCResponse.code).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST)
        whenever(mockOIDCResponse.rawBody).thenReturn("")

        discoveryService.onReceiveOpenIdConfigurationResponse(MCC_MNC, mockOIDCResponse, mockSuccessUnit, mockErrorUnit)

        verify(mockErrorUnit).invoke(isA<HttpException>())
    }

    @Test
    fun shouldGetCachedOIDCOnReceiveOpenIdConfigurationFailureWithNetworkFailureAndCachedOIDC() {
        discoveryService.onReceiveOpenIdConfigurationFailure(UnknownHostException(), mockOidc, mockSuccessUnit, mockErrorUnit)

        verify(mockSuccessUnit).invoke(mockOidc)
    }

    @Test
    fun shouldGetErrorOnReceiveOpenIdConfigurationFailureWithNonNetworkFailureException() {
        val exception = Exception()
        discoveryService.onReceiveOpenIdConfigurationFailure(exception, mockOidc, mockSuccessUnit, mockErrorUnit)
        verify(mockErrorUnit).invoke(exception)
    }

    companion object {
        private const val MCC_MNC = "MCC_MNC"
        private const val SCHEME = "https"
        private const val AUTHORITY = "mno.com"
        private const val PATH = "authorise"
        private val AUTHORIZATION_URI = Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .path(PATH)
                .build()

    }
}
