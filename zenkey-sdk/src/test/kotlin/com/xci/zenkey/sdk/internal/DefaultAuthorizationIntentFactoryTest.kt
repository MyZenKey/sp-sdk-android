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
package com.xci.zenkey.sdk.internal

import android.content.Intent
import android.net.Uri
import android.os.Build
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.xci.zenkey.sdk.internal.contract.PackageManager
import com.xci.zenkey.sdk.internal.contract.WebIntentFactory
import com.xci.zenkey.sdk.internal.model.AuthorizationRequest
import com.xci.zenkey.sdk.internal.model.OpenIdConfiguration
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Build.VERSION_CODES.N])
class DefaultAuthorizationIntentFactoryTest {

    private val mockWebIntentFactory = mock<WebIntentFactory>()
    private val mockPackageManager = mock<PackageManager>()
    private val mockConfiguration = mock<OpenIdConfiguration>()
    private val mockRequest = mock<AuthorizationRequest>()
    private val mockUri = mock<Uri>()

    private lateinit var factory: DefaultAuthorizationIntentFactory

    @Before
    fun setUp() {
        whenever(mockConfiguration.authorizationEndpoint).thenReturn(AUTHORIZE_ENDPOINT)
        factory = DefaultAuthorizationIntentFactory(mockWebIntentFactory, mockPackageManager)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.M])
    fun shouldCreateAppAuthWebIntentIfBelowNougat() {
        val webIntent = Intent()
        whenever(mockWebIntentFactory.create(mockUri)).thenReturn(webIntent)

        val intent = factory.createAuthorizeIntent(mockUri, emptyMap())

        assertEquals(webIntent, intent)
    }

    @Test
    fun shouldCreateWebIntentIfNativeAppNotAvailable() {
        val webIntent = Intent()
        whenever(mockWebIntentFactory.create(mockUri)).thenReturn(webIntent)

        whenever(mockPackageManager.anyValidPackageFor(mockUri, emptyMap())).thenReturn(false)

        val intent = factory.createAuthorizeIntent(mockUri, emptyMap())

        verify(mockPackageManager).anyValidPackageFor(mockUri, emptyMap())

        assertEquals(webIntent, intent)
    }

    @Test
    fun shouldCreateNativeIntentIfNativeAppAvailable() {
        val uri = Uri.parse("https://test.xci.com/authorize")
        whenever(mockRequest.toAuthorizationUri(anyString())).thenReturn(uri)

        whenever(mockPackageManager.anyValidPackageFor(uri, emptyMap())).thenReturn(true)

        val intent = factory.createAuthorizeIntent(uri, emptyMap())

        verify(mockPackageManager).anyValidPackageFor(uri, emptyMap())

        assertEquals(uri, intent.data)
    }

    @Test
    fun shouldCreateDiscoverUIIntent() {
        val discoverUiIntent = Intent()
        whenever(mockWebIntentFactory.create(mockUri)).thenReturn(discoverUiIntent)

        val intent = factory.createDiscoverUIIntent(mockUri)

        verify(mockWebIntentFactory).create(mockUri)
        assertEquals(discoverUiIntent, intent)
    }

    companion object {
        private const val SCHEME = "https"
        private const val AUTHORITY = "test.xci.com"
        private const val AUTHORIZE_PATH = "authorize"
        private const val AUTHORIZE_ENDPOINT = "$SCHEME://$AUTHORITY/$AUTHORIZE_PATH"
    }
}
