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

import com.xci.zenkey.sdk.IdentityProvider
import com.xci.zenkey.sdk.internal.OpenIdConfigurationCache
import com.xci.zenkey.sdk.internal.contract.ICache
import com.xci.zenkey.sdk.internal.contract.IDiscoveryService
import com.xci.zenkey.sdk.internal.ktx.isNetworkFailure
import com.xci.zenkey.sdk.internal.model.DiscoveryResponse
import com.xci.zenkey.sdk.internal.model.OpenIdConfiguration
import com.xci.zenkey.sdk.internal.model.exception.ProviderNotFoundException
import com.xci.zenkey.sdk.internal.network.call.discovery.DiscoveryCallFactory
import com.xci.zenkey.sdk.internal.network.call.discovery.IDiscoveryCallFactory
import com.xci.zenkey.sdk.internal.network.stack.HttpException
import com.xci.zenkey.sdk.internal.network.stack.HttpResponse
import java.net.HttpURLConnection

/**
 * A [IDiscoveryService] implementation.
 * This class is responsible to manage OpenId discovery process.
 * It's responsible to perform discovery using a [IDiscoveryCallFactory] implementation,
 * and to cache the received [OpenIdConfiguration] using a [ICache] implementation.
 * This class isn't intended to be use directly but by a
 * [IdentityProvider] implementation.
 */
internal open class DiscoveryService internal constructor(
    private val cache: ICache<String, OpenIdConfiguration>,
    private val discoveryCallFactory: IDiscoveryCallFactory
) : IDiscoveryService {

    internal constructor(clientId: String) : this(
        OpenIdConfigurationCache(),
        DiscoveryCallFactory(clientId)
    )

    /**
     * Discover the [OpenIdConfiguration] for the provided MCC/MNC tuple.
     * If the discovery operation contain been previously done less than 15 min ago for this MCC/MNC tuple,
     * this method return a cached [OpenIdConfiguration].
     * Otherwise it perform a Network call to get the [OpenIdConfiguration] for the provided MCC/MNC tuple.
     * If the network call fail, it fallback on a local discovery.
     *
     * @param mccMnc  The MCC and MNC of the SIM Card.
     * @param prompt   The prompt.
     * @param onSuccess the unit to invoke in case of success.
     * @param onError the Unit to invoke in case of error.
     * This Unit might be invoked with the following errors:
     * [ProviderNotFoundException] if the remote or local discovery
     * wasn't able to match an [OpenIdConfiguration] for the provided MCC/MNC tuple.
     * [HttpException] if the discovery call fail.
     */
    override fun discoverConfiguration(
        mccMnc: String?,
        prompt: Boolean,
        onSuccess: (OpenIdConfiguration) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        getOIDCInternal(mccMnc, prompt, onSuccess, onError, mccMnc?.let { cache[it] })
    }

    /**
     * @param mccMnc The MCC and MNC of the SIM Card.
     * @param prompt The prompt param to use for the request.
     * @param onSuccess the unit to invoke in case of success.
     * @param onError the Unit to invoke in case of error.
     * This Unit might be invoked with the following errors:
     * [ProviderNotFoundException] if the remote or local discovery
     * wasn't able to match an [OpenIdConfiguration] for the provided MCC/MNC tuple.
     * @param cachedOIDC a Nullable cached [OpenIdConfiguration]
     */
    internal fun getOIDCInternal(
        mccMnc: String?,
        prompt: Boolean,
        onSuccess: (OpenIdConfiguration) -> Unit,
        onError: (Throwable) -> Unit,
        cachedOIDC: OpenIdConfiguration?
    ) {

        if (cachedOIDC != null && !cachedOIDC.isExpired) {
            onSuccess.invoke(cachedOIDC)
            return
        }

        discoveryCallFactory.create(mccMnc, prompt).enqueue(
            {
                onReceiveOpenIdConfigurationResponse(mccMnc, it, onSuccess, onError)
            },
            {
                onReceiveOpenIdConfigurationFailure(it, cachedOIDC, onSuccess, onError)
            })
    }

    internal fun onReceiveOpenIdConfigurationResponse(
        mccMnc: String?,
        response: HttpResponse<DiscoveryResponse>,
        onSuccess: (OpenIdConfiguration) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (response.isSuccessful) {
            onSuccessfulResponse(mccMnc, response.body!!, onSuccess, onError)
        } else {
            onOpenIdConfigurationHttpFailure(response, onError)
        }
    }

    private fun onSuccessfulResponse(
        mccMnc: String?,
        discoveryResponse: DiscoveryResponse,
        onSuccess: (OpenIdConfiguration) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (discoveryResponse.mustShowDiscoverUI()) {
            onError.invoke(ProviderNotFoundException(discoveryResponse.discoverUIEndpoint))
        } else {
            onConfigurationDiscovered(mccMnc, discoveryResponse.configuration!!, onSuccess, onError)
        }
    }

    internal open fun onConfigurationDiscovered(
        mccMnc: String?,
        configuration: OpenIdConfiguration,
        onSuccess: (OpenIdConfiguration) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        cacheOIDC(mccMnc, configuration)
        onSuccess.invoke(configuration)
    }

    internal fun onOpenIdConfigurationHttpFailure(
        response: HttpResponse<DiscoveryResponse>,
        onError: (Throwable) -> Unit
    ) {
        if (response.code == HttpURLConnection.HTTP_NOT_FOUND) {
            onError.invoke(ProviderNotFoundException.from(response))
        } else {
            onError.invoke(HttpException(response.code, response.rawBody))
        }
    }

    internal fun onReceiveOpenIdConfigurationFailure(
        throwable: Throwable,
        cachedOIDC: OpenIdConfiguration?,
        onSuccess: (OpenIdConfiguration) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (throwable.isNetworkFailure and (cachedOIDC != null)) {
            onSuccess.invoke(cachedOIDC!!)
        } else {
            onError.invoke(throwable)
        }
    }

    internal fun cacheOIDC(
        mccMnc: String?,
        configuration: OpenIdConfiguration
    ) {
        if (mccMnc != null) {
            cache.put(mccMnc, configuration)
        } else if (configuration.mccMnc != null) {
            cache.put(configuration.mccMnc, configuration)
        }
    }
}
