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

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.*
import com.xci.zenkey.sdk.AuthorizationError
import com.xci.zenkey.sdk.AuthorizationResponse
import com.xci.zenkey.sdk.internal.DefaultAuthorizationService.Companion.EXTRA_KEY_CANCELLATION_INTENT
import com.xci.zenkey.sdk.internal.DefaultAuthorizationService.Companion.EXTRA_KEY_COMPLETION_INTENT
import com.xci.zenkey.sdk.internal.DefaultAuthorizationService.Companion.EXTRA_KEY_FAILURE_INTENT
import com.xci.zenkey.sdk.internal.DefaultAuthorizationService.Companion.EXTRA_KEY_REQUEST
import com.xci.zenkey.sdk.internal.DefaultAuthorizationService.Companion.EXTRA_KEY_STATE
import com.xci.zenkey.sdk.internal.DefaultAuthorizationService.Companion.EXTRA_KEY_SUCCESS_INTENT
import com.xci.zenkey.sdk.internal.contract.AuthorizationIntentFactory
import com.xci.zenkey.sdk.internal.contract.SimDataProvider
import com.xci.zenkey.sdk.internal.ktx.isUserNotFoundError
import com.xci.zenkey.sdk.internal.ktx.setResultCanceledAndFinish
import com.xci.zenkey.sdk.internal.ktx.setResultOKAndFinish
import com.xci.zenkey.sdk.internal.ktx.updateState
import com.xci.zenkey.sdk.internal.model.AuthorizationRequest
import com.xci.zenkey.sdk.internal.model.AuthorizationState
import com.xci.zenkey.sdk.internal.model.OpenIdConfiguration
import com.xci.zenkey.sdk.internal.model.exception.ProviderNotFoundException
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyString

@RunWith(AndroidJUnit4::class)
class DefaultAuthorizationServiceTest {

    private val mockActivity = mock<Activity>()
    private val mockDiscoveryService = mock<DiscoveryService>()
    private val mockIntentFactory = mock<AuthorizationIntentFactory>()
    private val mockSimDataProvider = mock<SimDataProvider>()
    private val mockResponseFactory = mock<AuthorizationResponse.Factory>()
    private val mockRequest = mock<AuthorizationRequest>()
    private val mockResponse = mock<AuthorizationResponse>()
    private val mockConfiguration = mock<OpenIdConfiguration>()
    private val mockAuthorizeUri = mock<Uri>()
    private val mockDiscoverUIUri = mock<Uri>()
    private val mockSuccessIntent = mock<PendingIntent>()
    private val mockFailureIntent = mock<PendingIntent>()
    private val mockCompletionIntent = mock<PendingIntent>()
    private val mockCancellationIntent = mock<PendingIntent>()
    private val mockResponseIntent = mock<Intent>()
    private val mockBundleExtra = mock<Bundle>()
    private val mockStartIntent = mock<Intent>()

    private lateinit var authorizationService: DefaultAuthorizationService
    private var oidcSuccessUnitCaptor: KArgumentCaptor<(OpenIdConfiguration) -> Unit> = argumentCaptor()
    private var oidcErrorUnitCaptor: KArgumentCaptor<(Throwable) -> Unit> = argumentCaptor()

    @Before
    fun setUp() {
        whenever(mockConfiguration.authorizationEndpoint).thenReturn(AUTHORIZE_ENDPOINT)
        doCallRealMethod().whenever(mockRequest).withLoginHintToken(anyString())
        whenever(mockRequest.toAuthorizationUri(AUTHORIZE_ENDPOINT)).thenReturn(mockAuthorizeUri)
        whenever(mockRequest.toDiscoverUiUri(DISCOVER_UI_ENDPOINT)).thenReturn(mockDiscoverUIUri)
        whenever(mockRequest.redirectUri).thenReturn(REDIRECT_URI)

        authorizationService = DefaultAuthorizationService(
                mockDiscoveryService,
                mockIntentFactory,
                mockSimDataProvider,
                mockResponseFactory)
    }

    @Test
    fun shouldFinishIfExtraAndSavedInstanceStateAreNull() {
        doNothing().whenever(mockActivity).finish()

        authorizationService.onCreate(mockActivity, Intent(), null)

        verify(mockActivity).finish()
    }

    @Test
    fun shouldNotFinishIfIntentExtraNotNull() {
        whenever(mockBundleExtra.getParcelable<AuthorizationRequest>(EXTRA_KEY_REQUEST)).thenReturn(mockRequest)
        whenever(mockStartIntent.extras).thenReturn(mockBundleExtra)
        doNothing().whenever(mockActivity).overridePendingTransition(0, 0)

        authorizationService.onCreate(mockActivity, mockStartIntent, null)

        verify(mockActivity, never()).finish()
        verify(mockActivity).overridePendingTransition(0, 0)
    }

    @Test
    fun shouldNotFinishIfSavedInstanceStateNotNull() {
        whenever(mockBundleExtra.getParcelable<AuthorizationRequest>(EXTRA_KEY_REQUEST)).thenReturn(mockRequest)
        doNothing().whenever(mockActivity).overridePendingTransition(0, 0)

        authorizationService.onCreate(mockActivity, Intent(), mockBundleExtra)

        verify(mockActivity, never()).finish()
        verify(mockActivity).overridePendingTransition(0, 0)
    }

    @Test
    fun shouldSetStateToNone() {
        whenever(mockBundleExtra.getParcelable<AuthorizationRequest>(EXTRA_KEY_REQUEST)).thenReturn(mockRequest)
        whenever(mockStartIntent.extras).thenReturn(mockBundleExtra)
        doNothing().whenever(mockActivity).overridePendingTransition(0, 0)

        authorizationService.onCreate(mockActivity, mockStartIntent, null)

        verify(mockActivity, never()).finish()
        verify(mockActivity).overridePendingTransition(0, 0)

        assertEquals(AuthorizationState.NONE, authorizationService.state)
    }

