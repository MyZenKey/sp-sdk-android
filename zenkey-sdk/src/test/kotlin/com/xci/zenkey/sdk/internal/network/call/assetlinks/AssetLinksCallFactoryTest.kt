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
package com.xci.zenkey.sdk.internal.network.call.assetlinks

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.xci.zenkey.sdk.internal.network.stack.HttpMethod
import com.xci.zenkey.sdk.internal.network.stack.HttpRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.net.URISyntaxException

@RunWith(AndroidJUnit4::class)
class AssetLinksCallFactoryTest {

    private lateinit var callFactory: AssetLinksCallFactory

    @Before
    fun setUp() {
        callFactory = AssetLinksCallFactory()
    }

    @Test
    fun shouldContainTheCorrectStaticValues() {
        assertEquals("target", AssetLinksCallFactory.TARGET_KEY)
        assertEquals("package_name", AssetLinksCallFactory.PACKAGE_NAME_KEY)
        assertEquals("sha256_cert_fingerprints", AssetLinksCallFactory.FINGERPRINTS_KEY)
        assertEquals(".well-known", AssetLinksCallFactory.WELL_KNOW_PATH)
        assertEquals("assetlinks.json", AssetLinksCallFactory.ASSET_LINKS_PATH)
    }

    @Test
    fun shouldCreateAssetLinksCall() {
        assertNotNull(callFactory.create(AUTHORIZATION_URI))
    }

    @Test
    @Throws(URISyntaxException::class)
    fun shouldBuildCorrectPackagesRequest() {

        val path = "/" + AssetLinksCallFactory.WELL_KNOW_PATH + "/" + AssetLinksCallFactory.ASSET_LINKS_PATH

        val request = callFactory.buildPackageRequest(SCHEME, AUTHORITY)

        assertNotNull(request!!)
        assertEquals(HttpMethod.GET, request.method)
        val requestUri = request.url.toURI()
        assertEquals(SCHEME, requestUri.scheme)
        assertEquals(AUTHORITY, requestUri.authority)
        assertEquals(path, requestUri.path)
        assertEquals(HttpRequest.DEFAULT_CONNECT_TIMEOUT.toLong(), request.connectTimeout.toLong())
        assertEquals(HttpRequest.DEFAULT_READ_TIMEOUT.toLong(), request.readTimeout.toLong())
    }

    companion object {
        private const val SCHEME = "https"
        private const val AUTHORITY = "mno.com"
        private const val PATH = "authorize"
        private val AUTHORIZATION_URI = Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .path(PATH)
                .build()
    }
}
