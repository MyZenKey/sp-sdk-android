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

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import java.security.cert.CertificateException
import java.util.Arrays

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever

import org.junit.Assert.assertEquals

@RunWith(AndroidJUnit4::class)
class JavaFingerprintFactoryTest {

    private val mockPublicKeyFactory = mock<PublicKeyFactory>()
    private lateinit var fingerprintFactory: DefaultFingerprintFactory

    @Before
    fun setUp() {
        fingerprintFactory = DefaultFingerprintFactory(mockPublicKeyFactory)
    }

    @Test
    @Throws(CertificateException::class)
    fun shouldCreateFingerprint() {
        whenever(mockPublicKeyFactory.create(SIGNATURE)).thenReturn(PUBLIC_KEY)

        assertEquals(FINGERPRINT, fingerprintFactory.create(SIGNATURE))
    }

    @Test
    @Throws(CertificateException::class)
    fun shouldCreateFingerprintWithAdjustedBytesLength() {
        val bytes = ByteArray(3)
        Arrays.fill(bytes, 1.toByte())
        val expectedFingerprint = "01:01:01"

        whenever(mockPublicKeyFactory.create(SIGNATURE)).thenReturn(bytes)

        assertEquals(expectedFingerprint, fingerprintFactory.create(SIGNATURE))
    }

    @Test(expected = CertificateException::class)
    @Throws(CertificateException::class)
    fun shouldThrowCertificateException() {
        whenever(mockPublicKeyFactory.create(SIGNATURE)).thenThrow(CertificateException::class.java)

        fingerprintFactory.create(SIGNATURE)
    }

    companion object {
        private val SIGNATURE = "SIGNATURE".toByteArray()
        private val PUBLIC_KEY = "ANY".toByteArray()
        private const val FINGERPRINT = "41:4E:59"
    }
}
