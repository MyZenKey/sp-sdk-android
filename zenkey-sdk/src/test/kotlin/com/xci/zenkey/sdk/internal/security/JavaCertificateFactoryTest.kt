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

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import java.io.InputStream
import java.security.MessageDigest
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.mockito.ArgumentMatchers.any

@RunWith(AndroidJUnit4::class)
class JavaCertificateFactoryTest {
    private lateinit var certificateFactory: DefaultPublicKeyFactory

    private val mockCertificateFactory = mock<java.security.cert.CertificateFactory>()
    private val mockMessageDigest = mock<MessageDigest>()
    private val mockCertificate = mock<X509Certificate>()

    @Before
    fun setUp() {
        certificateFactory = DefaultPublicKeyFactory(mockCertificateFactory, mockMessageDigest)
    }

    @Test
    fun shouldGetByteArrayInputStream() {
        assertNotNull(certificateFactory.getByteArrayInputStream(SIGNATURE))
    }

    @Test
    @Throws(CertificateException::class)
    fun shouldCreatePublicKey() {
        whenever(mockCertificate.encoded).thenReturn(PUBLIC_KEY)
        whenever(mockCertificateFactory.generateCertificate(any(InputStream::class.java)))
                .thenReturn(mockCertificate)
        whenever(mockMessageDigest.digest(any(ByteArray::class.java))).thenReturn(PUBLIC_KEY)

        assertEquals(PUBLIC_KEY, certificateFactory.create(SIGNATURE))
    }

    @Test(expected = CertificateException::class)
    @Throws(CertificateException::class)
    fun shouldNotCreatePublicKeyWhenThrowCertificateException() {
        whenever(mockCertificateFactory.generateCertificate(any(InputStream::class.java)))
                .thenThrow(CertificateException::class.java)

        assertNull(certificateFactory.create(SIGNATURE))
    }

    @Test
    @Throws(java.security.cert.CertificateException::class)
    fun shouldGenerateCertificate() {
        whenever(mockCertificateFactory.generateCertificate(any(InputStream::class.java)))
                .thenReturn(mockCertificate)

        assertEquals(mockCertificate, certificateFactory.generateCertificate(SIGNATURE))
    }

    @Test(expected = CertificateException::class)
    @Throws(CertificateException::class)
    fun shouldThrowCertificateExceptionWhenGenerateCertificate() {
        whenever(mockCertificateFactory.generateCertificate(any(InputStream::class.java)))
                .thenThrow(CertificateException::class.java)

        certificateFactory.generateCertificate(SIGNATURE)
    }

    companion object {
        private val SIGNATURE = "SIGNATURE".toByteArray()
        private val PUBLIC_KEY = "PUBLIC_KEY".toByteArray()
    }
}