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
package com.xci.zenkey.sdk.internal

import android.content.Context
import android.os.Build
import com.xci.zenkey.sdk.internal.contract.AuthorizationService
import com.xci.zenkey.sdk.internal.contract.IDiscoveryService
import com.xci.zenkey.sdk.internal.discovery.DiscoveryService16
import com.xci.zenkey.sdk.internal.discovery.DiscoveryService
import com.xci.zenkey.sdk.internal.ktx.SHA256MessageDigest
import com.xci.zenkey.sdk.internal.ktx.telephonyManager
import com.xci.zenkey.sdk.internal.security.DefaultFingerprintFactory
import java.security.cert.CertificateFactory

internal class DefaultContentProvider
    : BaseContentProvider() {

    override fun create(clientId: String, context: Context) {

        val fingerprintFactory = DefaultFingerprintFactory(
                CertificateFactory.getInstance(CERTIFICATE_FACTORY_TYPE),
                SHA256MessageDigest)

        discoveryService = if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R){
            DiscoveryService16(clientId)
        } else {
            DiscoveryService(clientId)
        }

        authorizationService = DefaultAuthorizationService(
                discoveryService,
                DefaultAuthorizationIntentFactory(
                        DefaultWebIntentFactory(context),
                        AndroidPackageManager(
                                context.packageManager,
                                fingerprintFactory)),
                context.telephonyManager,
                AuthorizationResponseFactory())
    }

    companion object {
        internal const val CERTIFICATE_FACTORY_TYPE = "X509"

        @Volatile
        internal lateinit var authorizationService: AuthorizationService

        @Volatile
        internal lateinit var discoveryService: IDiscoveryService

        internal fun authorizationService(): AuthorizationService {
            return authorizationService
        }
    }
}
