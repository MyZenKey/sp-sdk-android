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
package com.xci.zenkey.sdk.internal.ktx

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.*
import android.os.Build
import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
class PackageManagerTest {

    private val mockPackageManager = mock<PackageManager>()
    private val mockApplicationInfo = mock<ApplicationInfo>()
    private val mockPackageInfo = mock<PackageInfo>()

    @Suppress("DEPRECATION")
    @Test
    @Config(sdk = [Build.VERSION_CODES.O])
    fun `should get packageInfo below P`() {
        whenever(mockPackageManager.getPackageInfo(eq(PACKAGE_NAME1), eq(GET_SIGNATURES)))
                .thenReturn(mockPackageInfo)

        assertEquals(mockPackageInfo, mockPackageManager.getPackageInfoCompat(PACKAGE_NAME1))
    }

    @Suppress("DEPRECATION")
    @Test(expected = NameNotFoundException::class)
    @Config(sdk = [Build.VERSION_CODES.O])
    fun `should not get packageInfo below P if NameNotFoundException`() {
        whenever(mockPackageManager.getPackageInfo(eq(PACKAGE_NAME1), eq(GET_SIGNATURES)))
                .thenThrow(NameNotFoundException::class.java)

        mockPackageManager.getPackageInfoCompat(PACKAGE_NAME1)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.P])
    fun `should get packageInfo above P`() {
        whenever(mockPackageManager.getPackageInfo(eq(PACKAGE_NAME1), eq(GET_SIGNING_CERTIFICATES)))
                .thenReturn(mockPackageInfo)

        assertEquals(mockPackageInfo, mockPackageManager.getPackageInfoCompat(PACKAGE_NAME1))
    }

    @Suppress("DEPRECATION")
    @Test(expected = NameNotFoundException::class)
    @Config(sdk = [Build.VERSION_CODES.P])
    fun `should not get packageInfo above P if NameNotFoundException`() {
        whenever(mockPackageManager.getPackageInfo(eq(PACKAGE_NAME1), eq(GET_SIGNING_CERTIFICATES)))
                .thenThrow(NameNotFoundException::class.java)

        mockPackageManager.getPackageInfoCompat(PACKAGE_NAME1)
    }

    @Test
    fun `should get metadata bundle`() {
        val metadata = Bundle()
        val packageName = "packageName"

        mockApplicationInfo.metaData = metadata
        whenever(mockPackageManager.getApplicationInfo(packageName, GET_META_DATA))
                .thenReturn(mockApplicationInfo)

        assertEquals(metadata, mockPackageManager.metadata(packageName))

        verify(mockPackageManager).getApplicationInfo(packageName, GET_META_DATA)
    }

    @Test
    fun `should not get metadata bundle if NameNotFoundException`() {
        val packageName = "packageName"
        whenever(mockPackageManager.getApplicationInfo(packageName, GET_META_DATA))
                .thenThrow(NameNotFoundException::class.java)

        assertNull(mockPackageManager.metadata(packageName))

        verify(mockPackageManager).getApplicationInfo(packageName, GET_META_DATA)
    }

    companion object {
        private const val PACKAGE_NAME1 = "PACKAGE_NAME1"
    }
}