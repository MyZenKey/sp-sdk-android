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

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.*
import com.xci.zenkey.sdk.internal.contract.AuthorizationService
import com.xci.zenkey.sdk.internal.model.AuthorizationRequest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController

@RunWith(RobolectricTestRunner::class)
class AuthorizationRequestActivityTest {
    private val redirectUri = Uri.Builder()
            .scheme(CLIENT_ID)
            .authority(AUTHORITY)
            .build()
    private var intent: Intent? = null

    private lateinit var activity: AuthorizationRequestActivity
    private lateinit var controller: ActivityController<AuthorizationRequestActivity>

    private val mockAuthorizationService = mock<AuthorizationService>()
    private val mockRequest = mock<AuthorizationRequest>()

    @Before
    fun setUp() {
        val packageName = ApplicationProvider.getApplicationContext<Context>().packageName
        whenever(mockRequest.redirectUri).thenReturn(redirectUri)
        whenever(mockRequest.clientId).thenReturn(CLIENT_ID)

        DefaultContentProvider.authorizationService = mockAuthorizationService
        doNothing().whenever(mockAuthorizationService).onCreate(any(), any(), any())
        doNothing().whenever(mockAuthorizationService).onResume(any(), any())
        doNothing().whenever(mockAuthorizationService).onSaveInstanceState(any())

        intent = AuthorizationRequestActivity.createStartForResultIntent(packageName, mockRequest, null, null, null, null)
        controller = Robolectric.buildActivity(AuthorizationRequestActivity::class.java, intent).start()
        activity = controller.get() as AuthorizationRequestActivity
    }

    @Test
    fun shouldCallAuthorizationServiceOnCreate() {
        controller.create()

        verify(mockAuthorizationService).onCreate(activity, intent!!, null)
    }

    @Test
    fun shouldCallAuthorizationServiceOnResume() {
        controller.create()

        verify(mockAuthorizationService).onCreate(activity, intent!!, null)

        controller.resume()

        verify(mockAuthorizationService).onResume(activity, intent!!)
    }

    @Test
    fun shouldCallAuthorizationServiceOnSaveInstanceState() {
        controller.create()

        verify(mockAuthorizationService).onCreate(activity, intent!!, null)

        controller.resume()

        verify(mockAuthorizationService).onResume(activity, intent!!)

        val bundle = Bundle()

        controller.saveInstanceState(bundle)

        verify(mockAuthorizationService).onSaveInstanceState(bundle)
    }

    @Test
    fun shouldCallAuthorizationServiceOnNewIntent() {
        controller.create()

        verify(mockAuthorizationService).onCreate(activity, intent!!, null)

        controller.resume()

        verify(mockAuthorizationService).onResume(activity, intent!!)

        val intent = Intent()

        controller.newIntent(intent)

        verify(mockAuthorizationService).onNewIntent(activity, intent)
    }

    companion object {
        private const val CLIENT_ID = "CLIENT_ID"
        private const val AUTHORITY = "AUTHORITY"
    }
}
