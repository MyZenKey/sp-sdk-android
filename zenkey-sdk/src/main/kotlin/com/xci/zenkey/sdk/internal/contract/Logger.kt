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
package com.xci.zenkey.sdk.internal.contract

import android.net.Uri

import com.xci.zenkey.sdk.AuthorizationResponse
import com.xci.zenkey.sdk.internal.DefaultLogger
import com.xci.zenkey.sdk.internal.browser.BrowserDescriptor
import com.xci.zenkey.sdk.internal.model.AuthorizationRequest
import com.xci.zenkey.sdk.internal.model.AuthorizationState
import com.xci.zenkey.sdk.internal.network.stack.HttpException
import com.xci.zenkey.sdk.internal.network.stack.HttpRequest
import com.xci.zenkey.sdk.internal.network.stack.HttpResponse

internal interface Logger {

    fun v(value: String)

    fun d(value: String)

    fun i(value: String)

    fun w(value: String)

    fun e(value: String)

    fun begin(request: AuthorizationRequest, mcc_mnc: String?)

    fun end(request: AuthorizationRequest, response: AuthorizationResponse?)

    fun request(request: HttpRequest)

    fun response(response: HttpResponse<*>)

    fun exception(exception: HttpException)

    fun browser(browser: BrowserDescriptor, uri: Uri)

    fun state(state: AuthorizationState)

    fun request(uri: Uri)

    fun redirect(uri: Uri)

    fun throwable(t: Throwable)

    companion object Instance {
        fun get() = DefaultLogger.get()
    }
}