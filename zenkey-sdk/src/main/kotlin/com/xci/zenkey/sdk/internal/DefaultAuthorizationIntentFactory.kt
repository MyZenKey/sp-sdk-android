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

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import com.xci.zenkey.sdk.internal.browser.NoBrowserException
import com.xci.zenkey.sdk.internal.contract.AuthorizationIntentFactory
import com.xci.zenkey.sdk.internal.contract.PackageManager
import com.xci.zenkey.sdk.internal.contract.WebIntentFactory
import com.xci.zenkey.sdk.internal.ktx.intent
import com.xci.zenkey.sdk.internal.model.Package

internal class DefaultAuthorizationIntentFactory internal constructor(
        private val webIntentFactory: WebIntentFactory,
        private val packageManager: PackageManager
) : AuthorizationIntentFactory {

    @Throws(NoBrowserException::class)
    override fun createAuthorizeIntent(uri: Uri, packages: List<Package>): Intent {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            webIntentFactory.create(uri)
        } else {
            if (anyValidPackageFor(uri, packages)) {
                uri.intent
            } else {
                webIntentFactory.create(uri)
            }
        }
    }

    @Throws(NoBrowserException::class)
    override fun createDiscoverUIIntent(uri: Uri): Intent {
        return webIntentFactory.create(uri)
    }

    override fun bindWebSession(context: Context) {
        webIntentFactory.bind(context)
    }

    override fun unbindWebSession(context: Context) {
        webIntentFactory.unbind(context)
    }

    private fun anyValidPackageFor(uri: Uri, packages: List<Package>): Boolean {
        return packageManager.anyValidPackageFor(uri, packages)
    }
}
