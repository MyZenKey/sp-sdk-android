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
package com.xci.zenkey.sdk.internal.security;

import java.security.cert.CertificateException

/**
 * Contract for a Certificate signature fingerprint factory.
 */
internal interface FingerprintFactory {

    /**
     * Create a fingerprints from a certificate signature.
     *
     * @param signature the signature of the certificate.
     * @return a fingerprint for the provider certificate signature.
     * @throws CertificateException if unable to create the fingerprint.
     */
    @Throws(CertificateException::class)
    fun create(signature: ByteArray): String

}
