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
import com.xci.zenkey.sdk.IdentityProvider
import com.xci.zenkey.sdk.internal.OpenIdConfigurationCache
import com.xci.zenkey.sdk.internal.contract.ICache
import com.xci.zenkey.sdk.internal.contract.IDiscoveryService
import com.xci.zenkey.sdk.internal.model.DiscoveryResponse
import com.xci.zenkey.sdk.internal.model.OpenIdConfiguration
import com.xci.zenkey.sdk.internal.model.exception.AssetsNotFoundException
import com.xci.zenkey.sdk.internal.model.exception.ProviderNotFoundException
import com.xci.zenkey.sdk.internal.network.call.assetlinks.AssetLinksCallFactory
import com.xci.zenkey.sdk.internal.network.call.assetlinks.IAssetLinksCallFactory
import com.xci.zenkey.sdk.internal.network.call.discovery.DiscoveryCallFactory
import com.xci.zenkey.sdk.internal.network.call.discovery.IDiscoveryCallFactory
import com.xci.zenkey.sdk.internal.network.stack.HttpResponse

/**
 * A [IDiscoveryService] implementation.
 * This class is responsible to manage OpenId discovery process.
 * It's responsible to perform discovery using a [IDiscoveryCallFactory] implementation,
 * and to cache the received [OpenIdConfiguration] using a [ICache] implementation.
 * It's responsible to fetch assetlinks.json using a [IAssetLinksCallFactory] implementation,
 * This class isn't intended to be use directly but by a
 * [IdentityProvider] implementation.
 */
@Deprecated("Deprecated since Android 30 (R)", ReplaceWith(expression = "DiscoveryService30"))
internal class DiscoveryService16 internal constructor(
    cache: ICache<String, OpenIdConfiguration>,
    discoveryCallFactory: IDiscoveryCallFactory,
    private val assetLinksCallFactory: IAssetLinksCallFactory
) : DiscoveryService(cache, discoveryCallFactory) {

    internal constructor(clientId: String) : this(OpenIdConfigurationCache(),
        DiscoveryCallFactory(clientId),
        AssetLinksCallFactory())

    override fun onConfigurationDiscovered(
        mccMnc: String?,
        configuration: OpenIdConfiguration,
        onSuccess: (OpenIdConfiguration) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        getAssets(configuration,
            {
                cacheOIDC(mccMnc, it)
                onSuccess.invoke(it)
            }, onError)
    }

    internal fun getAssets(
        configuration: OpenIdConfiguration,
        onSuccess: (OpenIdConfiguration) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        assetLinksCallFactory.create(Uri.parse(configuration.authorizationEndpoint)).enqueue(
            {
                onReceiveAssetsResponse(it, configuration, onSuccess, onError)
            },
            {
                onError.invoke(it)
            })
    }

    private fun onReceiveAssetsResponse(
        response: HttpResponse<Map<String, List<String>>>,
        configuration: OpenIdConfiguration,
        onSuccess: (OpenIdConfiguration) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (response.isSuccessful) {
            configuration.packages = response.body!!
            onSuccess.invoke(configuration)
        } else {
            onError.invoke(AssetsNotFoundException("Unable to fetch assetLinks.json " + response.rawBody))
        }
    }
}
