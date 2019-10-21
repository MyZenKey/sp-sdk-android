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
package com.xci.zenkey.sdk.internal.network.call.discovery

import android.net.Uri
import android.support.annotation.VisibleForTesting

import com.xci.zenkey.sdk.BuildConfig
import com.xci.zenkey.sdk.internal.Json
import com.xci.zenkey.sdk.internal.model.DiscoveryResponse
import com.xci.zenkey.sdk.internal.model.OpenIdConfiguration
import com.xci.zenkey.sdk.internal.network.stack.HttpCall
import com.xci.zenkey.sdk.internal.network.stack.HttpCallFactory
import com.xci.zenkey.sdk.internal.network.stack.HttpMethod
import com.xci.zenkey.sdk.internal.network.stack.HttpRequest

import java.net.MalformedURLException


/**
 * A [IDiscoveryCallFactory] implementation
 * This class is responsible to make openId discovery request for a provided tuple of MCC/MNC.
 */
internal class DiscoveryCallFactory(
        private val clientId: String
) : HttpCallFactory(), IDiscoveryCallFactory {

    /**
     * Get the [OpenIdConfiguration] from the network for the provided MCC/MNC
     *
     * @param mcc_mnc a tuple containing the MCC and MNC of the SIM Card.
     */
    override fun create(mcc_mnc: String?, prompt: Boolean): HttpCall<DiscoveryResponse> {
        return create(buildDiscoveryRequestInternal(mcc_mnc, prompt)!!, DiscoveryResponseConverter())
    }

    /**
     * Build the discovery [HttpRequest].
     *
     * @param mcc_mnc the MCC/MNC to use for the request.
     * @return the [HttpRequest]
     */
    @VisibleForTesting
    private fun buildDiscoveryRequestInternal(mcc_mnc: String?, prompt: Boolean): HttpRequest? {
        return buildDiscoveryRequest(BuildConfig.DISCOVERY_ENDPOINT, mcc_mnc, prompt)
    }

    /**
     * Build an HttpRequest to perform discovery.
     *
     * @param mcc_mnc the MCC/MNC of the SIM card.
     * @return a [HttpRequest] to perform discovery.
     */
    @VisibleForTesting
    internal fun buildDiscoveryRequest(endpoint: String,
                                       mcc_mnc: String?,
                                       prompt: Boolean): HttpRequest? {
        try {
            val uriBuilder = Uri.parse(endpoint)
                    .buildUpon()
                    .appendPath(WELL_KNOW_PATH)
                    .appendPath(OPEN_ID_CONFIG_PATH)
                    .appendQueryParameter(Json.KEY_CLIENT_ID, clientId)

            if (prompt) {
                uriBuilder.appendQueryParameter(Json.KEY_PROMPT, true.toString())
            }

            if (mcc_mnc != null) {
                uriBuilder.appendQueryParameter(Json.KEY_MCC_MNC, mcc_mnc)
            }
            return HttpRequest.Builder(HttpMethod.GET, uriBuilder.build().toString()).build()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            return null
        }

    }

    companion object {

        @VisibleForTesting
        internal val WELL_KNOW_PATH = ".well-known"
        @VisibleForTesting
        internal val OPEN_ID_CONFIG_PATH = "openid_configuration"
    }
}
