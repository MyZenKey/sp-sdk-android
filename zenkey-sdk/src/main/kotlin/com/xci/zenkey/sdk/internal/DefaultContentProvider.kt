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

import android.content.Context
import android.support.annotation.VisibleForTesting

import com.xci.zenkey.sdk.internal.contract.AuthorizationService
import com.xci.zenkey.sdk.internal.security.FingerprintFactory
import com.xci.zenkey.sdk.internal.security.DefaultFingerprintFactory

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory

internal class DefaultContentProvider
    : BaseContentProvider() {

    private lateinit var fingerprintFactory: FingerprintFactory

    init {
        try {
            fingerprintFactory = DefaultFingerprintFactory(
                    CertificateFactory.getInstance(CERTIFICATE_FACTORY_TYPE),
                    MessageDigest.getInstance(ALGORITHM))
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }

    override fun create(clientId: String, context: Context) {
        authorizationService = DefaultAuthorizationService(
                DiscoveryService(clientId),
                DefaultAuthorizationIntentFactory(
                        DefaultNativeIntentFactory(),
                        DefaultWebIntentFactory(context),
                        AndroidPackageManager(
                                context.packageManager,
                                fingerprintFactory)),
                AndroidSimDataProvider(context),
                AuthorizationResponseFactory())
    }

    companion object {

        @VisibleForTesting
        internal val CERTIFICATE_FACTORY_TYPE = "X509"
        @VisibleForTesting
        internal val ALGORITHM = "SHA-256"

        @Volatile
        internal lateinit var authorizationService: AuthorizationService

        internal fun authorizationService(): AuthorizationService {
            return authorizationService
        }
    }
}
