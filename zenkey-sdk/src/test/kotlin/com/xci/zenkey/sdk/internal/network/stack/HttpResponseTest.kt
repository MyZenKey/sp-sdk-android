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
package com.xci.zenkey.sdk.internal.network.stack

import androidx.test.filters.SmallTest
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever

import org.junit.Before
import org.junit.Test

import java.io.IOException
import java.net.URL
import java.util.HashMap

import javax.net.ssl.HttpsURLConnection

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

@SmallTest
class HttpResponseTest {

    private lateinit var response: HttpResponse<TestBody>
    private val mockConnection = mock<HttpsURLConnection>()
    private val mockURL = mock<URL>()

    @Before
    fun setUp() {
        whenever(mockConnection.url).thenReturn(mockURL)
        whenever(mockConnection.headerFields).thenReturn(HashMap())
    }

    @Test
    @Throws(IOException::class)
    fun shouldBeSuccessful() {
        whenever(mockConnection.responseCode).thenReturn(HttpsURLConnection.HTTP_OK)
        response = HttpResponse.success(mockConnection, RAW_BODY, TestBody())
        assertTrue(response.isSuccessful)
    }

    @Test
    @Throws(IOException::class)
    fun shouldNotBeSuccessfulIfCodeBelow200() {
        whenever(mockConnection.responseCode).thenReturn(100)
        response = HttpResponse.error(mockConnection, ERROR_BODY)
        assertFalse(response.isSuccessful)
    }

    @Test
    @Throws(IOException::class)
    fun shouldNotBeSuccessful() {
        whenever(mockConnection.responseCode).thenReturn(HttpsURLConnection.HTTP_MULT_CHOICE)
        response = HttpResponse.error(mockConnection, ERROR_BODY)
        assertFalse(response.isSuccessful)
    }

    @Test
    @Throws(IOException::class)
    fun shouldGetErrorBody() {
        whenever(mockConnection.responseCode).thenReturn(HttpsURLConnection.HTTP_MULT_CHOICE)

        response = HttpResponse.error(mockConnection, ERROR_BODY)
        assertEquals(ERROR_BODY, response.rawBody)
        assertEquals(HttpsURLConnection.HTTP_MULT_CHOICE.toLong(), response.code.toLong())
        assertNull(response.body)
    }

    @Test
    @Throws(IOException::class)
    fun shouldGetTestBody() {
        val body = TestBody()
        whenever(mockConnection.responseCode).thenReturn(HttpsURLConnection.HTTP_OK)

        response = HttpResponse.success(mockConnection, RAW_BODY, body)
        assertEquals(body, response.body)
        assertEquals(RAW_BODY, response.rawBody)
        assertEquals(HttpsURLConnection.HTTP_OK.toLong(), response.code.toLong())
    }

    companion object {
        private const val RAW_BODY = "RAW_BODY"
        private const val ERROR_BODY = "ERROR_BODY"
    }
}