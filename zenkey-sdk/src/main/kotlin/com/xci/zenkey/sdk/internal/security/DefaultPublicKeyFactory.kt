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
package com.xci.zenkey.sdk.internal.security

import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

/**
 * A [PublicKeyFactory] implementation.
 * This class is responsible to create certificate signature public key's.
 */
@Deprecated("Deprecated since Android 30 (R)")
internal class DefaultPublicKeyFactory internal constructor(
        private val certificateFactory: CertificateFactory,
        private val messageDigest: MessageDigest?
) : PublicKeyFactory {

    /**
     * Create the public key for a provided Certificate Signature.
     *
     * @param signature the certificate signature
     * @return the public key of the signature
     */
    @Throws(CertificateException::class)
    override fun create(signature: ByteArray): ByteArray {
        return messageDigest?.digest(generateCertificate(signature).encoded) ?: ByteArray(0)
    }

    /**
     * Generate a [Certificate] for a provided signature.
     * @param signature the signature to use
     * @return a [Certificate] corresponding to the provided signature.
     * @throws CertificateException when failing to convert the [Certificate]
     */
    @Throws(CertificateException::class)
    internal fun generateCertificate(signature: ByteArray): X509Certificate {
        return certificateFactory.generateCertificate(getByteArrayInputStream(signature)) as X509Certificate
    }

    /**
     * Create a [ByteArrayInputStream] from a byte[].
     * @param bytes the bytes to use in the [java.io.InputStream]
     * @return a [ByteArrayInputStream] from the provided bytes.
     */
    internal fun getByteArrayInputStream(bytes: ByteArray): ByteArrayInputStream {
        return ByteArrayInputStream(bytes)
    }
}
