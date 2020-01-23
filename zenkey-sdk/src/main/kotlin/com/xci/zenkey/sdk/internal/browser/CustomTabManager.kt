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

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.support.customtabs.CustomTabsClient
import android.support.customtabs.CustomTabsIntent.Builder
import android.support.customtabs.CustomTabsServiceConnection
import com.xci.zenkey.sdk.internal.contract.Logger

/**
 * Hides the details of establishing connections and sessions with custom tabs, to make testing
 * easier.
 */
internal class CustomTabManager {

    private var mConnection: CustomTabsServiceConnection? = null

    private val sessionFactory: CustomTabSessionFactory

    private val clientManager: DefaultCustomTabClientManager

    init {
        sessionFactory = DefaultCustomTabSessionFactory()
        clientManager = DefaultCustomTabClientManager()
    }

    @Synchronized
    fun bind(context: Context,
             browserPackage: String) {
        if (mConnection != null) {
            return
        }

        mConnection = object : CustomTabsServiceConnection() {
            override fun onServiceDisconnected(componentName: ComponentName) {
                Logger.get().d("CustomTabsService disconnected")
                clientManager.set(null)
                clientManager.countDown()
            }

            override fun onCustomTabsServiceConnected(componentName: ComponentName,
                                                      customTabsClient: CustomTabsClient) {
                Logger.get().d("CustomTabsService connected")
                customTabsClient.warmup(0)
                clientManager.set(customTabsClient)
                clientManager.countDown()
            }
        }

        if (!CustomTabsClient.bindCustomTabsService(
                        context,
                        browserPackage,
                        mConnection)) {
            Logger.get().d("No CustomTabs support")
            clientManager.countDown()
        }
    }

    @Synchronized
    fun unbind(context: Context) {

        if (mConnection == null) {
            return
        }

        context.unbindService(mConnection!!)
        mConnection = null
        clientManager.set(null)
    }

    /**
     * Creates a custom tab [Builder],
     * with an optional list of optional URIs that may be requested. The URI list
     * should be ordered such that the most likely URI to be requested is first. If the selected
     * browser does not support custom tabs, then the URI list has no effect.
     */
    fun createTabBuilder(vararg possibleUris: Uri): Builder {
        return Builder(
                sessionFactory.create(clientManager.get(), null, *possibleUris))
    }
}