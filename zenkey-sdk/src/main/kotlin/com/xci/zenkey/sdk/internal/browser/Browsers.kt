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
 * Defines the attributes of some commonly-used browsers on Android, for use in browser matchers.
 */
internal object Browsers {

    /**
     * Constants related to Google Chrome.
     */
    internal object Chrome {

        /**
         * The package name for Chrome.
         */
        const val PACKAGE_NAME = "com.android.chrome"

        /**
         * The SHA-512 hash (Base64 url-safe encoded) of the public key for Chrome.
         */
        private const val SIGNATURE = "7fmduHKTdHHrlMvldlEqAIlSfii1tl35bxj1OXN5Ve8c4lU6URVu4xtSHc3BVZxS" + "6WWJnxMDhIfQN0N0K2NDJg=="

        /**
         * The set of signature hashes for Chrome.
         */
        val SIGNATURE_SET = setOf(SIGNATURE)
    }
}
