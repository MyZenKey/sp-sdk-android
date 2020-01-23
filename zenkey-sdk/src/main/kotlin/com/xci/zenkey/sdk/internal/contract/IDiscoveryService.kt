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
package com.xci.zenkey.sdk.internal.contract

import com.xci.zenkey.sdk.internal.model.OpenIdConfiguration
import com.xci.zenkey.sdk.internal.model.exception.ProviderNotFoundException

/**
 * A contract for the Discovery Service.
 */
internal interface IDiscoveryService {

    /**
     * Discover the [OpenIdConfiguration] for the provided MCC/MNC tuple.
     * If the discovery operation contain been previously done less than 15 min ago for this MCC/MNC tuple,
     * this method return a cached [OpenIdConfiguration].
     * Otherwise it perform a Network call to get the [OpenIdConfiguration] for the provided MCC/MNC tuple.
     * If the network call fail, it fallback on a local discovery.
     *
     * @param mccMnc  a tuple containing the MCC and MNC of the SIM Card.
     * @param prompt   the prompt parameter.
     * @param listener the listener receiving the result of the [OpenIdConfiguration] discovery.
     * This listener might receive the following errors:
     * [ProviderNotFoundException] if the remote or local discovery
     * wasn't able to match an [OpenIdConfiguration] for the provided MCC/MNC tuple.
     */
    fun discoverConfiguration(mccMnc: String?,
                              prompt: Boolean,
                              onSuccess: (OpenIdConfiguration) -> Unit,
                              onError: (Throwable) -> Unit)

}
