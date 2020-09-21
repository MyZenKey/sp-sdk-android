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

import android.net.Uri
import androidx.test.filters.SmallTest
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString

@SmallTest
class UriBuilderTest {

    private val mockUriBuilder = mock<Uri.Builder>()

    @Before
    fun setUp() {
        whenever(mockUriBuilder.appendQueryParameter(anyString(), anyString())).thenReturn(mockUriBuilder)
    }

    @Test
    fun shouldAppendQueryParam() {
        val value = "value"
        mockUriBuilder.appendQueryParameterIfNotNull(KEY, value)
        verify(mockUriBuilder).appendQueryParameter(KEY, value)
    }

    @Test
    fun shouldNotAppendQueryParam() {
        mockUriBuilder.appendQueryParameterIfNotNull(KEY, null)
        verify(mockUriBuilder, never()).appendQueryParameter(KEY, null)
    }

    companion object {
        private const val KEY = "key"
    }
}

