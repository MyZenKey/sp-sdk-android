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
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import java.io.BufferedReader
import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

@SmallTest
@RunWith(AndroidJUnit4::class)
class HttpCallExecutorTest {

    private val mockRequest = mock<HttpRequest>()
    private val mockResponse = mock<HttpResponse<TestBody>>()
    private val mockUrlConnection = mock<HttpsURLConnection>()
    private val mockBufferedReader = mock<BufferedReader>()
    private val mockResponseFactory = mock<HttpResponse.Factory<TestBody>>()
    private val mockJsonConverter = mock<JsonConverter<TestBody>>()
    private val mockURL = mock<URL>()

    private lateinit var executor: HttpCallExecutor<TestBody>

    @Before
    fun setUp() {
        executor = HttpCallExecutor()
    }

    @Test
    @Throws(IOException::class)
    fun shouldConnectUrl() {
        whenever(mockRequest.url).thenReturn(mockURL)
        whenever(mockURL.openConnection()).thenReturn(mockUrlConnection)
        whenever(mockRequest.method).thenReturn(HttpMethod.GET)
        whenever(mockRequest.connectTimeout).thenReturn(CONNECT_TIMEOUT)
        whenever(mockRequest.readTimeout).thenReturn(READ_TIMEOUT)

        doCallRealMethod().whenever(mockUrlConnection).requestMethod = anyString()
        doCallRealMethod().whenever(mockUrlConnection).connectTimeout = anyInt()
        doCallRealMethod().whenever(mockUrlConnection).readTimeout = anyInt()
        doCallRealMethod().whenever(mockUrlConnection).readTimeout = anyInt()
        doNothing().whenever(mockUrlConnection).connect()

        val connection = executor.buildConnectionFor(mockRequest)
        assertNotNull(connection)

        verify(mockRequest).method
        verify(mockRequest).connectTimeout
        verify(mockRequest).readTimeout
        verify(mockRequest).url

        verify(mockUrlConnection).requestMethod = HttpMethod.GET.name
        verify(mockUrlConnection).connectTimeout = CONNECT_TIMEOUT
        verify(mockUrlConnection).readTimeout = READ_TIMEOUT
        verify(mockUrlConnection).connect()
    }

    @Test
    @Throws(IOException::class)
    fun shouldReadResponseFromInputStreamReader() {
        val line = "line"
        whenever(mockBufferedReader.readLine()).thenAnswer(object : Answer<String> {
            private var count = 0

            override fun answer(invocation: InvocationOnMock): String? {
                return if (count++ == 1) null else line

            }
        })

        val response = executor.readResponse(mockBufferedReader, StringBuilder())
        assertEquals(line, response)

        verify(mockBufferedReader, times(2)).readLine()
        verify(mockBufferedReader).close()
    }

    @Test
    @Throws(IOException::class, JSONException::class)
    fun shouldReturnSuccessfulResponse() {
        whenever(mockRequest.url).thenReturn(mockURL)
        whenever(mockURL.openConnection()).thenReturn(mockUrlConnection)
        whenever(mockRequest.method).thenReturn(HttpMethod.GET)
        whenever(mockRequest.connectTimeout).thenReturn(CONNECT_TIMEOUT)
        whenever(mockRequest.readTimeout).thenReturn(READ_TIMEOUT)

        doCallRealMethod().whenever(mockUrlConnection).requestMethod = anyString()
        doCallRealMethod().whenever(mockUrlConnection).connectTimeout = anyInt()
        doCallRealMethod().whenever(mockUrlConnection).readTimeout = anyInt()
        doCallRealMethod().whenever(mockUrlConnection).readTimeout = anyInt()
        doNothing().whenever(mockUrlConnection).connect()

        val body = TestBody()
        val line = "line"
        whenever(mockBufferedReader.readLine()).thenAnswer(object : Answer<String> {
            private var count = 0

            override fun answer(invocation: InvocationOnMock): String? {
                return if (count++ == 1) null else line

            }
        })

        whenever(mockResponse.body).thenReturn(body)
        whenever(mockResponse.isSuccessful).thenReturn(true)
        whenever(mockResponseFactory.create(mockUrlConnection, mockJsonConverter)).thenReturn(mockResponse)
        whenever(mockJsonConverter.convert(line)).thenReturn(body)

        executor = HttpCallExecutor(mockResponseFactory)

        val response = executor.execute(mockRequest, mockJsonConverter)

        assertNotNull(response)
        assertTrue(response.isSuccessful)
        assertEquals(body, response.body)

        verify(mockRequest).method
        verify(mockRequest).connectTimeout
        verify(mockRequest).readTimeout
        verify(mockRequest).url

        verify(mockUrlConnection).requestMethod = HttpMethod.GET.name
        verify(mockUrlConnection).connectTimeout = CONNECT_TIMEOUT
        verify(mockUrlConnection).readTimeout = READ_TIMEOUT
        verify(mockUrlConnection).connect()
        //verify(mockBufferedReader, times(2)).readLine();
        //verify(mockBufferedReader).close();
    }

    @Test(expected = IOException::class)
    @Throws(IOException::class, JSONException::class)
    fun shouldReturnFailedResponse() {

        val exception = IOException()
        whenever(mockRequest.url).thenReturn(mockURL)
        whenever(mockURL.openConnection()).thenReturn(mockUrlConnection)
        whenever(mockRequest.method).thenReturn(HttpMethod.GET)
        whenever(mockRequest.connectTimeout).thenReturn(CONNECT_TIMEOUT)
        whenever(mockRequest.readTimeout).thenReturn(READ_TIMEOUT)

        doCallRealMethod().whenever(mockUrlConnection).requestMethod = anyString()
        doCallRealMethod().whenever(mockUrlConnection).connectTimeout = anyInt()
        doCallRealMethod().whenever(mockUrlConnection).readTimeout = anyInt()
        doCallRealMethod().whenever(mockUrlConnection).readTimeout = anyInt()
        doNothing().whenever(mockUrlConnection).connect()

        whenever(mockResponseFactory.create(mockUrlConnection, mockJsonConverter)).thenThrow(exception)

        executor = HttpCallExecutor(mockResponseFactory)

        val response = executor.execute(mockRequest, mockJsonConverter)

        assertNotNull(response)
        assertFalse(response.isSuccessful)

        verify(mockRequest).method
        verify(mockRequest).connectTimeout
        verify(mockRequest).readTimeout
        verify(mockRequest).url

        verify(mockUrlConnection).requestMethod = HttpMethod.GET.name
        verify(mockUrlConnection).connectTimeout = CONNECT_TIMEOUT
        verify(mockUrlConnection).readTimeout = READ_TIMEOUT
        verify(mockUrlConnection).connect()
        verifyZeroInteractions(mockBufferedReader)
    }

    companion object {
        private const val CONNECT_TIMEOUT = 100
        private const val READ_TIMEOUT = 100
    }
}