    @Test
    fun shouldExtractValuesFromIntentExtra() {
        val extra = Bundle()
        extra.putParcelable(EXTRA_KEY_REQUEST, mockRequest)
        extra.putParcelable(EXTRA_KEY_SUCCESS_INTENT, mockSuccessIntent)
        extra.putParcelable(EXTRA_KEY_FAILURE_INTENT, mockFailureIntent)
        extra.putParcelable(EXTRA_KEY_COMPLETION_INTENT, mockCompletionIntent)
        extra.putParcelable(EXTRA_KEY_CANCELLATION_INTENT, mockCancellationIntent)

        doNothing().whenever(mockActivity).overridePendingTransition(0, 0)

        authorizationService.onCreate(mockActivity, Intent().putExtras(extra), null)

        verify(mockActivity, never()).finish()
        verify(mockActivity).overridePendingTransition(0, 0)

        assertEquals(mockRequest, authorizationService.request)
        assertEquals(AuthorizationState.NONE, authorizationService.state)
        assertEquals(mockSuccessIntent, authorizationService.successIntent)
        assertEquals(mockFailureIntent, authorizationService.failureIntent)
        assertEquals(mockCompletionIntent, authorizationService.completionIntent)
        assertEquals(mockCancellationIntent, authorizationService.cancellationIntent)
        assertNull(authorizationService.mccMnc)
    }

    @Test
    fun shouldExtractValuesFromSavedInstanceState() {
        val state = AuthorizationState.AUTHORIZE
        val savedInstanceState = Bundle()
        savedInstanceState.putParcelable(EXTRA_KEY_REQUEST, mockRequest)
        savedInstanceState.putString(Json.KEY_MCC_MNC, MCC_MNC)
        savedInstanceState.putSerializable(EXTRA_KEY_STATE, state)
        savedInstanceState.putParcelable(EXTRA_KEY_SUCCESS_INTENT, mockSuccessIntent)
        savedInstanceState.putParcelable(EXTRA_KEY_FAILURE_INTENT, mockFailureIntent)
        savedInstanceState.putParcelable(EXTRA_KEY_COMPLETION_INTENT, mockCompletionIntent)
        savedInstanceState.putParcelable(EXTRA_KEY_CANCELLATION_INTENT, mockCancellationIntent)

        doNothing().whenever(mockActivity).overridePendingTransition(0, 0)

        authorizationService.onCreate(mockActivity, Intent(), savedInstanceState)

        verify(mockActivity, never()).finish()
        verify(mockActivity).overridePendingTransition(0, 0)

        assertEquals(state, authorizationService.state)
        assertEquals(mockRequest, authorizationService.request)
        assertEquals(mockSuccessIntent, authorizationService.successIntent)
        assertEquals(mockFailureIntent, authorizationService.failureIntent)
        assertEquals(mockCompletionIntent, authorizationService.completionIntent)
        assertEquals(mockCancellationIntent, authorizationService.cancellationIntent)
        assertEquals(MCC_MNC, authorizationService.mccMnc)
    }

    @Test
    fun shouldSetIntentOnNewIntent() {
        val intent = Intent()

        doNothing().whenever(mockActivity).intent = intent

        authorizationService.onNewIntent(mockActivity, intent)

        verify(mockActivity).intent = intent
    }

    @Test
    fun shouldCheckStateOnResume() {
        authorizationService.request = mockRequest
        val intent = Intent()
        val spy = spy(authorizationService)

        spy.onResume(mockActivity, intent)

        verify(spy).checkState(mockActivity, intent)
    }

    @Test
    fun shouldCallOnStateNone() {
        val intent = Intent()
        val spy = spy(authorizationService)
        spy.updateState(AuthorizationState.NONE)
        doNothing().whenever(spy).onStateNone(mockActivity)

        spy.checkState(mockActivity, intent)

        verify(spy).onStateNone(mockActivity)
    }

    @Test
    fun shouldCallOnStateDiscovery() {
        val uri = Uri.EMPTY
        val intent = Intent().setData(uri)
        val spy = spy(authorizationService)
        spy.updateState(AuthorizationState.DISCOVER_UI)
        doNothing().whenever(spy).onStateDiscoverUI(mockActivity, uri)

        spy.checkState(mockActivity, intent)

        verify(spy).onStateDiscoverUI(mockActivity, uri)
    }

    @Test
    fun shouldCallOnStateAuthorize() {
        val uri = Uri.EMPTY
        val intent = Intent().setData(uri)
        val spy = spy(authorizationService)
        spy.updateState(AuthorizationState.AUTHORIZE)
        doNothing().whenever(spy).onStateAuthorize(mockActivity, uri)

        spy.checkState(mockActivity, intent)

        verify(spy).onStateAuthorize(mockActivity, uri)
    }

    @Test
    fun shouldCallOnStateDiscoverUserNotFound() {
        val uri = Uri.EMPTY
        val intent = Intent().setData(uri)
        val spy = spy(authorizationService)
        spy.updateState(AuthorizationState.DISCOVER_USER_NOT_FOUND)
        doNothing().whenever(spy).onStateDiscoverUserNotFound(mockActivity, uri)

        spy.checkState(mockActivity, intent)

        verify(spy).onStateDiscoverUserNotFound(mockActivity, uri)
    }

    @Test
    fun shouldSetResultAndFinish() {
        val uri = Uri.EMPTY
        authorizationService.request = mockRequest
        authorizationService.mccMnc = MCC_MNC
        val intent = Intent()
        whenever(mockResponse.toIntent()).thenReturn(intent)
        whenever(mockResponseFactory.create(MCC_MNC, mockRequest, uri)).thenReturn(mockResponse)
        doNothing().whenever(mockActivity).setResult(Activity.RESULT_OK, intent)
        doNothing().whenever(mockActivity).finish()

        val redirectIntent = Intent().setData(uri)
        authorizationService.updateState(AuthorizationState.AUTHORIZE_USER_NOT_FOUND)

        authorizationService.checkState(mockActivity, redirectIntent)

        verify(mockResponseFactory).create(MCC_MNC, mockRequest, uri)
        verify(mockActivity).setResult(Activity.RESULT_OK, intent)
        verify(mockActivity).finish()
    }

