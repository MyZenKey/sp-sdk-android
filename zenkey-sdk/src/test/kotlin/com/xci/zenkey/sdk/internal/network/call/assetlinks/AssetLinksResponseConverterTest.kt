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
package com.xci.zenkey.sdk.internal.network.call.assetlinks

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest

import com.xci.zenkey.sdk.util.JsonUtil.getJsonResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull

@RunWith(AndroidJUnit4::class)
@SmallTest
class AssetLinksResponseConverterTest {

    private var converter: AssetLinksResponseConverter? = null

    private val assetLinksJson: String
        get() = getJsonResponse(ClassLoader.getSystemClassLoader(), ASSET_LINKS_FILE_NAME)

    @Before
    fun setUp() {
        converter = AssetLinksResponseConverter()
    }

    @Test
    @Throws(Exception::class)
    fun shouldParsePackageResponse() {
        val packages = converter!!.convert(assetLinksJson)
        assertNotNull(packages)
        assertEquals(2, packages.size.toLong())
        val (name, fingerprints1) = packages[0]
        assertEquals(PACKAGE1, name)
        assertEquals(FINGERPRINT1, fingerprints1[0])
        assertEquals(FINGERPRINT2, fingerprints1[1])

        val (name1, fingerprints2) = packages[1]
        assertEquals(PACKAGE2, name1)
        assertEquals(FINGERPRINT3, fingerprints2[0])
    }

    companion object {
        const val ASSET_LINKS_FILE_NAME = "assetlinks.json"
        const val PACKAGE1 = "PACKAGE1"
        const val PACKAGE2 = "PACKAGE2"
        const val FINGERPRINT1 = "FINGERPRINT1"
        const val FINGERPRINT2 = "FINGERPRINT2"
        const val FINGERPRINT3 = "FINGERPRINT3"
    }
}
