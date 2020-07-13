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

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.support.customtabs.CustomTabsService
import com.xci.zenkey.sdk.internal.ktx.getPackageInfoCompat
import java.util.*

/**
 * Utility class to obtain the browser package name to be used for
 * [com.xci.zenkey.sdk.internal.contract.WebIntentFactory] calls. It prioritizes browsers which support
 * [custom tabs](https://developer.chrome.com/multidevice/android/customtabs). To mitigate
 * man-in-the-middle attacks by malicious apps pretending to be browsers for the specific URI we
 * query, only those which are registered as a handler for _all_ HTTP and HTTPS URIs will be
 * used.
 */
internal object BrowserSelector {

    private const val SCHEME_HTTP = "http"
    private const val SCHEME_HTTPS = "https"

    /**
     * The service we expect to find on a web browser that indicates it supports custom tabs.
     */
    internal val ACTION_CUSTOM_TABS_CONNECTION = CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION

    /**
     * An arbitrary (but un-registrable, per
     * [IANA rules](https://www.iana.org/domains/reserved)) web intent used to query
     * for installed web browsers on the system.
     */
    internal val BROWSER_INTENT = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("http://www.example.com"))

    /**
     * Retrieves the full list of browsers installed on the device. Two entries will exist
     * for each browser that supports custom tabs, with the [BrowserDescriptor.useCustomTab]
     * flag set to `true` in one and `false` in the other. The list is in the
     * order returned by the package manager, so indirectly reflects the user's preferences
     * (i.e. their default browser, if set, should be the first entry in the list).
     */
    @SuppressLint("PackageManagerGetSignatures")
    private fun getAllBrowsers(context: Context): List<BrowserDescriptor> {
        val pm = context.packageManager
        val browsers = ArrayList<BrowserDescriptor>()
        var defaultBrowserPackage: String? = null

        var queryFlag = PackageManager.GET_RESOLVED_FILTER
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            queryFlag = queryFlag or PackageManager.MATCH_ALL
        }
        // When requesting all matching activities for an intent from the package manager,
        // the user's preferred browser is not guaranteed to be at the head of this list.
        // Therefore, the preferred browser must be separately determined and the resultant
        // list of browsers reordered to restored this desired property.
        val resolvedDefaultActivity = pm.resolveActivity(BROWSER_INTENT, 0)
        if (resolvedDefaultActivity != null) {
            defaultBrowserPackage = resolvedDefaultActivity.activityInfo.packageName
        }
        val resolvedActivityList = pm.queryIntentActivities(BROWSER_INTENT, queryFlag)

        for (info in resolvedActivityList) {
            // ignore handlers which are not browsers
            if (!isFullBrowser(info)) {
                continue
            }

            try {
                var defaultBrowserIndex = 0
                val packageInfo = pm.getPackageInfoCompat(info.activityInfo.packageName)

                if (hasWarmupService(pm, info.activityInfo.packageName)) {
                    val customTabBrowserDescriptor = BrowserDescriptor(packageInfo, true)
                    if (info.activityInfo.packageName == defaultBrowserPackage) {
                        // If the default browser is having a WarmupService,
                        // will it be added to the beginning of the list.
                        browsers.add(defaultBrowserIndex, customTabBrowserDescriptor)
                        defaultBrowserIndex++
                    } else {
                        browsers.add(customTabBrowserDescriptor)
                    }
                }

                val fullBrowserDescriptor = BrowserDescriptor(packageInfo, false)
                if (info.activityInfo.packageName == defaultBrowserPackage) {
                    // The default browser is added to the beginning of the list.
                    // If there is support for Custom Tabs, will the one disabling Custom Tabs
                    // be added as the second entry.
                    browsers.add(defaultBrowserIndex, fullBrowserDescriptor)
                } else {
                    browsers.add(fullBrowserDescriptor)
                }
            } catch (e: NameNotFoundException) {
                // a descriptor cannot be generated without the package info
            }

        }

        return browsers
    }

    /**
     * Searches through all browsers for the best match based on the supplied browser matcher.
     * Custom tab supporting browsers are preferred, if the matcher permits them, and browsers
     * are evaluated in the order returned by the package manager, which should indirectly match
     * the user's preferences.
     *
     * @param context [Context] to use for accessing [PackageManager].
     * @return The package name recommended to use for connecting to custom tabs related components.
     */
    @SuppressLint("PackageManagerGetSignatures")
    fun select(context: Context, browserMatcher: BrowserMatcher): BrowserDescriptor? {
        val allBrowsers = getAllBrowsers(context)
        var bestMatch: BrowserDescriptor? = null
        for (browser in allBrowsers) {
            if (!browserMatcher.matches(browser)) {
                continue
            }

            if (browser.useCustomTab) {
                // directly return the first custom tab supporting browser that is matched
                return browser
            }

            if (bestMatch == null) {
                // store this as the best match for use if we don't find any matching
                // custom tab supporting browsers
                bestMatch = browser
            }
        }

        return bestMatch
    }

    private fun hasWarmupService(pm: PackageManager, packageName: String): Boolean {
        val serviceIntent = Intent()
        serviceIntent.action = ACTION_CUSTOM_TABS_CONNECTION
        serviceIntent.setPackage(packageName)
        return pm.resolveService(serviceIntent, 0) != null
    }

    private fun isFullBrowser(resolveInfo: ResolveInfo): Boolean {
        // The filter must match ACTION_VIEW, CATEGORY_BROWSEABLE, and at least one scheme,
        if (!resolveInfo.filter.hasAction(Intent.ACTION_VIEW)
                || !resolveInfo.filter.hasCategory(Intent.CATEGORY_BROWSABLE)
                || resolveInfo.filter.schemesIterator() == null) {
            return false
        }

        // The filter must not be restricted to any particular set of authorities
        if (resolveInfo.filter.authoritiesIterator() != null) {
            return false
        }

        // The filter must support both HTTP and HTTPS.
        var supportsHttp = false
        var supportsHttps = false
        val schemeIterator = resolveInfo.filter.schemesIterator()
        while (schemeIterator.hasNext()) {
            val scheme = schemeIterator.next()
            supportsHttp = supportsHttp or (SCHEME_HTTP == scheme)
            supportsHttps = supportsHttps or (SCHEME_HTTPS == scheme)

            if (supportsHttp && supportsHttps) {
                return true
            }
        }

        // at least one of HTTP or HTTPS is not supported
        return false
    }
}