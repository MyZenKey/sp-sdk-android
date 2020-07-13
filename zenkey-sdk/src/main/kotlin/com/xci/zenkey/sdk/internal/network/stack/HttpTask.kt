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

import android.os.AsyncTask
import com.xci.zenkey.sdk.internal.contract.Logger
import java.util.concurrent.CancellationException

/**
 * An [AsyncTask] implementing [HttpCall].
 * This class is responsible to perform [HttpRequest]
 * in a background Thread using a [HttpCall.Executor] implementation.
 */
internal class HttpTask<T> internal constructor(
        private val request: HttpRequest,
        private val converter: JsonConverter<T>,
        private val executor: HttpCall.Executor<T>
) : AsyncTask<HttpRequest, Int, HttpResponse<T>>(), HttpCall<T> {

    private lateinit var onSuccess: (HttpResponse<T>) -> Unit
    private lateinit var onError: (Exception) -> Unit
    private var exception: Exception? = null

    internal constructor(request: HttpRequest, converter: JsonConverter<T>)
            : this(request, converter, HttpCallExecutor<T>())

    /**
     * Perform the [HttpCall]
     *
     * @param params the params containing the [HttpRequest]
     * @return an [HttpResponse] if the network call was performed successfully,
     * null if the Task has been canceled.
     */
    override fun doInBackground(vararg params: HttpRequest): HttpResponse<T>? {
        val request = params[0]
        Logger.get().request(request)
        return try {
            val response = executor.execute(request, converter)
            Logger.get().response(response)
            response
        } catch (e: Exception) {
            this.exception = e
            null
        }
    }

    /**
     * Invoke onError or onSuccess Units depending on the result of the [HttpTask].
     *
     * @param response the [HttpResponse] result of the [HttpTask]
     * onSuccess will be invoked if the [HttpTask] succeed.
     * onError will be invoked if the [HttpTask] fail with any exception present in the [HttpResponse]
     * or if the [HttpTask] has been cancelled with [CancellationException]
     */
    override fun onPostExecute(response: HttpResponse<T>?) {
        super.onPostExecute(response)
        if ((response == null) and (exception != null)) {
            onError.invoke(exception!!)
        } else {
            onSuccess.invoke(response!!)
        }
    }

    /**
     * Execute the [HttpCall]
     *
     * @param onSuccess a Unit to invoke in case of success.
     * @param onError a Unit to invoke in case of error.
     */
    override fun enqueue(onSuccess: (HttpResponse<T>) -> Unit, onError: (Exception) -> Unit) {
        this.onSuccess = onSuccess
        this.onError = onError
        if (isCancelled) {
            this.onError.invoke(CancellationException())
        } else {
            this.execute(request)
        }
    }
}
