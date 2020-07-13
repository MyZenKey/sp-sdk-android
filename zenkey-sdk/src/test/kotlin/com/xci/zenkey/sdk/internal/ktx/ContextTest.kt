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

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_META_DATA
import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.xci.zenkey.sdk.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContextTest {

    private val mockContext = mock<Context>()
    private val mockPackageManager = mock<PackageManager>()
    private val mockApplicationInfo = mock<ApplicationInfo>()

    @Before
    @Throws(PackageManager.NameNotFoundException::class)
    fun setUp() {
        whenever(mockContext.getString(R.string.zenkey_client_id)).thenReturn(METADATA_KEY)
        whenever(mockContext.packageName).thenReturn(PACKAGE_NAME)
        whenever(mockContext.packageManager).thenReturn(mockPackageManager)
        whenever(mockPackageManager.getApplicationInfo(PACKAGE_NAME, GET_META_DATA))
                .thenReturn(mockApplicationInfo)

        val metadata = Bundle()
        metadata.putString(METADATA_KEY, CLIENT_ID)
        mockApplicationInfo.metaData = metadata
    }

    @Test
    fun `should get clientId from metadata`() {
        assertEquals(CLIENT_ID, mockContext.clientId)
    }

    //The 2 following test cases can't really happen.
    //The SP isn't able to compile if they forget to put the manifestPlaceholder for clientId
    @Test
    @Throws(PackageManager.NameNotFoundException::class)
    fun `should get empty string from metadata if throw NameNotFoundException`() {
        whenever(mockPackageManager.getApplicationInfo(PACKAGE_NAME, GET_META_DATA))
                .thenThrow(PackageManager.NameNotFoundException::class.java)

        assertTrue(mockContext.clientId.isEmpty())
    }

    @Test
    fun `should get empty string from metadata if metadata is null`() {
        mockApplicationInfo.metaData = Bundle()
        assertTrue(mockContext.clientId.isEmpty())
    }

    companion object {
        const val PACKAGE_NAME = "PACKAGE_NAME"
        const val METADATA_KEY = "METADATA_KEY"
        const val CLIENT_ID = "CLIENT_ID"
    }
} 