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

import android.net.Uri
import android.util.Base64
import com.xci.zenkey.sdk.internal.ktx.encodeToString
import com.xci.zenkey.sdk.internal.model.AuthorizationRequest
import com.xci.zenkey.sdk.param.ACR
import com.xci.zenkey.sdk.param.Prompt
import com.xci.zenkey.sdk.param.Scopes
import com.xci.zenkey.sdk.param.Theme
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.security.MessageDigest

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class DefaultAuthorizeIntentBuilderTest {

    private lateinit var intentBuilder: DefaultAuthorizeIntentBuilder

    private val mockMessageDigest = Mockito.mock(MessageDigest::class.java)

    @Before
    fun setUp() {
        intentBuilder = DefaultAuthorizeIntentBuilder(PACKAGE_NAME, CLIENT_ID, mockMessageDigest, REDIRECT_URI)
    }

    @Test
    fun shouldRemoveDuplicateScopes() {
        intentBuilder.withScopes(Scopes.EMAIL, Scopes.EMAIL, Scopes.OPEN_ID)
        assertEquals("email openid",intentBuilder.scope())
    }

    @Test
    fun shouldBuildScopeString() {
        intentBuilder.withScopes(Scopes.EMAIL, Scopes.NAME, Scopes.OPEN_ID)
        assertEquals("email name openid", intentBuilder.scope())
    }

    @Test
    fun shouldHaveNullScope() {
        intentBuilder.withScopes()
        assertNull(intentBuilder.scope())

    }

    @Test
    fun shouldUseDefaultScope() {
        assertEquals("openid", intentBuilder.scope())
    }

    @Test
    fun shouldUseSpecifiedScope() {
        intentBuilder.withScopes(Scopes.EMAIL, Scopes.NAME)
        assertEquals("email name", intentBuilder.scope())
    }

    @Test
    fun shouldGetNullPrompt() {
        assertNull(intentBuilder.prompt())
    }

    @Test
    fun shouldGetNotNullPrompt() {
        intentBuilder.withPrompt(Prompt.CONSENT, Prompt.LOGIN)
        assertEquals(Prompt.CONSENT.value + " " + Prompt.LOGIN.value, intentBuilder.prompt())
    }

    @Test
    fun shouldGetNullACR() {
        assertNull(intentBuilder.acr())
    }

    @Test
    fun shouldGetNotNullACR() {
        intentBuilder.withAcrValues(ACR.AAL1, ACR.AAL2)
        assertEquals(ACR.AAL1.value + " " + ACR.AAL2.value, intentBuilder.acr())
    }

    @Test
    fun shouldGetNotNullStateByDefault() {
        assertNotNull(intentBuilder.state)
    }

    @Test
    fun shouldGetNullState() {
        intentBuilder.withoutState()
        assertNull(intentBuilder.state)
    }

    @Test
    fun shouldHaveNullOptionsByDefault() {
        assertNull(intentBuilder.options())
    }

    @Test
    fun shouldSetDarkThemeOption() {
        intentBuilder.withTheme(Theme.DARK)
        assertEquals("dark", intentBuilder.options())
    }

    @Test
    fun shouldSetLightThemeOption() {
        intentBuilder.withTheme(Theme.LIGHT)
        assertEquals("light", intentBuilder.options())
    }

    @Test
    fun shouldNotSetThemeOption() {
        intentBuilder.withTheme(null)
        assertNull(intentBuilder.options())
    }

    @Test
    fun shouldBuildIntentWithExpectedValues() {

        val context = "context"
        val scope = Scopes.NAME
        val state = "state"
        val acr = ACR.AAL1
        val nonce = "nonce"
        val correlationId = "correlationId"
        val prompt = Prompt.CONSENT
        val theme = Theme.DARK

        val intent = intentBuilder
                .withScopes(scope)
                .withState(state)
                .withAcrValues(acr)
                .withNonce(nonce)
                .withPrompt(prompt)
                .withCorrelationId(correlationId)
                .withContext(context)
                .withTheme(theme)
                .build()

        assertNotNull(intent)
        val extra = intent.extras
        assertNotNull(extra)
        val request = extra!!.getParcelable<AuthorizationRequest>(DefaultAuthorizationService.EXTRA_KEY_REQUEST)
        assertNotNull(request)

        assertEquals(CLIENT_ID, request!!.clientId)
        assertEquals(REDIRECT_URI, request.redirectUri)
        assertEquals(scope.value, request.scope)
        assertEquals(state.encodeToString(flags = Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING), request.state)
        assertEquals(acr.value, request.acr)
        assertEquals(nonce, request.nonce)
        assertEquals(prompt.value, request.prompt)
        assertEquals(correlationId, request.correlationId)
        assertEquals(context, request.context)
        assertEquals(theme.value, request.options)
    }

    companion object {
        private const val PACKAGE_NAME = "com.package"
        private const val CLIENT_ID = "client-id"
        private val REDIRECT_URI = Uri.Builder()
                .scheme(CLIENT_ID)
                .authority(PACKAGE_NAME)
                .build()
    }
}
