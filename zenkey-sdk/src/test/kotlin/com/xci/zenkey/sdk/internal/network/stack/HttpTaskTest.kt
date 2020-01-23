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
package com.xci.zenkey.sdk.internal.network.stack

import com.nhaarman.mockitokotlin2.*
import org.json.JSONException
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

import java.io.IOException
import java.net.URL
import java.util.HashMap
import java.util.concurrent.CancellationException

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class HttpTaskTest {

    private interface SuccessCallback : (HttpResponse<TestBody>) -> Unit
    private interface FailureCallback : (Throwable) -> Unit

    private lateinit var httpTask: HttpTask<TestBody>
    private val mockRequest = mock<HttpRequest>()
    private val mockUrl = mock<URL>()
    private val mockExecutor = mock<HttpCall.Executor<TestBody>>()
    private val mockJsonConverter = mock<JsonConverter<TestBody>>()
    private val mockResponse = mock<HttpResponse<TestBody>>()
    private val mockSuccessUnit = mock<SuccessCallback>()
    private val mockErrorUnit = mock<FailureCallback>()

    @Before
    fun setUp() {
        whenever(mockRequest.method).thenReturn(HttpMethod.GET)
        whenever(mockRequest.baseUrl).thenReturn("BASE_URL")
        whenever(mockRequest.url).thenReturn(mockUrl)
        httpTask = HttpTask(mockRequest, mockJsonConverter, mockExecutor)
    }

    @Test
    @Throws(IOException::class, JSONException::class)
    fun shouldGetSuccessfulResponse() {
        val resultBody = TestBody()
        whenever(mockResponse.body).thenReturn(resultBody)
        whenever(mockResponse.headers).thenReturn(HashMap())
        whenever(mockResponse.code).thenReturn(200)
        whenever(mockResponse.rawBody).thenReturn("")

        whenever(mockExecutor.execute(mockRequest, mockJsonConverter)).thenReturn(mockResponse)
        httpTask.enqueue(mockSuccessUnit, mockErrorUnit)

        Robolectric.flushBackgroundThreadScheduler()

        verify(mockExecutor).execute(mockRequest, mockJsonConverter)
        verify(mockSuccessUnit).invoke(mockResponse)
    }

    @Test
    @Throws(IOException::class, JSONException::class)
    fun shouldGetIOException() {
        val exception = IOException()
        whenever(mockExecutor.execute(mockRequest, mockJsonConverter)).thenThrow(exception)
        httpTask.enqueue(mockSuccessUnit, mockErrorUnit)

        Robolectric.flushBackgroundThreadScheduler()

        verify(mockExecutor).execute(mockRequest, mockJsonConverter)
        verify(mockErrorUnit).invoke(exception)
    }

    @Test
    @Throws(IOException::class, JSONException::class)
    fun shouldGetJSONException() {
        val exception = JSONException("")
        whenever(mockExecutor.execute(mockRequest, mockJsonConverter)).thenThrow(exception)
        httpTask.enqueue(mockSuccessUnit, mockErrorUnit)

        Robolectric.flushBackgroundThreadScheduler()

        verify(mockExecutor).execute(mockRequest, mockJsonConverter)
        verify(mockErrorUnit).invoke(exception)
    }

    @Test
    fun shouldGetCancellationException() {
        httpTask.cancel(true)
        httpTask.enqueue(mockSuccessUnit, mockErrorUnit)

        verifyZeroInteractions(mockExecutor)
        verify(mockErrorUnit).invoke(isA<CancellationException>())
    }
}