    @Test
    fun shouldSetCancelledAndFinishIfNotNoneStateAndNoData() {
        authorizationService.request = mockRequest
        doNothing().whenever(mockActivity).setResult(Activity.RESULT_CANCELED)
        doNothing().whenever(mockActivity).finish()

        val redirectIntent = Intent()
        authorizationService.updateState(AuthorizationState.AUTHORIZE)

        authorizationService.checkState(mockActivity, redirectIntent)

        verify(mockActivity).setResult(Activity.RESULT_CANCELED)
        verify(mockActivity).finish()
    }

    @Test
    fun shouldSaveStateOnSaveInstanceState() {
        val outState = Bundle()

        authorizationService.mccMnc = MCC_MNC
        authorizationService.updateState(AuthorizationState.AUTHORIZE_USER_NOT_FOUND)
        authorizationService.request = mockRequest

        authorizationService.onSaveInstanceState(outState)

        assertEquals(MCC_MNC, outState.getString(Json.KEY_MCC_MNC))
        assertEquals(AuthorizationState.AUTHORIZE_USER_NOT_FOUND, outState.getSerializable(EXTRA_KEY_STATE))
        assertEquals(mockRequest, outState.getParcelable(EXTRA_KEY_REQUEST) as AuthorizationRequest)
    }

    @Test
    fun shouldSaveState() {
        val outState = Bundle()

        authorizationService.mccMnc = MCC_MNC
        authorizationService.state = AuthorizationState.AUTHORIZE_USER_NOT_FOUND
        authorizationService.request = mockRequest
        authorizationService.successIntent = mockSuccessIntent
        authorizationService.failureIntent = mockFailureIntent
        authorizationService.completionIntent = mockCompletionIntent
        authorizationService.cancellationIntent = mockCancellationIntent

        authorizationService.saveState(outState)

        assertEquals(MCC_MNC, outState.getString(Json.KEY_MCC_MNC))
        assertEquals(AuthorizationState.AUTHORIZE_USER_NOT_FOUND, outState.getSerializable(EXTRA_KEY_STATE))
        assertEquals(mockSuccessIntent, outState.getParcelable(EXTRA_KEY_SUCCESS_INTENT) as PendingIntent)
        assertEquals(mockFailureIntent, outState.getParcelable(EXTRA_KEY_FAILURE_INTENT) as PendingIntent)
        assertEquals(mockCompletionIntent, outState.getParcelable(EXTRA_KEY_COMPLETION_INTENT) as PendingIntent)
        assertEquals(mockCancellationIntent, outState.getParcelable(EXTRA_KEY_CANCELLATION_INTENT) as PendingIntent)
        //assertEquals(mockRequest, outState.getSerializable(EXTRA_KEY_REQUEST));
    }

    @Test
    fun shouldStartDiscoverUI() {
        authorizationService.request = mockRequest
        val activityIntent = Intent()
        val discoverUIIntent = Intent()
        whenever(mockActivity.intent).thenReturn(activityIntent)
        whenever(mockIntentFactory.createDiscoverUIIntent(mockDiscoverUIUri)).thenReturn(discoverUIIntent)
        doNothing().whenever(mockActivity).startActivity(discoverUIIntent)

        authorizationService.startDiscoverUI(mockActivity, DISCOVER_UI_ENDPOINT)

        verify(mockIntentFactory).createDiscoverUIIntent(mockDiscoverUIUri)
        verify(mockActivity).startActivity(discoverUIIntent)
        verify(mockActivity).intent
        verify(mockActivity).intent = activityIntent
        assertNull(activityIntent.data)
    }

    @Test
    fun shouldStartAuthorize() {
        val activityIntent = Intent()
        val authorizeIntent = Intent()
        whenever(mockActivity.intent).thenReturn(activityIntent)
        whenever(mockIntentFactory.createAuthorizeIntent(eq(mockAuthorizeUri), anyList())).thenReturn(authorizeIntent)
        doNothing().whenever(mockActivity).startActivity(authorizeIntent)
        authorizationService.request = mockRequest

        authorizationService.startAuthorize(mockActivity, mockConfiguration, LOGIN_HINT_TOKEN)

        verify(mockIntentFactory).createAuthorizeIntent(eq(mockAuthorizeUri), anyList())
        verify(mockActivity).startActivity(authorizeIntent)
        verify(mockActivity).intent
        verify(mockActivity).intent = activityIntent
        assertNull(activityIntent.data)
    }

