/*
 * Copyright 2019 XCI JV, LLC.
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
@file:Suppress("DEPRECATION")

package com.xci.zenkey.sdk.widget

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.net.Uri
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.Button
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.*
import com.xci.zenkey.sdk.AuthorizeIntentBuilder
import com.xci.zenkey.sdk.R
import com.xci.zenkey.sdk.internal.ktx.getColorCompat
import com.xci.zenkey.sdk.param.ACR
import com.xci.zenkey.sdk.param.Prompt
import com.xci.zenkey.sdk.param.Scopes
import com.xci.zenkey.sdk.util.TestContentProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.powermock.api.mockito.PowerMockito
import org.robolectric.Robolectric.setupContentProvider

@RunWith(AndroidJUnit4::class)
class ZenKeyButtonTest {

    private lateinit var zenKeyButton: ZenKeyButton

    private val mockContext = mock<Context>()
    private val mockAttributeSet = mock<AttributeSet>()
    private val mockTypedArray = mock<TypedArray>()
    private val mockButton = mock<Button>()
    private val mockValidRedirectUri = mock<Uri>()
    private val mockIntentBuilder = PowerMockito.mock(AuthorizeIntentBuilder::class.java)
    private val mockFragment = mock<Fragment>()
    private val mockActivity = mock<Activity>()
    private val mockIntent = mock<Intent>()
    private val mockContainer = mock<ViewGroup>()

    @Before
    fun setUp() {
        setupContentProvider(TestContentProvider::class.java)
        val context = ApplicationProvider.getApplicationContext<Context>()
        COLOR_GREEN = context.getColorCompat(R.color.zenkey_green)
        COLOR_WHITE = context.getColorCompat(android.R.color.white)
        COLOR_TRANSPARENT = context.getColorCompat(android.R.color.transparent)
        PowerMockito.`when`(mockContext.obtainStyledAttributes(mockAttributeSet, R.styleable.ZenKeyButton)).thenReturn(mockTypedArray)
        whenever(mockValidRedirectUri.toString()).thenReturn("test://test")

        doCallRealMethod().whenever(mockIntentBuilder).withScopes(any())
        doCallRealMethod().whenever(mockIntentBuilder).withRedirectUri(any())
        doCallRealMethod().whenever(mockIntentBuilder).withAcrValues(any())
        doCallRealMethod().whenever(mockIntentBuilder).withNonce(anyString())
        doCallRealMethod().whenever(mockIntentBuilder).withState(anyString())
        doCallRealMethod().whenever(mockIntentBuilder).withCorrelationId(anyString())
        doCallRealMethod().whenever(mockIntentBuilder).withPrompt(any())
        doCallRealMethod().whenever(mockIntentBuilder).withContext(anyString())
        whenever(mockIntentBuilder.build()).thenReturn(mockIntent)

        zenKeyButton = ZenKeyButton(context)
    }

    @Test
    fun allConstructorsShouldWork() {
        ZenKeyButton(ApplicationProvider.getApplicationContext())
        ZenKeyButton(ApplicationProvider.getApplicationContext(), mockAttributeSet)
        ZenKeyButton(ApplicationProvider.getApplicationContext<Context>(), mockAttributeSet, 0)
        ZenKeyButton(ApplicationProvider.getApplicationContext<Context>(), mockAttributeSet, 0, 0)
    }

    @Test
    fun shouldInitSubviews() {
        assertNotNull(zenKeyButton.button)

        assertEquals("SIGN IN WITH ZENKEY", zenKeyButton.button.text.toString().toUpperCase())

        assertEquals(COLOR_TRANSPARENT.toLong(), zenKeyButton.solidColor.toLong())

        assertTrue(zenKeyButton.button.hasOnClickListeners())
    }


    @Test
    fun shouldInitDarkModeByDefault() {
        whenever(mockTypedArray.getInt(eq(R.styleable.ZenKeyButton_mode), anyInt())).thenReturn(0)


        zenKeyButton.init(mockAttributeSet)

        //verify(textView).setTextColor(COLOR_WHITE);
        //mockActivity.launchActivity(MockActivity.get(com.xci.provider.sdk.test.R.layout.verify_button_test));
        //verifyButton = mockActivityScenario.
    }

    @Test
    fun shouldInitLightModeFromAttributeSet() {
        whenever(mockTypedArray.getInt(eq(R.styleable.ZenKeyButton_mode), anyInt())).thenReturn(ZenKeyButton.Mode.LIGHT.ordinal)

        zenKeyButton.init(mockAttributeSet)

    }

    @Test
    fun shouldSetDarkMode() {

        zenKeyButton.button = mockButton
        doNothing().whenever(mockButton).setTextColor(COLOR_WHITE)

        zenKeyButton.setMode(ZenKeyButton.Mode.DARK)

        verify(mockButton).setTextColor(COLOR_WHITE)
        //assertEquals(COLOR_WHITE, verifyButton.textView.getCurrentTextColor());
        //assertEquals(COLOR_WHITE, verifyButton.imageView.getDrawable().getColorFilter().);
        //assertEquals(COLOR_GREEN, verifyButton.container.getDrawingCacheBackgroundColor());
    }

    @Test
    fun shouldSetLightMode() {

        zenKeyButton.setMode(ZenKeyButton.Mode.LIGHT)

        assertEquals(COLOR_GREEN, zenKeyButton.button.currentTextColor)
    }

    @Test
    fun shouldSetRequestCode() {
        val requestCode = 888

        zenKeyButton.setRequestCode(requestCode)

        assertEquals(requestCode, zenKeyButton.requestCode)
    }

    @Test
    fun shouldSetCustomRedirectUri() {
        zenKeyButton.intentBuilder = mockIntentBuilder
        zenKeyButton.setRedirectUri(mockValidRedirectUri)
        verify(mockIntentBuilder).withRedirectUri(mockValidRedirectUri)
    }

    @Test
    fun shouldSetScopes() {
        zenKeyButton.intentBuilder = mockIntentBuilder
        zenKeyButton.setScopes(Scopes.EMAIL, Scopes.NAME)
        verify(mockIntentBuilder).withScopes(Scopes.EMAIL, Scopes.NAME)
    }

    @Test
    fun shouldSetFragmentReference() {
        zenKeyButton.setFragment(mockFragment)
        assertEquals(mockFragment, zenKeyButton.fragment!!.get())
    }

    @Test
    fun shouldStartRequestFromFragment() {
        doNothing().whenever(mockFragment).startActivityForResult(any(), anyInt())
        zenKeyButton.setFragment(mockFragment)

        zenKeyButton.startRequest(mockContainer, Intent())

        verify(mockFragment).startActivityForResult(any(), anyInt())
    }

    @Test
    fun shouldStartRequestFromActivity() {
        doNothing().`when`(mockActivity).startActivityForResult(any(), anyInt())
        whenever(mockContainer.context).thenReturn(mockActivity)

        zenKeyButton.startRequest(mockContainer, Intent())

        verify(mockContainer).context
        verify(mockActivity).startActivityForResult(any(), anyInt())
    }

    @Test
    fun shouldStartRequestUsingCustomRequestCode() {
        val requestCode = 888
        doNothing().`when`(mockActivity).startActivityForResult(any(), eq(requestCode))
        whenever(mockContainer.context).thenReturn(mockActivity)
        zenKeyButton.setRequestCode(requestCode)

        zenKeyButton.startRequest(mockContainer, Intent())

        verify(mockContainer).context
        verify(mockActivity).startActivityForResult(any(), eq(requestCode))
    }

    @Test
    fun shouldStartRequestUsingDefaultRequestCode() {
        doNothing().`when`(mockActivity).startActivityForResult(any(), eq(ZenKeyButton.DEFAULT_REQUEST_CODE))
        whenever(mockContainer.context).thenReturn(mockActivity)

        zenKeyButton.startRequest(mockContainer, Intent())

        verify(mockContainer).context
        verify(mockActivity).startActivityForResult(any(), eq(ZenKeyButton.DEFAULT_REQUEST_CODE))
    }

    @Test
    fun shouldGetAuthorizeIntentWithSpecifiedValues() {
        zenKeyButton.intentBuilder = mockIntentBuilder
        val state = "state"
        val acrValue = ACR.AAL1
        val scope = Scopes.EMAIL
        val prompt = Prompt.CONSENT
        val nonce = "nonce"
        val correlationId = "correlationId"
        val context = "context"

        zenKeyButton.setRedirectUri(mockValidRedirectUri)
        zenKeyButton.setScopes(scope)
        zenKeyButton.setState(state)
        zenKeyButton.setAcrValues(acrValue)
        zenKeyButton.setNonce(nonce)
        zenKeyButton.setCorrelationId(correlationId)
        zenKeyButton.setPrompt(prompt)
        zenKeyButton.setContext(context)

        assertEquals(mockIntent, zenKeyButton.buildAuthorizationIntent())

        verify(mockIntentBuilder).withRedirectUri(mockValidRedirectUri)
        verify(mockIntentBuilder).withScopes(scope)
        verify(mockIntentBuilder).withState(state)
        verify(mockIntentBuilder).withAcrValues(acrValue)
        verify(mockIntentBuilder).withNonce(nonce)
        verify(mockIntentBuilder).withCorrelationId(correlationId)
        verify(mockIntentBuilder).withPrompt(prompt)
        verify(mockIntentBuilder).withContext(context)
        verify(mockIntentBuilder).build()
    }

    @Test
    fun shouldGetAuthorizeIntentAndStartRequestOnClick() {
        zenKeyButton.intentBuilder = mockIntentBuilder
        val state = "state"
        val nonce = "nonce"
        val correlationId = "correlationId"
        val acrValue = ACR.AAL1
        val scope = Scopes.EMAIL
        val prompt = Prompt.CONSENT
        val context = "context"

        whenever(mockContainer.context).thenReturn(mockActivity)
        doNothing().`when`(mockActivity).startActivityForResult(eq(mockIntent), eq(ZenKeyButton.DEFAULT_REQUEST_CODE))

        zenKeyButton.setRedirectUri(mockValidRedirectUri)
        zenKeyButton.setScopes(scope)
        zenKeyButton.setState(state)
        zenKeyButton.setAcrValues(acrValue)
        zenKeyButton.setNonce(nonce)
        zenKeyButton.setCorrelationId(correlationId)
        zenKeyButton.setPrompt(prompt)
        zenKeyButton.setContext(context)

        zenKeyButton.onClick(mockContainer)

        verify(mockIntentBuilder).withRedirectUri(mockValidRedirectUri)
        verify(mockIntentBuilder).withScopes(scope)
        verify(mockIntentBuilder).withState(state)
        verify(mockIntentBuilder).withAcrValues(acrValue)
        verify(mockIntentBuilder).withNonce(nonce)
        verify(mockIntentBuilder).withCorrelationId(correlationId)
        verify(mockIntentBuilder).withPrompt(prompt)
        verify(mockIntentBuilder).withContext(context)
        verify(mockIntentBuilder).build()

        verify(mockContainer).context
        verify(mockActivity).startActivityForResult(mockIntent, ZenKeyButton.DEFAULT_REQUEST_CODE)
    }

    @Test
    fun shouldGetIdentityProvider() {
        assertNotNull(zenKeyButton.intentBuilder)
    }

    companion object {
        private var COLOR_WHITE: Int = 0
        private var COLOR_GREEN: Int = 0
        private var COLOR_TRANSPARENT: Int = 0
    }
}