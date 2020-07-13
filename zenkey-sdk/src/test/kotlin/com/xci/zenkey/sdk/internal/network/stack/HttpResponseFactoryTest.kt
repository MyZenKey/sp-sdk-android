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

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.nhaarman.mockitokotlin2.*
import org.json.JSONException
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

@SmallTest
@RunWith(AndroidJUnit4::class)
class HttpResponseFactoryTest {

    private val mockInputStream = mock<InputStream>()
    private val mockInputStreamReader = mock<InputStreamReader>()
    private val mockUrlConnection = mock<HttpsURLConnection>()
    private val mockBufferedReader = mock<BufferedReader>()
    private val mockJsonConverter = mock<JsonConverter<TestBody>>()
    private lateinit var factory: HttpResponseFactory<TestBody>

    @Before
    fun setUp() {
        whenever(mockUrlConnection.url).thenReturn(URL("https://test.com"))
        factory = HttpResponseFactory()
    }

    @Test
    @Throws(IOException::class, JSONException::class)
    fun shouldCreateSuccessfulHttpResponseFor200OK() {
        val result = TestBody()
        whenever(mockUrlConnection.responseCode).thenReturn(200)
        whenever(mockUrlConnection.inputStream).thenReturn(mockInputStream)
        whenever(mockInputStream.read(any(ByteArray::class.java), anyInt(), anyInt())).thenReturn(1, -1)
        whenever(mockJsonConverter.convert(anyString())).thenReturn(result)

        val response = factory.create(mockUrlConnection, mockJsonConverter)

        assertEquals(result, response.body)
        assertTrue(response.isSuccessful)
        verify(mockUrlConnection, times(2)).responseCode
        verify(mockUrlConnection).inputStream

        verify(mockInputStream, times(3)).read(any(ByteArray::class.java), anyInt(), anyInt())
        verify(mockJsonConverter).convert(anyString())
    }

    @Test
    @Throws(IOException::class, JSONException::class)
    fun shouldCreateSuccessfulHttpResponseBetween200to300() {
        val result = TestBody()
        whenever(mockUrlConnection.responseCode).thenReturn(201)
        whenever(mockUrlConnection.inputStream).thenReturn(mockInputStream)
        whenever(mockInputStream.read(any(ByteArray::class.java), anyInt(), anyInt())).thenReturn(1, -1)
        whenever(mockJsonConverter.convert(anyString())).thenReturn(result)

        val response = factory.create(mockUrlConnection, mockJsonConverter)

        assertEquals(result, response.body)
        assertTrue(response.isSuccessful)
        verify(mockUrlConnection, times(2)).responseCode
        verify(mockUrlConnection).inputStream

        verify(mockInputStream, times(3)).read(any(ByteArray::class.java), anyInt(), anyInt())
        verify(mockJsonConverter).convert(anyString())
    }

    @Test
    @Throws(IOException::class, JSONException::class)
    fun shouldCreateFailedHttpResponseFor300AndAbove() {

        whenever(mockUrlConnection.responseCode).thenReturn(300)
        whenever(mockUrlConnection.errorStream).thenReturn(mockInputStream)
        whenever(mockInputStream.read(any(ByteArray::class.java), anyInt(), anyInt())).thenReturn(1, -1)

        val response = factory.create(mockUrlConnection, mockJsonConverter)
        assertFalse(response.isSuccessful)

        verify(mockUrlConnection, times(2)).responseCode
        verify(mockUrlConnection).errorStream

        verify(mockInputStream, times(3)).read(any(ByteArray::class.java), anyInt(), anyInt())
    }

    @Test
    @Throws(IOException::class, JSONException::class)
    fun shouldCreateFailedHttpResponseForBelow200() {

        whenever(mockUrlConnection.responseCode).thenReturn(199)
        whenever(mockUrlConnection.errorStream).thenReturn(mockInputStream)
        whenever(mockInputStream.read(any(ByteArray::class.java), anyInt(), anyInt())).thenReturn(1, -1)

        val response = factory.create(mockUrlConnection, mockJsonConverter)
        assertFalse(response.isSuccessful)

        verify(mockUrlConnection, times(2)).responseCode
        verify(mockUrlConnection).errorStream

        verify(mockInputStream, times(3)).read(any(ByteArray::class.java), anyInt(), anyInt())
    }

    @Test
    @Throws(IOException::class)
    fun shouldGetInputStreamReaderFromUrlConnection() {
        whenever(mockUrlConnection.inputStream).thenReturn(mockInputStream)
        assertNotNull(factory.getInputStreamReader(mockUrlConnection.inputStream))
        verify(mockUrlConnection).inputStream
    }

    @Test
    fun shouldGetBufferedReaderFromInputStreamReader() {
        assertNotNull(factory.getBufferedReader(mockInputStreamReader))
    }

    @Test
    @Throws(IOException::class)
    fun shouldReadBody() {
        val body = "body"
        doNothing().`when`(mockBufferedReader).close()
        whenever(mockBufferedReader.readLine()).thenReturn(body, null as String?)
        assertEquals(body, factory.readBody(mockBufferedReader))
    }
}
