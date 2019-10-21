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
package com.xci.zenkey.sdk.internal.browser

import java.util.Arrays

/**
 * A blacklist of browsers. This will reject a match for any browser on the list, and permit
 * all others. Examples:
 *
 * ```java
 * // blacklist Chrome, whether using a custom tab or not
 * new BrowserBlacklist(
 * VersionedBrowserMatcher.CHROME_BROWSER,
 * VersionedBrowserMatcher.CHROME_CUSTOM_TAB);
 *
 * // blacklist Firefox
 * new BrowserBlacklist(
 * VersionedBrowserMatcher.FIREFOX_BROWSER,
 * VersionedBrowserMatcher.FIREFOX_CUSTOM_TAB);
 *
 * // blacklist Dolphin Browser
 * new BrowserBlacklist(
 * new VersionedBrowserMatcher(
 * "mobi.mgeek.TunnyBrowser",
 * "DOLPHIN_SIGNATURE",
 * false,
 * VersionRange.ANY_VERSION));
 * }
 * ```
 */
internal class BrowserBlacklist internal constructor(
        vararg matchers: BrowserMatcher
) : BrowserMatcher {

    private val mBrowserMatchers: List<BrowserMatcher> = listOf(*matchers)

    override fun matches(descriptor: BrowserDescriptor): Boolean {
        for (matcher in mBrowserMatchers) {
            if (matcher.matches(descriptor)) {
                return false
            }
        }
        return true
    }
}
