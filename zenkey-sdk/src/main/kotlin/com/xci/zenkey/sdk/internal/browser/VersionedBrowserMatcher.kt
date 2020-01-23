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
package com.xci.zenkey.sdk.internal.browser

/**
 * Matches a browser based on its package name, set of signatures, version and whether it is
 * being used as a custom tab. This can be used as part of a browser whitelist or blacklist.
 */
internal class VersionedBrowserMatcher private constructor(
        private val mPackageName: String,
        private val mSignatureHashes: Set<String>,
        private val mUsingCustomTab: Boolean,
        private val mVersionRange: VersionRange
) : BrowserMatcher {

    override fun matches(descriptor: BrowserDescriptor): Boolean {
        return (mPackageName == descriptor.packageName
                && mUsingCustomTab == descriptor.useCustomTab
                && mVersionRange.matches(descriptor.version)
                && mSignatureHashes == descriptor.signatureHashes)
    }

    companion object {
        /**
         * Matches any version of Chrome for use as a custom tab.
         */
        val CHROME_CUSTOM_TAB_72 = VersionedBrowserMatcher(
                Browsers.Chrome.PACKAGE_NAME,
                Browsers.Chrome.SIGNATURE_SET,
                true,
                VersionRange.atLeast("72.0.3626"))
    }
}
