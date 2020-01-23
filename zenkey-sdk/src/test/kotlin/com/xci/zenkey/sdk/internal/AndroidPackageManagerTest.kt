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
package com.xci.zenkey.sdk.internal

import android.content.Intent
import android.content.pm.*
import android.content.pm.PackageManager.*
import android.net.Uri
import android.os.Build
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.xci.zenkey.sdk.internal.model.Package
import com.xci.zenkey.sdk.internal.security.FingerprintFactory
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.security.cert.CertificateException
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AndroidPackageManagerTest {

    private val mockPackageManager = mock<PackageManager>()
    private val mockResolveInfo = mock<ResolveInfo>()
    private val mockActivityInfo = mock<ActivityInfo>()
    private val mockSignature = mock<Signature>()
    private val mockPackageInfo = mock<PackageInfo>()
    private val mockSigningInfo = mock<SigningInfo>()
    private val mockFingerprintFactory = mock<FingerprintFactory>()

    private lateinit var packageManager: AndroidPackageManager

    @Before
    fun setUp() {
        packageManager = AndroidPackageManager(mockPackageManager, mockFingerprintFactory)
    }

    @Test
    fun shouldGetAvailablePackages() {
        mockResolveInfo.activityInfo = mockActivityInfo
        mockActivityInfo.packageName = PACKAGE_NAME1
        val info = ArrayList<ResolveInfo>()
        info.add(mockResolveInfo)
        whenever(mockPackageManager.queryIntentActivities(any(Intent::class.java), eq(MATCH_DEFAULT_ONLY)))
                .thenReturn(info)

        val packages = packageManager.getAvailablePackages(Uri.EMPTY)
        assertFalse(packages.isEmpty())
        assertEquals(PACKAGE_NAME1, packages[0])

        verify(mockPackageManager).queryIntentActivities(any(Intent::class.java), eq(MATCH_DEFAULT_ONLY))
    }

    @Suppress("DEPRECATION")
    @Test
    @Throws(NameNotFoundException::class, CertificateException::class)
    @Config(sdk = [Build.VERSION_CODES.O])
    fun shouldGetFingerprintsBelowAndroidP() {
        val byteArray = ByteArray(0)
        val signatures = arrayOfNulls<Signature>(1)
        signatures[0] = mockSignature
        mockPackageInfo.signatures = signatures
        whenever(mockPackageManager.getPackageInfo(eq(PACKAGE_NAME1), eq(GET_SIGNATURES)))
                .thenReturn(mockPackageInfo)

        whenever(mockSignature.toByteArray()).thenReturn(byteArray)

        whenever(mockFingerprintFactory.create(byteArray)).thenReturn(FINGERPRINT)

        val fingerprints = packageManager.getCertificateFingerprints(PACKAGE_NAME1)

        assertFalse(fingerprints.isEmpty())
        assertEquals(FINGERPRINT, fingerprints[0])
    }

    @Test
    @Throws(NameNotFoundException::class, CertificateException::class)
    @Config(sdk = [Build.VERSION_CODES.P])
    fun shouldGetFingerprintsAboveAndroidPWithMultipleSigners() {
        val byteArray = ByteArray(0)
        val signatures = arrayOfNulls<Signature>(1)
        signatures[0] = mockSignature

        mockPackageInfo.signingInfo = mockSigningInfo
        whenever(mockSigningInfo.hasMultipleSigners()).thenReturn(true)
        whenever(mockSigningInfo.apkContentsSigners).thenReturn(signatures)

        whenever(mockPackageManager.getPackageInfo(eq(PACKAGE_NAME1), eq(GET_SIGNING_CERTIFICATES)))
                .thenReturn(mockPackageInfo)
        whenever(mockSignature.toByteArray()).thenReturn(byteArray)
        whenever(mockFingerprintFactory.create(byteArray)).thenReturn(FINGERPRINT)

        val fingerprints = packageManager.getCertificateFingerprints(PACKAGE_NAME1)

        assertFalse(fingerprints.isEmpty())
        assertEquals(FINGERPRINT, fingerprints[0])
    }

    @Test
    @Throws(NameNotFoundException::class, CertificateException::class)
    @Config(sdk = [Build.VERSION_CODES.P])
    fun shouldGetFingerprintsAboveAndroidPWithUniqueSigner() {
        val byteArray = ByteArray(0)
        val signatures = arrayOfNulls<Signature>(1)
        signatures[0] = mockSignature

        mockPackageInfo.signingInfo = mockSigningInfo
        whenever(mockSigningInfo.hasMultipleSigners()).thenReturn(false)
        whenever(mockSigningInfo.signingCertificateHistory).thenReturn(signatures)

        whenever(mockPackageManager.getPackageInfo(eq(PACKAGE_NAME1), eq(GET_SIGNING_CERTIFICATES)))
                .thenReturn(mockPackageInfo)

        whenever(mockSignature.toByteArray()).thenReturn(byteArray)

        whenever(mockFingerprintFactory.create(byteArray)).thenReturn(FINGERPRINT)

        val fingerprints = packageManager.getCertificateFingerprints(PACKAGE_NAME1)

        assertFalse(fingerprints.isEmpty())
        assertEquals(FINGERPRINT, fingerprints[0])
    }

    @Suppress("DEPRECATION")
    @Test
    @Throws(NameNotFoundException::class)
    @Config(sdk = [Build.VERSION_CODES.O])
    fun shouldNotGetFingerprintsIfNameNotFoundExceptionBelowP() {
        whenever(mockPackageManager.getPackageInfo(PACKAGE_NAME1, GET_SIGNATURES))
                .thenThrow(NameNotFoundException::class.java)

        val fingerprints = packageManager.getCertificateFingerprints(PACKAGE_NAME1)

        assertTrue(fingerprints.isEmpty())
    }

    @Test
    @Throws(NameNotFoundException::class)
    @Config(sdk = [Build.VERSION_CODES.P])
    fun shouldNotGetFingerprintsIfNameNotFoundExceptionAbovePWithMultipleSigners() {
        whenever(mockPackageManager.getPackageInfo(PACKAGE_NAME1, GET_SIGNING_CERTIFICATES))
                .thenThrow(NameNotFoundException::class.java)

        val fingerprints = packageManager.getCertificateFingerprints(PACKAGE_NAME1)

        assertTrue(fingerprints.isEmpty())
    }

    @Test
    @Throws(NameNotFoundException::class)
    @Config(sdk = [Build.VERSION_CODES.P])
    fun shouldNotGetFingerprintsIfNameNotFoundExceptionAbovePWithSingleSigners() {
        whenever(mockPackageManager.getPackageInfo(PACKAGE_NAME1, GET_SIGNING_CERTIFICATES))
                .thenThrow(NameNotFoundException::class.java)

        val fingerprints = packageManager.getCertificateFingerprints(PACKAGE_NAME1)

        assertTrue(fingerprints.isEmpty())
    }

    @Test
    fun allFingerprintsShouldBeValid() {
        val expected = ArrayList<String>()
        expected.add(FINGERPRINT)
        val actual = ArrayList<String>()
        actual.add(FINGERPRINT)
        assertTrue(packageManager.allFingerprintsAreValid(expected, actual))
    }

    @Test
    fun allFingerprintsShouldNotBeValid() {
        val expected = ArrayList<String>()
        expected.add(FINGERPRINT1)
        val actual = ArrayList<String>()
        actual.add(FINGERPRINT2)
        assertFalse(packageManager.allFingerprintsAreValid(expected, actual))
    }

    @Test
    fun allFingerprintsShouldNotBeValidIfActualContainUnexpected() {
        val actual = ArrayList<String>()
        actual.add(EXPECTED_FINGERPRINT)
        actual.add(FINGERPRINT2)
        assertFalse(packageManager.allFingerprintsAreValid(EXPECTED_FINGERPRINTS, actual))
    }

    @Suppress("DEPRECATION")
    @Test
    @Throws(NameNotFoundException::class, CertificateException::class)
    @Config(sdk = [Build.VERSION_CODES.O])
    fun shouldFoundAValidPackageBelowAndroidP() {

        val expectedPackage = Package(PACKAGE_NAME1, EXPECTED_FINGERPRINTS)
        val expectedPackages = ArrayList<Package>()
        expectedPackages.add(expectedPackage)

        mockResolveInfo.activityInfo = mockActivityInfo
        mockActivityInfo.packageName = PACKAGE_NAME1
        val info = ArrayList<ResolveInfo>()
        info.add(mockResolveInfo)
        whenever(mockPackageManager.queryIntentActivities(any(Intent::class.java), eq(MATCH_DEFAULT_ONLY)))
                .thenReturn(info)

        val actualSignatures = arrayOfNulls<Signature>(1)
        actualSignatures[0] = Signature(PUBLIC_KEY_1)
        mockPackageInfo.signatures = actualSignatures
        whenever(mockPackageManager.getPackageInfo(eq(PACKAGE_NAME1), eq(GET_SIGNATURES)))
                .thenReturn(mockPackageInfo)

        whenever(mockFingerprintFactory.create(PUBLIC_KEY_1)).thenReturn(EXPECTED_FINGERPRINT)

        assertTrue(packageManager.anyValidPackageFor(Uri.EMPTY, expectedPackages))

        verify(mockPackageManager).queryIntentActivities(any(Intent::class.java), eq(MATCH_DEFAULT_ONLY))
        verify(mockPackageManager).getPackageInfo(eq(PACKAGE_NAME1), eq(GET_SIGNATURES))
    }

    @Suppress("DEPRECATION")
    @Test
    @Throws(NameNotFoundException::class, CertificateException::class)
    @Config(sdk = [Build.VERSION_CODES.P])
    fun shouldFoundAValidPackageAboveAndroidPWithMultipleSigners() {

        val expectedPackage = Package(PACKAGE_NAME1, EXPECTED_FINGERPRINTS)
        val expectedPackages = ArrayList<Package>()
        expectedPackages.add(expectedPackage)

        mockResolveInfo.activityInfo = mockActivityInfo
        mockActivityInfo.packageName = PACKAGE_NAME1
        val info = ArrayList<ResolveInfo>()
        info.add(mockResolveInfo)
        whenever(mockPackageManager.queryIntentActivities(any(Intent::class.java), eq(MATCH_DEFAULT_ONLY)))
                .thenReturn(info)

        val actualSignatures = arrayOfNulls<Signature>(1)
        actualSignatures[0] = Signature(PUBLIC_KEY_1)

        mockPackageInfo.signingInfo = mockSigningInfo
        whenever(mockSigningInfo.hasMultipleSigners()).thenReturn(true)
        whenever(mockSigningInfo.apkContentsSigners).thenReturn(actualSignatures)

        whenever(mockPackageManager.getPackageInfo(eq(PACKAGE_NAME1), eq(GET_SIGNING_CERTIFICATES)))
                .thenReturn(mockPackageInfo)

        whenever(mockFingerprintFactory.create(PUBLIC_KEY_1)).thenReturn(EXPECTED_FINGERPRINT)

        assertTrue(packageManager.anyValidPackageFor(Uri.EMPTY, expectedPackages))

        verify(mockSigningInfo).hasMultipleSigners()
        verify(mockSigningInfo).apkContentsSigners
        verify(mockPackageManager).queryIntentActivities(any(Intent::class.java), eq(MATCH_DEFAULT_ONLY))
        verify(mockPackageManager).getPackageInfo(eq(PACKAGE_NAME1), eq(GET_SIGNING_CERTIFICATES))
    }

    @Suppress("DEPRECATION")
    @Test
    @Throws(NameNotFoundException::class, CertificateException::class)
    @Config(sdk = [Build.VERSION_CODES.P])
    fun shouldFoundAValidPackageAboveAndroidPWithSingleSigner() {

        val expectedPackage = Package(PACKAGE_NAME1, EXPECTED_FINGERPRINTS)
        val expectedPackages = ArrayList<Package>()
        expectedPackages.add(expectedPackage)

        mockResolveInfo.activityInfo = mockActivityInfo
        mockActivityInfo.packageName = PACKAGE_NAME1
        val info = ArrayList<ResolveInfo>()
        info.add(mockResolveInfo)
        whenever(mockPackageManager.queryIntentActivities(any(Intent::class.java), eq(MATCH_DEFAULT_ONLY)))
                .thenReturn(info)

        val actualSignatures = arrayOfNulls<Signature>(1)
        actualSignatures[0] = Signature(PUBLIC_KEY_1)

        mockPackageInfo.signingInfo = mockSigningInfo
        whenever(mockSigningInfo.hasMultipleSigners()).thenReturn(false)
        whenever(mockSigningInfo.signingCertificateHistory).thenReturn(actualSignatures)

        whenever(mockPackageManager.getPackageInfo(eq(PACKAGE_NAME1), eq(GET_SIGNING_CERTIFICATES)))
                .thenReturn(mockPackageInfo)

        whenever(mockFingerprintFactory.create(PUBLIC_KEY_1)).thenReturn(EXPECTED_FINGERPRINT)

        assertTrue(packageManager.anyValidPackageFor(Uri.EMPTY, expectedPackages))

        verify(mockSigningInfo).hasMultipleSigners()
        verify(mockSigningInfo).signingCertificateHistory
        verify(mockPackageManager).queryIntentActivities(any(Intent::class.java), eq(MATCH_DEFAULT_ONLY))
        verify(mockPackageManager).getPackageInfo(eq(PACKAGE_NAME1), eq(GET_SIGNING_CERTIFICATES))
    }

    @Test
    fun shouldNotFoundAnyValidPackageIfNoActivityHandlingUri() {

        val expectedPackage = Package(PACKAGE_NAME1, ArrayList())
        val expectedPackages = ArrayList<Package>()
        expectedPackages.add(expectedPackage)

        whenever(mockPackageManager.queryIntentActivities(any(Intent::class.java), eq(MATCH_DEFAULT_ONLY)))
                .thenReturn(ArrayList())

        assertFalse(packageManager.anyValidPackageFor(Uri.EMPTY, expectedPackages))

        verify(mockPackageManager).queryIntentActivities(any(Intent::class.java), eq(MATCH_DEFAULT_ONLY))
    }

    @Test
    fun shouldNotFoundAnyValidPackageIfActivityHandlingUriNotMatchingPackageName() {

        val expectedPackage = Package(PACKAGE_NAME1, ArrayList())
        val expectedPackages = ArrayList<Package>()
        expectedPackages.add(expectedPackage)

        mockResolveInfo.activityInfo = mockActivityInfo
        mockActivityInfo.packageName = PACKAGE_NAME2
        val info = ArrayList<ResolveInfo>()
        info.add(mockResolveInfo)
        whenever(mockPackageManager.queryIntentActivities(any(Intent::class.java), eq(MATCH_DEFAULT_ONLY)))
                .thenReturn(info)

        assertFalse(packageManager.anyValidPackageFor(Uri.EMPTY, expectedPackages))

        verify(mockPackageManager).queryIntentActivities(any(Intent::class.java), eq(MATCH_DEFAULT_ONLY))
    }

    @Suppress("DEPRECATION")
    @Test
    @Throws(NameNotFoundException::class, CertificateException::class)
    @Config(sdk = [Build.VERSION_CODES.O])
    fun shouldNotFoundAnyValidPackageBelowAndroidPIfMatchingPackageNameButNotMatchingAllFingerprints() {

        val expectedFingerprint = "EXPECTED_PACKAGE_FINGERPRINT"
        val expectedFingerprints = ArrayList<String>()
        expectedFingerprints.add(expectedFingerprint)
        val expectedPackage = Package(PACKAGE_NAME1, expectedFingerprints)
        val expectedPackages = ArrayList<Package>()
        expectedPackages.add(expectedPackage)

        mockResolveInfo.activityInfo = mockActivityInfo
        mockActivityInfo.packageName = PACKAGE_NAME1
        val info = ArrayList<ResolveInfo>()
        info.add(mockResolveInfo)
        whenever(mockPackageManager.queryIntentActivities(any(Intent::class.java), eq(MATCH_DEFAULT_ONLY)))
                .thenReturn(info)

        val actualSignatures = arrayOfNulls<Signature>(2)
        actualSignatures[0] = Signature(PUBLIC_KEY_1)
        actualSignatures[1] = Signature(PUBLIC_KEY_2)

        mockPackageInfo.signatures = actualSignatures

        whenever(mockPackageManager.getPackageInfo(eq(PACKAGE_NAME1), eq(GET_SIGNATURES)))
                .thenReturn(mockPackageInfo)

        whenever(mockFingerprintFactory.create(PUBLIC_KEY_1)).thenReturn(expectedFingerprint)
        whenever(mockFingerprintFactory.create(PUBLIC_KEY_2)).thenReturn("ANY")

        assertFalse(packageManager.anyValidPackageFor(Uri.EMPTY, expectedPackages))

        verify(mockPackageManager).queryIntentActivities(any(Intent::class.java), eq(MATCH_DEFAULT_ONLY))
        verify(mockPackageManager).getPackageInfo(eq(PACKAGE_NAME1), eq(GET_SIGNATURES))
    }

    @Test
    @Throws(NameNotFoundException::class, CertificateException::class)
    @Config(sdk = [Build.VERSION_CODES.P])
    fun shouldNotFoundAnyValidPackageAboveAndroidPWithMultipleSignersIfMatchingPackageNameButNotMatchingAllFingerprints() {

        val expectedFingerprint = "EXPECTED_PACKAGE_FINGERPRINT"
        val expectedFingerprints = ArrayList<String>()
        expectedFingerprints.add(expectedFingerprint)
        val expectedPackage = Package(PACKAGE_NAME1, expectedFingerprints)
        val expectedPackages = ArrayList<Package>()
        expectedPackages.add(expectedPackage)

        mockResolveInfo.activityInfo = mockActivityInfo
        mockActivityInfo.packageName = PACKAGE_NAME1
        val info = ArrayList<ResolveInfo>()
        info.add(mockResolveInfo)
        whenever(mockPackageManager.queryIntentActivities(any(Intent::class.java), eq(MATCH_DEFAULT_ONLY)))
                .thenReturn(info)


        val actualSignatures = arrayOfNulls<Signature>(2)
        actualSignatures[0] = Signature(PUBLIC_KEY_1)
        actualSignatures[1] = Signature(PUBLIC_KEY_2)

        mockPackageInfo.signingInfo = mockSigningInfo
        whenever(mockSigningInfo.hasMultipleSigners()).thenReturn(true)
        whenever(mockSigningInfo.apkContentsSigners).thenReturn(actualSignatures)

        whenever(mockPackageManager.getPackageInfo(eq(PACKAGE_NAME1), eq(GET_SIGNING_CERTIFICATES)))
                .thenReturn(mockPackageInfo)

        whenever(mockFingerprintFactory.create(PUBLIC_KEY_1)).thenReturn(expectedFingerprint)
        whenever(mockFingerprintFactory.create(PUBLIC_KEY_2)).thenReturn("ANY")

        assertFalse(packageManager.anyValidPackageFor(Uri.EMPTY, expectedPackages))

        verify(mockPackageManager).queryIntentActivities(any(Intent::class.java), eq(MATCH_DEFAULT_ONLY))
        verify(mockPackageManager).getPackageInfo(eq(PACKAGE_NAME1), eq(GET_SIGNING_CERTIFICATES))
        verify(mockSigningInfo).hasMultipleSigners()
        verify(mockSigningInfo).apkContentsSigners
    }

    @Test
    @Throws(NameNotFoundException::class, CertificateException::class)
    @Config(sdk = [Build.VERSION_CODES.P])
    fun shouldNotFoundAnyValidPackageAboveAndroidPWithSingleSignerIfMatchingPackageNameButNotMatchingAllFingerprints() {

        val expectedFingerprint = "EXPECTED_PACKAGE_FINGERPRINT"
        val expectedFingerprints = ArrayList<String>()
        expectedFingerprints.add(expectedFingerprint)
        val expectedPackage = Package(PACKAGE_NAME1, expectedFingerprints)
        val expectedPackages = ArrayList<Package>()
        expectedPackages.add(expectedPackage)

        mockResolveInfo.activityInfo = mockActivityInfo
        mockActivityInfo.packageName = PACKAGE_NAME1
        val info = ArrayList<ResolveInfo>()
        info.add(mockResolveInfo)
        whenever(mockPackageManager.queryIntentActivities(any(Intent::class.java), eq(MATCH_DEFAULT_ONLY)))
                .thenReturn(info)


        val actualSignatures = arrayOfNulls<Signature>(2)
        actualSignatures[0] = Signature(PUBLIC_KEY_1)
        actualSignatures[1] = Signature(PUBLIC_KEY_2)

        mockPackageInfo.signingInfo = mockSigningInfo
        whenever(mockSigningInfo.hasMultipleSigners()).thenReturn(false)
        whenever(mockSigningInfo.signingCertificateHistory).thenReturn(actualSignatures)

        whenever(mockPackageManager.getPackageInfo(eq(PACKAGE_NAME1), eq(GET_SIGNING_CERTIFICATES)))
                .thenReturn(mockPackageInfo)

        whenever(mockFingerprintFactory.create(PUBLIC_KEY_1)).thenReturn(expectedFingerprint)
        whenever(mockFingerprintFactory.create(PUBLIC_KEY_2)).thenReturn("ANY")

        assertFalse(packageManager.anyValidPackageFor(Uri.EMPTY, expectedPackages))

        verify(mockPackageManager).queryIntentActivities(any(Intent::class.java), eq(MATCH_DEFAULT_ONLY))
        verify(mockPackageManager).getPackageInfo(eq(PACKAGE_NAME1), eq(GET_SIGNING_CERTIFICATES))
        verify(mockSigningInfo).hasMultipleSigners()
        verify(mockSigningInfo).signingCertificateHistory
    }

    @Suppress("DEPRECATION")
    @Test
    @Throws(CertificateException::class, NameNotFoundException::class)
    @Config(sdk = [Build.VERSION_CODES.O])
    fun shouldCatchCertificateExceptionAndIgnoreCertificateBelowAndroidP() {
        val signatures = arrayOfNulls<Signature>(1)
        val byteArray = ByteArray(0)
        signatures[0] = mockSignature
        mockPackageInfo.signatures = signatures
        whenever(mockPackageManager.getPackageInfo(eq(PACKAGE_NAME1), eq(GET_SIGNATURES)))
                .thenReturn(mockPackageInfo)

        whenever(mockSignature.toByteArray()).thenReturn(byteArray)
        whenever(mockFingerprintFactory.create(byteArray)).thenThrow(CertificateException::class.java)

        val fingerprints = packageManager.getCertificateFingerprints(PACKAGE_NAME1)

        assertTrue(fingerprints.isEmpty())
    }

    @Test
    @Throws(CertificateException::class, NameNotFoundException::class)
    @Config(sdk = [Build.VERSION_CODES.P])
    fun shouldCatchCertificateExceptionAndIgnoreCertificateAboveAndroidPWithMultipleSigners() {
        val signatures = arrayOfNulls<Signature>(1)
        val byteArray = ByteArray(0)
        signatures[0] = mockSignature

        mockPackageInfo.signingInfo = mockSigningInfo
        whenever(mockSigningInfo.hasMultipleSigners()).thenReturn(true)
        whenever(mockSigningInfo.apkContentsSigners).thenReturn(signatures)

        whenever(mockPackageManager.getPackageInfo(eq(PACKAGE_NAME1), eq(GET_SIGNING_CERTIFICATES)))
                .thenReturn(mockPackageInfo)

        whenever(mockSignature.toByteArray()).thenReturn(byteArray)
        whenever(mockFingerprintFactory.create(byteArray)).thenThrow(CertificateException::class.java)

        val fingerprints = packageManager.getCertificateFingerprints(PACKAGE_NAME1)

        assertTrue(fingerprints.isEmpty())
        verify(mockSigningInfo).hasMultipleSigners()
        verify(mockSigningInfo).apkContentsSigners
    }

    @Test
    @Throws(CertificateException::class, NameNotFoundException::class)
    @Config(sdk = [Build.VERSION_CODES.P])
    fun shouldCatchCertificateExceptionAndIgnoreCertificateAboveAndroidPWithSingleSigner() {
        val signatures = arrayOfNulls<Signature>(1)
        val byteArray = ByteArray(0)
        signatures[0] = mockSignature

        mockPackageInfo.signingInfo = mockSigningInfo
        whenever(mockSigningInfo.hasMultipleSigners()).thenReturn(false)
        whenever(mockSigningInfo.signingCertificateHistory).thenReturn(signatures)

        whenever(mockPackageManager.getPackageInfo(eq(PACKAGE_NAME1), eq(GET_SIGNING_CERTIFICATES)))
                .thenReturn(mockPackageInfo)

        whenever(mockSignature.toByteArray()).thenReturn(byteArray)
        whenever(mockFingerprintFactory.create(byteArray)).thenThrow(CertificateException::class.java)

        val fingerprints = packageManager.getCertificateFingerprints(PACKAGE_NAME1)

        assertTrue(fingerprints.isEmpty())
        verify(mockSigningInfo).hasMultipleSigners()
        verify(mockSigningInfo).signingCertificateHistory
    }

    companion object {
        private const val PACKAGE_NAME1 = "PACKAGE_NAME1"
        private const val PACKAGE_NAME2 = "PACKAGE_NAME2"
        private const val FINGERPRINT = "FINGERPRINT"

        private const val FINGERPRINT1 = "FINGERPRINT1"
        private const val FINGERPRINT2 = "FINGERPRINT2"

        private val PUBLIC_KEY_1 = "PUBLIC_KEY1".toByteArray()
        private val PUBLIC_KEY_2 = "PUBLIC_KEY2".toByteArray()

        const val EXPECTED_FINGERPRINT = "EXPECTED_FINGERPRINT"
        val EXPECTED_FINGERPRINTS = listOf(EXPECTED_FINGERPRINT)
    }
}