    @Test
    fun shouldDiscoverConfigurationAndStartAuthorizeOnStateNone() {
        val activityIntent = Intent()
        whenever(mockActivity.intent).thenReturn(activityIntent)
        authorizationService.request = mockRequest
        doCallRealMethod().whenever(mockRequest).withLoginHintToken(null)
        whenever(mockRequest.toAuthorizationUri(anyString())).thenReturn(mockAuthorizeUri)
        whenever(mockConfiguration.authorizationEndpoint).thenReturn(AUTHORIZE_ENDPOINT)
        whenever(mockConfiguration.packages).thenReturn(emptyList())

        whenever(mockSimDataProvider.simOperator).thenReturn(MCC_MNC)

        doNothing().whenever(mockDiscoveryService).discoverConfiguration(eq(MCC_MNC), eq(false), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        val intent = Intent()
        whenever(mockIntentFactory.createAuthorizeIntent(eq(mockAuthorizeUri), anyList())).thenReturn(intent)
        doNothing().whenever(mockActivity).startActivity(intent)

        authorizationService.onStateNone(mockActivity)

        verify(mockDiscoveryService).discoverConfiguration(eq(MCC_MNC), eq(false), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        oidcSuccessUnitCaptor.firstValue.invoke(mockConfiguration)
        assertEquals(AuthorizationState.AUTHORIZE, authorizationService.state)
        verify(mockIntentFactory).createAuthorizeIntent(eq(mockAuthorizeUri), anyList())
        verify(mockActivity).startActivity(intent)
        verify(mockActivity).intent
        verify(mockActivity).intent = activityIntent
        assertNull(activityIntent.data)
    }

    @Test
    fun shouldDiscoverConfigurationAndStartDiscoverUIOnStateNone() {
        val activityIntent = Intent()
        whenever(mockActivity.intent).thenReturn(activityIntent)

        authorizationService.request = mockRequest

        val providerNotFoundException = ProviderNotFoundException(DISCOVER_UI_ENDPOINT)
        whenever(mockSimDataProvider.simOperator).thenReturn(MCC_MNC)

        doNothing().whenever(mockDiscoveryService).discoverConfiguration(eq(MCC_MNC), eq(false), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())
        val intent = Intent()
        whenever(mockIntentFactory.createDiscoverUIIntent(mockDiscoverUIUri)).thenReturn(intent)
        doNothing().whenever(mockActivity).startActivity(intent)


        authorizationService.onStateNone(mockActivity)

        verify(mockDiscoveryService).discoverConfiguration(eq(MCC_MNC), eq(false), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        oidcErrorUnitCaptor.firstValue.invoke(providerNotFoundException)
        assertEquals(AuthorizationState.DISCOVER_UI, authorizationService.state)
        verify(mockIntentFactory).createDiscoverUIIntent(mockDiscoverUIUri)
        verify(mockActivity).startActivity(intent)
        verify(mockActivity).intent
        verify(mockActivity).intent = activityIntent
        assertNull(activityIntent.data)
    }

    @Test
    fun shouldDiscoverConfigurationAndSetResultAndFinishOnStateNone() {
        val intent = Intent()
        val throwable = Throwable()
        whenever(mockSimDataProvider.simOperator).thenReturn(MCC_MNC)

        doNothing().whenever(mockDiscoveryService).discoverConfiguration(eq(MCC_MNC), eq(false), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())
        whenever(mockResponse.toIntent()).thenReturn(intent)
        whenever(mockResponseFactory.create(MCC_MNC, REDIRECT_URI, throwable)).thenReturn(mockResponse)
        doNothing().whenever(mockActivity).setResult(Activity.RESULT_OK, intent)
        doNothing().whenever(mockActivity).finish()

        authorizationService.request = mockRequest

        authorizationService.onStateNone(mockActivity)

        verify(mockDiscoveryService).discoverConfiguration(eq(MCC_MNC), eq(false), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        oidcErrorUnitCaptor.firstValue.invoke(throwable)

        verify(mockActivity).setResult(Activity.RESULT_OK, intent)
        verify(mockActivity).finish()
    }

    @Test
    fun shouldDiscoverConfigurationAndStartAuthorizeOnStateDiscoverUI() {
        val activityIntent = Intent()
        authorizationService.request = mockRequest

        val mccMnc = "mcc mnc from discover UI"
        val redirect = Uri.Builder()
                .scheme("https")
                .authority("authority")
                .appendQueryParameter(Json.KEY_MCC_MNC, mccMnc)
                .appendQueryParameter(Json.KEY_LOGIN_HINT_TOKEN, LOGIN_HINT_TOKEN)
                .build()

        whenever(mockActivity.intent).thenReturn(activityIntent)

        doNothing().whenever(mockDiscoveryService).discoverConfiguration(eq(mccMnc), eq(false), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())
        val intent = Intent()
        whenever(mockIntentFactory.createAuthorizeIntent(eq(mockAuthorizeUri), anyList())).thenReturn(intent)
        doNothing().whenever(mockActivity).startActivity(intent)

        authorizationService.onStateDiscoverUI(mockActivity, redirect)

        verify(mockDiscoveryService).discoverConfiguration(eq(mccMnc), eq(false), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        oidcSuccessUnitCaptor.firstValue.invoke(mockConfiguration)
        assertEquals(AuthorizationState.AUTHORIZE, authorizationService.state)
        verify(mockIntentFactory).createAuthorizeIntent(eq(mockAuthorizeUri), anyList())
        verify(mockActivity).startActivity(intent)
        verify(mockActivity).intent
        verify(mockActivity).intent = activityIntent
        assertNull(activityIntent.data)
    }

    @Test
    fun shouldDiscoverConfigurationAndSetResultAndFinishOnStateDiscoverUI() {
        authorizationService.request = mockRequest
        val intent = Intent()
        val throwable = Throwable()
        val mccMnc = "mcc mnc from discover UI"
        val redirect = Uri.Builder()
                .scheme("https")
                .authority("authority")
                .appendQueryParameter(Json.KEY_MCC_MNC, mccMnc)
                .build()


        doNothing().whenever(mockDiscoveryService).discoverConfiguration(eq(mccMnc), eq(false), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())
        whenever(mockResponse.toIntent()).thenReturn(intent)
        whenever(mockResponseFactory.create(mccMnc, REDIRECT_URI, throwable)).thenReturn(mockResponse)
        doNothing().whenever(mockActivity).setResult(Activity.RESULT_OK, intent)
        doNothing().whenever(mockActivity).finish()

        authorizationService.onStateDiscoverUI(mockActivity, redirect)

        verify(mockDiscoveryService).discoverConfiguration(eq(mccMnc), eq(false), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        oidcErrorUnitCaptor.firstValue.invoke(throwable)

        verify(mockResponseFactory).create(mccMnc, REDIRECT_URI, throwable)
        verify(mockActivity).setResult(Activity.RESULT_OK, intent)
        verify(mockActivity).finish()
    }

    @Test
    fun shouldDiscoverConfigurationAndSetResultAndFinishWithDiscoveryStateErrorOnStateDiscoverUI() {
        authorizationService.request = mockRequest
        val intent = Intent()
        val throwable = ProviderNotFoundException(null)
        val mccMnc = "mcc mnc from discover UI"
        val redirect = Uri.Builder()
                .scheme("https")
                .authority("authority")
                .appendQueryParameter(Json.KEY_MCC_MNC, mccMnc)
                .appendQueryParameter(Json.KEY_LOGIN_HINT_TOKEN, LOGIN_HINT_TOKEN)
                .build()


        doNothing().whenever(mockDiscoveryService).discoverConfiguration(eq(mccMnc), eq(false), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())
        whenever(mockResponse.toIntent()).thenReturn(intent)
        whenever(mockResponseFactory.create(mccMnc, REDIRECT_URI, AuthorizationError.DISCOVERY_STATE)).thenReturn(mockResponse)
        doNothing().whenever(mockActivity).setResult(Activity.RESULT_OK, intent)
        doNothing().whenever(mockActivity).finish()

        authorizationService.onStateDiscoverUI(mockActivity, redirect)

        verify(mockDiscoveryService).discoverConfiguration(eq(mccMnc), eq(false), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        oidcErrorUnitCaptor.firstValue.invoke(throwable)

        verify(mockResponseFactory).create(mccMnc, REDIRECT_URI, AuthorizationError.DISCOVERY_STATE)
        verify(mockActivity).setResult(Activity.RESULT_OK, intent)
        verify(mockActivity).finish()
    }

    @Test
    fun shouldSetResultAndFinishIfNotUserNotFoundErrorOnStateAuthorize() {
        authorizationService.request = mockRequest
        authorizationService.mccMnc = MCC_MNC

        val intent = Intent()
        val code = "code"
        val redirect = Uri.Builder()
                .scheme("https")
                .authority("authority")
                .appendQueryParameter(Json.KEY_CODE, code)
                .build()

        whenever(mockResponse.toIntent()).thenReturn(intent)
        whenever(mockResponseFactory.create(MCC_MNC, mockRequest, redirect)).thenReturn(mockResponse)
        doNothing().whenever(mockActivity).setResult(Activity.RESULT_OK, intent)
        doNothing().whenever(mockActivity).finish()

        authorizationService.onStateAuthorize(mockActivity, redirect)

        verify(mockResponseFactory).create(MCC_MNC, mockRequest, redirect)
        verify(mockActivity).setResult(Activity.RESULT_OK, intent)
        verify(mockActivity).finish()
    }

    @Test
    fun shouldSetResultAndFinishIfReceivedOIDCWithPromptTrueOnStateAuthorize() {
        authorizationService.request = mockRequest
        authorizationService.mccMnc = MCC_MNC

        val intent = Intent()
        val redirect = Uri.Builder()
                .scheme("https")
                .authority("authority")
                .appendQueryParameter(Json.KEY_ERROR, "user_not_found")
                .build()

        doNothing().whenever(mockDiscoveryService).discoverConfiguration(eq(MCC_MNC), eq(true), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        whenever(mockResponse.toIntent()).thenReturn(intent)
        whenever(mockResponseFactory.create(MCC_MNC, REDIRECT_URI, AuthorizationError.DISCOVERY_STATE)).thenReturn(mockResponse)
        doNothing().whenever(mockActivity).setResult(Activity.RESULT_OK, intent)
        doNothing().whenever(mockActivity).finish()

        authorizationService.onStateAuthorize(mockActivity, redirect)

        verify(mockDiscoveryService).discoverConfiguration(eq(MCC_MNC), eq(true), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        oidcSuccessUnitCaptor.firstValue.invoke(mockConfiguration)

        verify(mockResponseFactory).create(MCC_MNC, REDIRECT_URI, AuthorizationError.DISCOVERY_STATE)
        verify(mockActivity).setResult(Activity.RESULT_OK, intent)
        verify(mockActivity).finish()
    }

    @Test
    fun shouldDiscoverConfigurationStartDiscoverUIfUserNotFoundErrorOnStateAuthorize() {
        val activityIntent = Intent()
        authorizationService.request = mockRequest
        authorizationService.mccMnc = MCC_MNC

        val throwable = ProviderNotFoundException(DISCOVER_UI_ENDPOINT)
        val discoverUIIntent = Intent()
        val redirect = Uri.Builder()
                .scheme("https")
                .authority("authority")
                .appendQueryParameter(Json.KEY_ERROR, "user_not_found")
                .build()
        whenever(mockActivity.intent).thenReturn(activityIntent)

        doNothing().whenever(mockDiscoveryService).discoverConfiguration(eq(MCC_MNC), eq(true), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        whenever(mockIntentFactory.createDiscoverUIIntent(mockDiscoverUIUri)).thenReturn(discoverUIIntent)
        doNothing().whenever(mockActivity).startActivity(discoverUIIntent)

        authorizationService.onStateAuthorize(mockActivity, redirect)

        verify(mockDiscoveryService).discoverConfiguration(eq(MCC_MNC), eq(true), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        oidcErrorUnitCaptor.firstValue.invoke(throwable)

        assertEquals(AuthorizationState.DISCOVER_USER_NOT_FOUND, authorizationService.state)
        verify(mockIntentFactory).createDiscoverUIIntent(mockDiscoverUIUri)
        verify(mockActivity).startActivity(discoverUIIntent)
        verify(mockActivity).intent
        verify(mockActivity).intent = activityIntent
        assertNull(activityIntent.data)
    }

    @Test
    fun shouldDiscoverConfigurationSetResultAndFinishIfUserNotFoundErrorOnStateAuthorize() {
        authorizationService.request = mockRequest
        authorizationService.mccMnc = MCC_MNC

        val throwable = ProviderNotFoundException(null)
        val intent = Intent()
        val redirect = Uri.Builder()
                .scheme("https")
                .authority("authority")
                .appendQueryParameter(Json.KEY_ERROR, "user_not_found")
                .build()

        doNothing().whenever(mockDiscoveryService).discoverConfiguration(eq(MCC_MNC), eq(true), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        whenever(mockResponse.toIntent()).thenReturn(intent)
        whenever(mockResponseFactory.create(MCC_MNC, REDIRECT_URI, AuthorizationError.DISCOVERY_STATE)).thenReturn(mockResponse)
        doNothing().whenever(mockActivity).setResult(Activity.RESULT_OK, intent)
        doNothing().whenever(mockActivity).finish()

        authorizationService.onStateAuthorize(mockActivity, redirect)

        verify(mockDiscoveryService).discoverConfiguration(eq(MCC_MNC), eq(true), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        oidcErrorUnitCaptor.firstValue.invoke(throwable)

        assertEquals(AuthorizationState.DISCOVER_USER_NOT_FOUND, authorizationService.state)
        verify(mockResponseFactory).create(MCC_MNC, REDIRECT_URI, AuthorizationError.DISCOVERY_STATE)
        verify(mockActivity).setResult(Activity.RESULT_OK, intent)
        verify(mockActivity).finish()
    }

    @Test
    fun shouldDiscoverConfigurationSetResultAndFinishIfUserNotFoundAndProviderNotFoundErrorOnStateAuthorize() {
        authorizationService.request = mockRequest
        authorizationService.mccMnc = MCC_MNC

        val throwable = Throwable()
        val intent = Intent()
        val redirect = Uri.Builder()
                .scheme("https")
                .authority("authority")
                .appendQueryParameter(Json.KEY_ERROR, "user_not_found")
                .build()

        doNothing().whenever(mockDiscoveryService).discoverConfiguration(eq(MCC_MNC), eq(true), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        whenever(mockResponse.toIntent()).thenReturn(intent)
        whenever(mockResponseFactory.create(MCC_MNC, REDIRECT_URI, throwable)).thenReturn(mockResponse)
        doNothing().whenever(mockActivity).setResult(Activity.RESULT_OK, intent)
        doNothing().whenever(mockActivity).finish()

        authorizationService.onStateAuthorize(mockActivity, redirect)

        verify(mockDiscoveryService).discoverConfiguration(eq(MCC_MNC), eq(true), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        oidcErrorUnitCaptor.firstValue.invoke(throwable)

        verify(mockResponseFactory).create(MCC_MNC, REDIRECT_URI, throwable)
        verify(mockActivity).setResult(Activity.RESULT_OK, intent)
        verify(mockActivity).finish()
    }

    @Test
    fun shouldDiscoverConfigurationAndStartAuthorizeOnStateDiscoverUserNotFound() {
        authorizationService.request = mockRequest

        val mccMnc = "mcc mnc from discover UI"
        val redirect = Uri.Builder()
                .scheme("https")
                .authority("authority")
                .appendQueryParameter(Json.KEY_MCC_MNC, mccMnc)
                .appendQueryParameter(Json.KEY_LOGIN_HINT_TOKEN, LOGIN_HINT_TOKEN)
                .build()
        val activityIntent = Intent()
        whenever(mockActivity.intent).thenReturn(activityIntent)


        doNothing().whenever(mockDiscoveryService).discoverConfiguration(eq(mccMnc), eq(false), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())
        val authorizeIntent = Intent()
        whenever(mockIntentFactory.createAuthorizeIntent(eq(mockAuthorizeUri), anyList())).thenReturn(authorizeIntent)
        doNothing().whenever(mockActivity).startActivity(authorizeIntent)

        authorizationService.onStateDiscoverUserNotFound(mockActivity, redirect)

        verify(mockDiscoveryService).discoverConfiguration(eq(mccMnc), eq(false), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        oidcSuccessUnitCaptor.firstValue.invoke(mockConfiguration)
        assertEquals(AuthorizationState.AUTHORIZE_USER_NOT_FOUND, authorizationService.state)
        verify(mockIntentFactory).createAuthorizeIntent(eq(mockAuthorizeUri), anyList())
        verify(mockActivity).startActivity(authorizeIntent)
        verify(mockActivity).intent
        verify(mockActivity).intent = activityIntent
        assertNull(activityIntent.data)
    }

    @Test
    fun shouldDiscoverConfigurationAndSetResultAndFinishWithDiscoveryStateErrorOnDiscoverUserNotFound() {
        authorizationService.request = mockRequest
        val intent = Intent()
        val throwable = Throwable()
        val mccMnc = "mcc mnc from discover UI"
        val redirect = Uri.Builder()
                .scheme("https")
                .authority("authority")
                .appendQueryParameter(Json.KEY_MCC_MNC, mccMnc)
                .build()


        doNothing().whenever(mockDiscoveryService).discoverConfiguration(eq(mccMnc), eq(false), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())
        whenever(mockResponse.toIntent()).thenReturn(intent)
        whenever(mockResponseFactory.create(mccMnc, REDIRECT_URI, throwable)).thenReturn(mockResponse)
        doNothing().whenever(mockActivity).setResult(Activity.RESULT_OK, intent)
        doNothing().whenever(mockActivity).finish()

        authorizationService.onStateDiscoverUserNotFound(mockActivity, redirect)

        verify(mockDiscoveryService).discoverConfiguration(eq(mccMnc), eq(false), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        oidcErrorUnitCaptor.firstValue.invoke(throwable)

        verify(mockResponseFactory).create(mccMnc, REDIRECT_URI, throwable)
        verify(mockActivity).setResult(Activity.RESULT_OK, intent)
        verify(mockActivity).finish()
    }

    @Test
    fun shouldDiscoverConfigurationAndSetResultAndFinishOnDiscoverUserNotFound() {
        authorizationService.request = mockRequest
        val intent = Intent()
        val throwable = ProviderNotFoundException(null)
        val mccMnc = "mcc mnc from discover UI"
        val redirect = Uri.Builder()
                .scheme("https")
                .authority("authority")
                .appendQueryParameter(Json.KEY_MCC_MNC, mccMnc)
                .build()


        doNothing().whenever(mockDiscoveryService).discoverConfiguration(eq(mccMnc), eq(false), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())
        whenever(mockResponse.toIntent()).thenReturn(intent)
        whenever(mockResponseFactory.create(mccMnc, REDIRECT_URI, AuthorizationError.DISCOVERY_STATE)).thenReturn(mockResponse)
        doNothing().whenever(mockActivity).setResult(Activity.RESULT_OK, intent)
        doNothing().whenever(mockActivity).finish()

        authorizationService.onStateDiscoverUserNotFound(mockActivity, redirect)

        verify(mockDiscoveryService).discoverConfiguration(eq(mccMnc), eq(false), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        oidcErrorUnitCaptor.firstValue.invoke(throwable)

        verify(mockResponseFactory).create(mccMnc, REDIRECT_URI, AuthorizationError.DISCOVERY_STATE)
        verify(mockActivity).setResult(Activity.RESULT_OK, intent)
        verify(mockActivity).finish()
    }

    @Test
    fun shouldCheckStateAndStartDiscoveryWithMccMncFromSimDataProvider() {
        authorizationService.state = AuthorizationState.NONE
        authorizationService.request = mockRequest

        doNothing().whenever(mockDiscoveryService).discoverConfiguration(eq(MCC_MNC), eq(false), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        whenever(mockSimDataProvider.simOperator).thenReturn(MCC_MNC)
        authorizationService.checkState(mockActivity, Intent())


        verify(mockDiscoveryService).discoverConfiguration(eq(MCC_MNC), eq(false), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())
        verify(mockSimDataProvider).simOperator
    }

    @Test
    fun shouldCheckStateAndStartDiscoveryWithMccMncFromDiscoverUi() {
        val mccMnc = "mcc mnc from discover UI"
        authorizationService.state = AuthorizationState.DISCOVER_UI
        authorizationService.request = mockRequest
        val redirect = Uri.Builder()
                .scheme("https")
                .authority("authority")
                .appendQueryParameter(Json.KEY_MCC_MNC, mccMnc)
                .build()


        doNothing().whenever(mockDiscoveryService).discoverConfiguration(eq(mccMnc), eq(false), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        authorizationService.checkState(mockActivity, Intent().setData(redirect))

        verify(mockDiscoveryService).discoverConfiguration(eq(mccMnc), eq(false), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())
        verify(mockSimDataProvider, never()).simOperator

        assertEquals(mccMnc, authorizationService.mccMnc)
    }

    @Test
    fun shouldCheckStateAndStartDiscoveryWithPromptIfUserNotFoundFromAuthorize() {
        authorizationService.mccMnc = MCC_MNC
        authorizationService.state = AuthorizationState.AUTHORIZE
        val redirect = Uri.Builder()
                .scheme("https")
                .authority("authority")
                .appendQueryParameter("error", "user_not_found")
                .build()


        doNothing().whenever(mockDiscoveryService).discoverConfiguration(eq(MCC_MNC), eq(true), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        authorizationService.checkState(mockActivity, Intent().setData(redirect))


        verify(mockDiscoveryService).discoverConfiguration(eq(MCC_MNC), eq(true), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

    }

    @Test
    fun shouldCheckStateAndSetResultAndFinishAfterSuccessfulAuthorization() {
        val code = "code"
        authorizationService.mccMnc = MCC_MNC
        authorizationService.state = AuthorizationState.AUTHORIZE
        authorizationService.request = mockRequest
        val redirect = Uri.Builder()
                .scheme("https")
                .authority("authority")
                .appendQueryParameter(Json.KEY_CODE, code)
                .build()

        val intent = Intent()
        doNothing().whenever(mockActivity).finish()
        whenever(mockResponse.toIntent()).thenReturn(intent)
        whenever(mockResponseFactory.create(MCC_MNC, mockRequest, redirect)).thenReturn(mockResponse)
        doNothing().whenever(mockActivity).setResult(Activity.RESULT_OK, intent)

        authorizationService.checkState(mockActivity, Intent().setData(redirect))


        verify(mockDiscoveryService, never()).discoverConfiguration(eq(MCC_MNC), eq(true), oidcSuccessUnitCaptor.capture(), oidcErrorUnitCaptor.capture())

        verify(mockResponseFactory).create(MCC_MNC, mockRequest, redirect)
        verify(mockActivity).setResult(Activity.RESULT_OK, intent)
        verify(mockActivity).finish()
    }

    @Test
    fun shouldCheckStateAndSetCancelledAndFinish() {
        authorizationService.request = mockRequest
        doNothing().whenever(mockActivity).finish()
        doNothing().whenever(mockActivity).setResult(Activity.RESULT_CANCELED)

        authorizationService.updateState(AuthorizationState.AUTHORIZE)

        authorizationService.checkState(mockActivity, Intent())

        verify(mockActivity).setResult(Activity.RESULT_CANCELED)
        verify(mockActivity).finish()
    }

    @Test
    fun shouldSetResultAndFinishOnProviderNotFound() {
        authorizationService.request = mockRequest
        authorizationService.mccMnc = MCC_MNC

        val intent = Intent()
        doNothing().whenever(mockActivity).finish()
        whenever(mockResponse.toIntent()).thenReturn(intent)
        whenever(mockResponseFactory.create(MCC_MNC, REDIRECT_URI, AuthorizationError.DISCOVERY_STATE)).thenReturn(mockResponse)
        doNothing().whenever(mockActivity).setResult(Activity.RESULT_OK, intent)

        authorizationService.onProviderNotFoundError(mockActivity, null)

        verify(mockResponseFactory).create(MCC_MNC, REDIRECT_URI, AuthorizationError.DISCOVERY_STATE)
        verify(mockActivity).setResult(Activity.RESULT_OK, intent)
        verify(mockActivity).finish()
    }

    @Test
    fun shouldStartDiscoverUIOnProviderNotFound() {
        authorizationService.request = mockRequest
        val activityIntent = Intent()
        val discoverUiIntent = Intent()
        whenever(mockActivity.intent).thenReturn(activityIntent)
        whenever(mockIntentFactory.createDiscoverUIIntent(mockDiscoverUIUri)).thenReturn(discoverUiIntent)
        doNothing().whenever(mockActivity).startActivity(discoverUiIntent)

        authorizationService.onProviderNotFoundError(mockActivity, DISCOVER_UI_ENDPOINT)

        verify(mockIntentFactory).createDiscoverUIIntent(mockDiscoverUIUri)
        verify(mockActivity).startActivity(discoverUiIntent)
        verify(mockActivity).intent
        verify(mockActivity).intent = activityIntent
        assertNull(activityIntent.data)
    }

    @Test
    fun shouldAuthorizeAndUpdateMccMnc() {
        authorizationService.mccMnc = MCC_MNC
        authorizationService.request = mockRequest
        val newMccMnc = "mcc_mnc"
        whenever(mockConfiguration.mccMnc).thenReturn(newMccMnc)
        val activityIntent = Intent()
        val authorizeIntent = Intent()
        whenever(mockActivity.intent).thenReturn(activityIntent)
        whenever(mockIntentFactory.createAuthorizeIntent(eq(mockAuthorizeUri), anyList())).thenReturn(authorizeIntent)
        doNothing().whenever(mockActivity).startActivity(authorizeIntent)

        authorizationService.authorize(mockActivity, mockConfiguration, AuthorizationState.AUTHORIZE, LOGIN_HINT_TOKEN)

        verify(mockIntentFactory).createAuthorizeIntent(eq(mockAuthorizeUri), anyList())
        verify(mockActivity).startActivity(authorizeIntent)
        verify(mockActivity).intent
        verify(mockActivity).intent = activityIntent
        verify(mockRequest).withLoginHintToken(LOGIN_HINT_TOKEN)
        verify(mockRequest).toAuthorizationUri(anyString())
        assertNull(activityIntent.data)
        assertEquals(newMccMnc, authorizationService.mccMnc)
        assertEquals(AuthorizationState.AUTHORIZE, authorizationService.state)
    }

    @Test
    fun shouldAuthorizeAndIgnoreMccMnc() {
        authorizationService.mccMnc = MCC_MNC
        authorizationService.request = mockRequest
        whenever(mockConfiguration.mccMnc).thenReturn(null)
        val activityIntent = Intent()
        val authorizeIntent = Intent()
        whenever(mockActivity.intent).thenReturn(activityIntent)
        whenever(mockIntentFactory.createAuthorizeIntent(eq(mockAuthorizeUri), anyList())).thenReturn(authorizeIntent)
        doNothing().whenever(mockActivity).startActivity(authorizeIntent)

        authorizationService.authorize(mockActivity, mockConfiguration, AuthorizationState.AUTHORIZE, LOGIN_HINT_TOKEN)

        verify(mockIntentFactory).createAuthorizeIntent(eq(mockAuthorizeUri), anyList())
        verify(mockActivity).startActivity(authorizeIntent)
        verify(mockActivity).intent
        verify(mockActivity).intent = activityIntent
        verify(mockRequest).withLoginHintToken(LOGIN_HINT_TOKEN)
        verify(mockRequest).toAuthorizationUri(anyString())
        assertNull(activityIntent.data)
        assertEquals(MCC_MNC, authorizationService.mccMnc)
        assertEquals(AuthorizationState.AUTHORIZE, authorizationService.state)
    }

    @Test
    @Throws(PendingIntent.CanceledException::class)
    fun shouldStartSuccessIntent() {
        whenever(mockResponse.toIntent()).thenReturn(mockResponseIntent)
        authorizationService.request = mockRequest
        authorizationService.successIntent = mockSuccessIntent
        whenever(mockResponse.isSuccessful).thenReturn(true)

        doNothing().whenever(mockSuccessIntent).send(mockActivity, Activity.RESULT_OK, mockResponseIntent)

        authorizationService.setResultOKAndFinish(mockActivity, mockResponse)

        verify(mockSuccessIntent).send(mockActivity, Activity.RESULT_OK, mockResponseIntent)
    }

    @Test
    @Throws(PendingIntent.CanceledException::class)
    fun shouldStartFailureIntent() {
        whenever(mockResponse.toIntent()).thenReturn(mockResponseIntent)
        authorizationService.request = mockRequest
        authorizationService.failureIntent = mockFailureIntent
        whenever(mockResponse.isSuccessful).thenReturn(false)

        doNothing().whenever(mockFailureIntent).send(mockActivity, Activity.RESULT_OK, mockResponseIntent)

        authorizationService.setResultOKAndFinish(mockActivity, mockResponse)

        verify(mockFailureIntent).send(mockActivity, Activity.RESULT_OK, mockResponseIntent)
    }

    @Test
    @Throws(PendingIntent.CanceledException::class)
    fun shouldStartCompletionIntent() {
        whenever(mockResponse.toIntent()).thenReturn(mockResponseIntent)
        authorizationService.request = mockRequest
        authorizationService.completionIntent = mockCompletionIntent
        whenever(mockResponse.isSuccessful).thenReturn(false)

        doNothing().whenever(mockCompletionIntent).send(mockActivity, Activity.RESULT_OK, mockResponseIntent)

        authorizationService.setResultOKAndFinish(mockActivity, mockResponse)

        verify(mockCompletionIntent).send(mockActivity, Activity.RESULT_OK, mockResponseIntent)
    }

    @Test
    fun shouldReturnCompletionResult() {
        whenever(mockResponse.toIntent()).thenReturn(mockResponseIntent)
        authorizationService.request = mockRequest
        val intent = Intent()
        doNothing().whenever(mockActivity).finish()
        whenever(mockResponse.toIntent()).thenReturn(intent)
        doNothing().whenever(mockActivity).setResult(Activity.RESULT_OK, intent)

        authorizationService.setResultOKAndFinish(mockActivity, mockResponse)

        verify(mockActivity).setResult(Activity.RESULT_OK, intent)
        verify(mockActivity).finish()
    }

    @Test
    @Throws(PendingIntent.CanceledException::class)
    fun shouldStartCancellationIntent() {
        authorizationService.request = mockRequest
        authorizationService.cancellationIntent = mockCancellationIntent

        doNothing().whenever(mockCancellationIntent).send(mockActivity, Activity.RESULT_CANCELED, null)

        authorizationService.setResultCanceledAndFinish(mockActivity)

        verify(mockCancellationIntent).send(mockActivity, Activity.RESULT_CANCELED, null)
    }

    @Test
    fun shouldReturnCancellationResult() {
        authorizationService.request = mockRequest
        doNothing().whenever(mockActivity).finish()
        doNothing().whenever(mockActivity).setResult(Activity.RESULT_CANCELED)

        authorizationService.setResultCanceledAndFinish(mockActivity)

        verify(mockActivity).setResult(Activity.RESULT_CANCELED)
        verify(mockActivity).finish()
    }

    companion object {
        private const val MCC_MNC = "MCC_MNC"
        private const val DISCOVER_UI_ENDPOINT = "https://example.com/discover_ui"
        private const val AUTHORIZE_ENDPOINT = "https://example.com/authorize"
        private const val LOGIN_HINT_TOKEN = "LOGIN_HINT_TOKEN"
        private val REDIRECT_URI = Uri.EMPTY
    }
}
