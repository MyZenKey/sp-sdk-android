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

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.TelephonyManager

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.powermock.api.mockito.PowerMockito.`when`
import org.robolectric.Robolectric.setupContentProvider

@RunWith(RobolectricTestRunner::class)
class DefaultContentProviderTest {

    private val mockContext = mock(Context::class.java)
    private val mockApplication = mock(Application::class.java)
    private val mockPackageManager = mock(PackageManager::class.java)
    private val mockTelephonyManager = mock(TelephonyManager::class.java)
    private val mockApplicationInfo = mock(ApplicationInfo::class.java)
    private val mockMetadata = mock(Bundle::class.java)

    private var provider: DefaultContentProvider? = null

    @Test
    fun shouldUseX509Factory() {
        assertEquals("X509", DefaultContentProvider.CERTIFICATE_FACTORY_TYPE)
    }

    @Before
    @Throws(PackageManager.NameNotFoundException::class)
    fun setup() {
        `when`(mockContext.packageManager).thenReturn(mockPackageManager)
        `when`(mockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager)
        `when`(mockContext.applicationContext).thenReturn(mockApplication)

        `when`(mockApplication.packageManager).thenReturn(mockPackageManager)
        `when`(mockApplication.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager)
        `when`(mockApplication.applicationContext).thenReturn(mockApplication)

        `when`(mockContext.packageName).thenReturn("")
        `when`(mockApplication.packageName).thenReturn("")

        `when`(mockPackageManager.getApplicationInfo(anyString(), anyInt())).thenReturn(mockApplicationInfo)
        mockApplicationInfo.metaData = mockMetadata
        `when`(mockMetadata.get(anyString())).thenReturn("")
    }

    @Test
    fun shouldInitSDKOnCreate() {
        provider = setupContentProvider(DefaultContentProvider::class.java)

        Assert.assertNotNull(BaseContentProvider.identityProvider())
        Assert.assertNotNull(DefaultContentProvider.authorizationService())
    }

    @Test
    fun shouldGetApplicationContext() {
        provider = setupContentProvider(DefaultContentProvider::class.java)
        assertEquals(provider!!.getApplicationContext(mockContext), mockApplication)
        assertEquals(provider!!.getApplicationContext(mockApplication), mockApplication)
    }

    @Test
    fun shouldReturnZeroOnDelete() {
        provider = DefaultContentProvider()
        assertEquals(0, provider!!.delete(Uri.EMPTY, null, null).toLong())
    }

    @Test
    fun shouldReturnZeroOnUpdate() {
        provider = DefaultContentProvider()
        assertEquals(0, provider!!.update(Uri.EMPTY, null, null, null).toLong())
    }

    @Test
    fun shouldReturnNullOnInsert() {
        provider = DefaultContentProvider()
        assertNull(provider!!.insert(Uri.EMPTY, null))
    }

    @Test
    fun shouldReturnNullOnGetType() {
        provider = DefaultContentProvider()
        assertNull(provider!!.getType(Uri.EMPTY))
    }

    @Test
    fun shouldReturnNullOnQuery() {
        provider = DefaultContentProvider()
        assertNull(provider!!.query(Uri.EMPTY, null, null, null, null))
    }
}
