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

import android.support.customtabs.CustomTabsClient

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

internal class DefaultCustomTabClientManager
    : CustomTabClientManager {

    private val mClient: AtomicReference<CustomTabsClient> = AtomicReference()

    private val mClientLatch: CountDownLatch = CountDownLatch(1)

    override fun get(): CustomTabsClient? {
        try {
            mClientLatch.await(CLIENT_WAIT_TIME, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            mClientLatch.countDown()
        }

        return mClient.get()
    }

    override fun set(client: CustomTabsClient?) {
        mClient.set(client)
        countDown()
    }

    override fun countDown() {
        mClientLatch.countDown()
    }

    companion object {
        private const val CLIENT_WAIT_TIME = 1L
    }
}
