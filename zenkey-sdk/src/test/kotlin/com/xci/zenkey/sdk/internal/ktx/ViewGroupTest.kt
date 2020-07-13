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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class ViewGroupTest {

    private val mockContext = mock<Context>()
    private val mockInflater = mock<LayoutInflater>()
    private val mockViewGroup = mock<ViewGroup>()
    private val mockView = mock<View>()

    @Before
    fun setUp() {
        whenever(mockContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(mockInflater)
        whenever(mockViewGroup.context).thenReturn(mockContext)
    }

    @Test
    fun `should inflate with viewGroup as root`() {
        whenever(mockInflater.inflate(FAKE_LAYOUT_ID, mockViewGroup)).thenReturn(mockView)
        Assert.assertEquals(mockView, mockViewGroup.inflate(FAKE_LAYOUT_ID))
        verify(mockInflater).inflate(FAKE_LAYOUT_ID, mockViewGroup)
    }

    @Test
    fun `should inflate without viewGroup as root`() {
        whenever(mockInflater.inflate(eq(FAKE_LAYOUT_ID), isNull())).thenReturn(mockView)
        Assert.assertEquals(mockView, mockViewGroup.inflate(FAKE_LAYOUT_ID, false))
        verify(mockInflater).inflate(eq(FAKE_LAYOUT_ID), isNull())
    }

    companion object {
        private const val FAKE_LAYOUT_ID = 0
    }
}