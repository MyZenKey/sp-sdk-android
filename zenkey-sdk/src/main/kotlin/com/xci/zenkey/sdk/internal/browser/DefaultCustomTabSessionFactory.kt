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
package com.xci.zenkey.sdk.internal.browser

import android.net.Uri
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsSession

import com.xci.zenkey.sdk.internal.ktx.toCustomTabBundles

internal class DefaultCustomTabSessionFactory
    : CustomTabSessionFactory {

    override fun create(customTabsClient: CustomTabsClient?,
                        callbacks: CustomTabsCallback?,
                        vararg possibleUris: Uri)
            : CustomTabsSession? {
        if (customTabsClient == null) {
            return null
        }

        val session = customTabsClient.newSession(callbacks) ?: return null

        if (possibleUris.isNotEmpty()) {
            session.mayLaunchUrl(possibleUris[0], null, possibleUris.toCustomTabBundles(1))
        }

        return session
    }
}
