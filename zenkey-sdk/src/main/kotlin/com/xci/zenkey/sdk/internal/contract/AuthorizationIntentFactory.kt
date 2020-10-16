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
package com.xci.zenkey.sdk.internal.contract

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.xci.zenkey.sdk.internal.browser.NoBrowserException

internal interface AuthorizationIntentFactory {

    @Throws(NoBrowserException::class)
    @Deprecated("This method is useless since Android R, " +
        "we can no longer validate a package signature(s)")
    fun createAuthorizeIntent(authorizationUri: Uri, packages: Map<String, List<String>>?): Intent

    @Throws(NoBrowserException::class)
    fun createBrowserIntent(uri: Uri): Intent

    fun bindWebSession(context: Context)

    fun unbindWebSession(context: Context)
}
