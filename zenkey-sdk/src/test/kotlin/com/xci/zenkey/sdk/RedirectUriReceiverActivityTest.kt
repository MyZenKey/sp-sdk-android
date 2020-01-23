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
package com.xci.zenkey.sdk

import android.content.Intent
import android.net.Uri
import com.xci.zenkey.sdk.internal.AuthorizationRequestActivity
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class RedirectUriReceiverActivityTest {

    @Test
    fun shouldForwardsRedirectToAuthorizationRequestActivity() {
        val redirectIntent = Intent()
        redirectIntent.data = REDIRECT_URI

        val redirectController = Robolectric.buildActivity(RedirectUriReceiverActivity::class.java, redirectIntent)
                .create()

        val redirectActivity = redirectController.get() as RedirectUriReceiverActivity

        val nextIntent = shadowOf(redirectActivity).nextStartedActivity
        assertEquals(AuthorizationRequestActivity::class.java.name, nextIntent.component!!.className)
        assertEquals(REDIRECT_URI, nextIntent.data)
        assertTrue(redirectActivity.isFinishing)
    }

    @Test
    fun shouldIgnoreIntentWithoutData() {
        val redirectIntent = Intent()

        val redirectController = Robolectric.buildActivity(RedirectUriReceiverActivity::class.java, redirectIntent)
                .create()

        val redirectActivity = redirectController.get() as RedirectUriReceiverActivity

        assertNull(shadowOf(redirectActivity).nextStartedActivity)
        assertTrue(redirectActivity.isFinishing)
    }

    companion object {
        private val REDIRECT_URI = Uri.Builder().scheme("scheme").authority("authority").build()
    }
}