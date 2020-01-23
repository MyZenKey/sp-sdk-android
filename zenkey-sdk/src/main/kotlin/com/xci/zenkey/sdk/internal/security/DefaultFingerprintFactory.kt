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
package com.xci.zenkey.sdk.internal.security

import com.xci.zenkey.sdk.internal.ktx.hash
import java.security.MessageDigest
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory

/**
 * Implementation of [FingerprintFactory]
 * This class is responsible to create fingerprint of certificate signature.
 */
internal class DefaultFingerprintFactory internal constructor(
        private val publicKeyFactory: PublicKeyFactory
) : FingerprintFactory {

    constructor(certificateFactory: CertificateFactory,
                messageDigest: MessageDigest) : this(DefaultPublicKeyFactory(certificateFactory, messageDigest)) {
    }

    /**
     * Create a fingerprints from a certificate signature.
     *
     * @param signature the signature of the certificate.
     * @return a fingerprint for the provider certificate signature.
     */
    @Throws(CertificateException::class)
    override fun create(signature: ByteArray): String {
        return publicKeyFactory.create(signature).hash(separator = ":", upperCase = true)
    }
}