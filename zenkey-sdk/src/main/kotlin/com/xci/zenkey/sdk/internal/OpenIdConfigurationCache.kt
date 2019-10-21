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
package com.xci.zenkey.sdk.internal

import android.support.annotation.VisibleForTesting

import com.xci.zenkey.sdk.internal.contract.ICache
import com.xci.zenkey.sdk.internal.model.OpenIdConfiguration

import java.util.HashMap

/**
 * A [ICache] implementation.
 * This class is cache implementation for [OpenIdConfiguration]
 * It's used for caching of [OpenIdConfiguration] received from a OpenId Discovery.
 */
internal class OpenIdConfigurationCache
    : ICache<String, OpenIdConfiguration> {

    @VisibleForTesting
    internal var configurationCache = HashMap<String, OpenIdConfiguration>()

    @Synchronized
    override fun contain(key: String): Boolean {
        return configurationCache.containsKey(key)
    }

    @Synchronized
    override fun get(key: String): OpenIdConfiguration? {
        return configurationCache[key]
    }

    @Synchronized
    override fun put(key: String, value: OpenIdConfiguration) {
        this.configurationCache[key] = value
    }

    @Synchronized
    override fun remove(key: String) {
        this.configurationCache.remove(key)
    }
}
