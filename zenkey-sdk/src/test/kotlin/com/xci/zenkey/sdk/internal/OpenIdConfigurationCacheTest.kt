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

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.xci.zenkey.sdk.internal.model.OpenIdConfiguration

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

import java.util.HashMap

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

@RunWith(RobolectricTestRunner::class)
class OpenIdConfigurationCacheTest {

    private val mockOpenIdConfiguration = mock<OpenIdConfiguration>()
    private val mockMap = mock<HashMap<String, OpenIdConfiguration>>()
    private lateinit var cache: OpenIdConfigurationCache

    @Before
    fun setUp() {
        cache = OpenIdConfigurationCache()
        cache.configurationCache = mockMap
    }

    @Test
    fun shouldHaveConfigurationInCache() {
        whenever(mockMap.containsKey(MCC_MNC)).thenReturn(true)
        assertTrue(cache.contain(MCC_MNC))
    }

    @Test
    fun shouldNotHaveConfigurationInCache() {
        whenever(mockMap.containsKey(MCC_MNC)).thenReturn(false)
        assertFalse(cache.contain(MCC_MNC))
    }

    @Test
    fun shouldGetConfigurationFromCache() {
        whenever(mockMap[MCC_MNC]).thenReturn(mockOpenIdConfiguration)
        val configuration = cache[MCC_MNC]
        assertNotNull(configuration)
        assertEquals(mockOpenIdConfiguration, configuration)
    }

    @Test
    fun shouldNotGetConfigurationFromCache() {
        whenever(mockMap[MCC_MNC]).thenReturn(null)
        val configuration = cache[MCC_MNC]
        assertNull(configuration)
    }

    @Test
    fun shouldRemoveConfigurationFromCache() {
        cache.remove(MCC_MNC)
        verify(mockMap).remove(MCC_MNC)
    }

    @Test
    fun shouldPutConfigurationFromCache() {
        cache.put(MCC_MNC, mockOpenIdConfiguration)
        verify(mockMap)[MCC_MNC] = mockOpenIdConfiguration
    }

    companion object {
        private const val MCC_MNC = "MCCMNC"
    }
}
