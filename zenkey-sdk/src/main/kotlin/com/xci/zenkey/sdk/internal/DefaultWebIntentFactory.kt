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
package com.xci.zenkey.sdk.internal

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import com.xci.zenkey.sdk.internal.browser.BrowserBlacklist
import com.xci.zenkey.sdk.internal.browser.BrowserDescriptor
import com.xci.zenkey.sdk.internal.browser.BrowserSelector
import com.xci.zenkey.sdk.internal.browser.CustomTabManager
import com.xci.zenkey.sdk.internal.browser.NoBrowserException
import com.xci.zenkey.sdk.internal.browser.VersionedBrowserMatcher
import com.xci.zenkey.sdk.internal.contract.Logger
import com.xci.zenkey.sdk.internal.contract.WebIntentFactory


/**
 * Dispatches requests to an OAuth2 authorization service. Note that instances of this class
 * _must be manually disposed_ when no longer required, to avoid leaks
 * (see [.unbind].
 */
internal class DefaultWebIntentFactory internal constructor(
        private val mBrowser: BrowserDescriptor?,
        private val mCustomTabManager: CustomTabManager
) : WebIntentFactory {

    internal constructor(context: Context) : this(BrowserSelector.select(context,
            BrowserBlacklist(VersionedBrowserMatcher.CHROME_CUSTOM_TAB_72)),
            CustomTabManager())

    override fun bind(context: Context) {
        if (mBrowser != null && mBrowser.useCustomTab) {
            mCustomTabManager.bind(context, mBrowser.packageName)
        }
    }

    override fun unbind(context: Context) {
        mCustomTabManager.unbind(context)
    }

    @Throws(NoBrowserException::class)
    override fun create(requestUri: Uri): Intent {
        return prepareAuthorizationRequestIntent(requestUri, createCustomTabsIntentBuilder().build())
    }

    /**
     * Creates a custom tab builder, that will use a tab session from an existing connection to
     * a web browser, if available.
     */
    private fun createCustomTabsIntentBuilder(vararg possibleUris: Uri): CustomTabsIntent.Builder {
        return mCustomTabManager.createTabBuilder(*possibleUris)
    }

    private fun prepareAuthorizationRequestIntent(
            requestUri: Uri,
            customTabsIntent: CustomTabsIntent): Intent {

        if (mBrowser == null) {
            throw NoBrowserException()
        }

        val intent: Intent = if (mBrowser.useCustomTab) {
            customTabsIntent.intent
        } else {
            Intent(Intent.ACTION_VIEW)
        }
        Logger.get().browser(mBrowser, requestUri)
        intent.setPackage(mBrowser.packageName)
        intent.data = requestUri

        return intent
    }
}