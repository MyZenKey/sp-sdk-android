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

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import com.xci.zenkey.sdk.AuthorizationResponse
import com.xci.zenkey.sdk.internal.browser.BrowserDescriptor
import com.xci.zenkey.sdk.internal.contract.Logger
import com.xci.zenkey.sdk.internal.model.AuthorizationRequest
import com.xci.zenkey.sdk.internal.model.AuthorizationState
import com.xci.zenkey.sdk.internal.network.stack.HttpException
import com.xci.zenkey.sdk.internal.network.stack.HttpRequest
import com.xci.zenkey.sdk.internal.network.stack.HttpResponse

internal class DefaultLogger
    : Logger {

    @SuppressLint("VisibleForTests")
    override fun request(request: HttpRequest) {
        d("--> " + request.method.name + " " + request.baseUrl)
        for (headerKey in request.headers.keys) {
            d(headerKey + ": " + request.headers[headerKey])
        }
        var length = 0
        val url = request.url
        if (url.query != null) {
            length = url.query.toByteArray().size
            d("{ " + url.query + " }")
        }
        d("--> END " + request.method.name + " (" + length + "-bytes body)")
    }

    override fun response(response: HttpResponse<*>) {
        d("<-- " + response.code + " " + response.url)
        val headers = response.headers
        for (header in headers) {
            d("${header.key}: ${header.value}")
        }
        for (headerKey in headers.keys) {

            for (header in headers[headerKey]!!) {
                d("$headerKey: $header")
            }
        }
        d(response.rawBody)
        d("<-- END HTTP (" + response.rawBody.toByteArray().size + "-bytes body)")
    }

    override fun exception(exception: HttpException) {
        e("<-- HTTP EXCEPTION " + exception.code)
        e(exception.body)
        e("<-- END HTTP EXCEPTION (" + exception.body.toByteArray().size + "-bytes body)")
    }

    override fun throwable(t: Throwable) {
        if (BaseContentProvider.isLogsEnabled) {
            if (t.message != null) {
                e(t.message!!)
            } else if (t.localizedMessage != null) {
                e(t.localizedMessage)
            }
            t.printStackTrace()
        }
    }

    override fun browser(browser: BrowserDescriptor, uri: Uri) {
        d("--> DISPLAY WEB $uri")
        d("Browser: " + browser.packageName + " " + browser.version)
        d("CustomTabs: " + browser.useCustomTab)
        d("<-- END WEB DISPLAY $uri")
    }

    override fun state(state: AuthorizationState) {
        d("Authorization State : $state")
    }

    override fun request(uri: Uri) {
        d("--> REQUEST URI: $uri")
    }

    override fun redirect(uri: Uri) {
        d("--> REDIRECT RECEIVED: $uri")
    }

    override fun v(value: String) {
        if (BaseContentProvider.isLogsEnabled) {
            Log.v(LOGGER_TAG, value)
        }
    }

    override fun d(value: String) {
        if (BaseContentProvider.isLogsEnabled) {
            Log.d(LOGGER_TAG, value)
        }
    }

    override fun i(value: String) {
        if (BaseContentProvider.isLogsEnabled) {
            Log.i(LOGGER_TAG, value)
        }
    }

    override fun w(value: String) {
        if (BaseContentProvider.isLogsEnabled) {
            Log.w(LOGGER_TAG, value)
        }
    }

    override fun e(value: String) {
        if (BaseContentProvider.isLogsEnabled) {
            Log.e(LOGGER_TAG, value)
        }
    }

    override fun begin(request: AuthorizationRequest, mcc_mnc: String?) {
        d("--> START AUTHORIZATION REQUEST")
        d("Request: $request")
        d("MCC_MNC: $mcc_mnc")
        d("<-- END START AUTHORIZATION REQUEST")
    }

    override fun end(request: AuthorizationRequest, response: AuthorizationResponse?) {
        val status = if (response != null) "result available" else "request cancelled"
        val result = response?.toString() ?: "cancelled"
        d("--> AUTHORIZATION REQUEST RESULT: $status")
        d("Request: $request")
        d("Result: $result")
        d("<-- END AUTHORIZATION REQUEST RESULT")
    }

    companion object {

        private const val LOGGER_TAG = "ZenKey SDK"
        private var sInstance: Logger? = null

        fun get(): Logger {
            if (sInstance == null) {
                sInstance = DefaultLogger()
            }
            return sInstance as Logger
        }
    }
}
