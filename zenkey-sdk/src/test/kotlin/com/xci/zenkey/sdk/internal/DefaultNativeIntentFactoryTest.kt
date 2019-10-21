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
package com.xci.zenkey.sdk.internal

import android.content.Intent
import android.net.Uri

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue

@RunWith(AndroidJUnit4::class)
@SmallTest
class DefaultNativeIntentFactoryTest {

    private lateinit var intentFactory: DefaultNativeIntentFactory

    @Before
    fun setUp() {
        intentFactory = DefaultNativeIntentFactory()
    }

    @Test
    fun shouldCreateNativeIntentWithUri() {
        val intent = intentFactory.create(AUTHORIZE_URI)
        assertNotNull(intent)
        assertTrue(intent.categories.contains(Intent.CATEGORY_DEFAULT))
        assertEquals(1, intent.categories.size.toLong())
        assertEquals(Intent.ACTION_VIEW, intent.action)

        val dataUri = intent.data
        assertEquals(AUTHORIZE_URI, dataUri)
    }

    companion object {
        private val AUTHORIZE_URI = Uri.parse("https://test.xci.com/authorize")
    }
}
