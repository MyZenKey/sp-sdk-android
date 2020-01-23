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

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever

import com.xci.zenkey.sdk.util.TestContentProvider
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.robolectric.Robolectric.setupContentProvider

@RunWith(AndroidJUnit4::class)
class BaseContentProviderTest {

    private val mockContext = mock<Context>()
    private val mockApplication = mock<Application>()
    private val mockPackageManager = mock<PackageManager>()
    private val mockApplicationInfo = mock<ApplicationInfo>()
    private val mockMetadata = mock<Bundle>()

    private lateinit var provider: BaseContentProvider

    @Before
    @Throws(PackageManager.NameNotFoundException::class)
    fun setUp() {
        whenever(mockContext.applicationContext).thenReturn(mockApplication)
        whenever(mockContext.packageManager).thenReturn(mockPackageManager)
        whenever(mockApplication.applicationContext).thenReturn(mockApplication)
        whenever(mockApplication.packageManager).thenReturn(mockPackageManager)

        whenever(mockPackageManager.getApplicationInfo(anyString(), anyInt())).thenReturn(mockApplicationInfo)
        mockApplicationInfo.metaData = mockMetadata

    }

    @Test
    fun shouldGetClientIdFromMetadata() {
        val packageName = "packageName"
        val clientIdKey = "clientIdKey"
        val clientId = "clientId"
        whenever(mockMetadata.get(clientIdKey)).thenReturn(clientId)

        provider = TestContentProvider()
        assertEquals(clientId, provider.getMetadata(mockPackageManager, packageName, clientIdKey))
    }

    //The 4 following test cases can't really happen. The SP isn't able to compile if they forget to put the manifestPlaceholder for clientId
    @Test
    @Throws(PackageManager.NameNotFoundException::class)
    fun shouldGetEmptyStringFromMetadataIfThrow() {
        val packageName = "packageName"
        val clientIdKey = "clientIdKey"
        whenever(mockPackageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA))
                .thenThrow(PackageManager.NameNotFoundException::class.java)

        provider = TestContentProvider()
        assertTrue(provider.getMetadata(mockPackageManager, packageName, clientIdKey).isEmpty())
    }

    @Test
    @Throws(PackageManager.NameNotFoundException::class)
    fun shouldGetEmptyStringFromMetadataIfAppInfoNull() {
        val packageName = "packageName"
        val clientIdKey = "clientIdKey"

        whenever(mockPackageManager.getApplicationInfo(anyString(), anyInt())).thenReturn(null)

        provider = TestContentProvider()
        assertTrue(provider.getMetadata(mockPackageManager, packageName, clientIdKey).isEmpty())
    }

    @Test
    fun shouldGetEmptyStringFromMetadataIfMetadataNull() {
        val packageName = "packageName"
        val clientIdKey = "clientIdKey"

        mockApplicationInfo.metaData = null

        provider = TestContentProvider()
        assertTrue(provider.getMetadata(mockPackageManager, packageName, clientIdKey).isEmpty())
    }

    @Test
    fun shouldGetEmptyStringFromMetadataIfMetadataValueNUll() {
        val packageName = "packageName"
        val clientIdKey = "clientIdKey"

        whenever(mockMetadata.get(clientIdKey)).thenReturn(null)

        provider = TestContentProvider()
        assertTrue(provider.getMetadata(mockPackageManager, packageName, clientIdKey).isEmpty())
    }

    /*@Test
    public void shouldExtractClientId() {
        provider = setupContentProvider(TestContentProvider.class);
        Assert.assertNotNull(BaseContentProvider.clientId);
    }*/

    @Test
    fun shouldGetApplicationContext() {
        provider = setupContentProvider(TestContentProvider::class.java)
        assertEquals(mockApplication, provider.getApplicationContext(mockContext))
        assertEquals(mockApplication, provider.getApplicationContext(mockApplication))
    }

    @Test
    fun shouldReturnZeroOnDelete() {
        provider = TestContentProvider()
        assertEquals(0, provider.delete(Uri.EMPTY, null, null).toLong())
    }

    @Test
    fun shouldReturnZeroOnUpdate() {
        provider = TestContentProvider()
        assertEquals(0, provider.update(Uri.EMPTY, null, null, null).toLong())
    }

    @Test
    fun shouldReturnNullOnInsert() {
        provider = TestContentProvider()
        assertNull(provider.insert(Uri.EMPTY, null))
    }

    @Test
    fun shouldReturnNullOnGetType() {
        provider = TestContentProvider()
        assertNull(provider.getType(Uri.EMPTY))
    }

    @Test
    fun shouldReturnNullOnQuery() {
        provider = TestContentProvider()
        assertNull(provider.query(Uri.EMPTY, null, null, null, null))
    }
}