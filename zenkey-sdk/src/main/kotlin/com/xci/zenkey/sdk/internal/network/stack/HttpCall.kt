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
package com.xci.zenkey.sdk.internal.network.stack

import org.json.JSONException

import java.io.IOException

/**
 * Contract class for an Http Call.
 */
internal interface HttpCall<T> {

    /**
     * Execute the Http Call.
     *
     * @param onSuccess a Unit to invoke in case of success.
     * @param onError a Unit to invoke in case of error.
     */
    fun enqueue(onSuccess: (HttpResponse<T>) -> Unit, onError: (Exception) -> Unit)

    fun cancel(mayInterruptIfRunning: Boolean): Boolean

    /**
     * A factory contract for [HttpCall]
     */
    interface Factory {

        fun <T> create(request: HttpRequest, converter: JsonConverter<T>): HttpCall<T>

    }

    /**
     * An executor contract for [HttpCall]
     */
    interface Executor<T> {

        @Throws(IOException::class, JSONException::class)
        fun execute(request: HttpRequest, converter: JsonConverter<T>): HttpResponse<T>
    }
}
