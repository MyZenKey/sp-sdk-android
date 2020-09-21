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

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import com.xci.zenkey.sdk.IdentityProvider
import com.xci.zenkey.sdk.internal.ktx.SHA256MessageDigest
import com.xci.zenkey.sdk.internal.ktx.clientId

internal abstract class BaseContentProvider
    : ContentProvider() {

    override fun onCreate(): Boolean {
        val appContext = context!!.applicationContext
        clientId = appContext.clientId
        firstSimIdentityProvider = DefaultIdentityProvider(appContext.packageName,
                clientId,
                Uri.Builder()
                        .scheme(clientId)
                        .authority("com.xci.provider.sdk")
                        .build(),
                SHA256MessageDigest)
        create(clientId, appContext)
        return false
    }

    override fun query(uri: Uri,
                       projection: Array<String>?,
                       selection: String?,
                       selectionArgs: Array<String>?,
                       sortOrder: String?): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri,
                        values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri,
                        selection: String?,
                        selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun update(uri: Uri,
                        values: ContentValues?,
                        selection: String?,
                        selectionArgs: Array<String>?): Int {
        return 0
    }

    protected abstract fun create(clientId: String, context: Context)

    companion object Config {

        @Volatile
        internal lateinit var firstSimIdentityProvider: IdentityProvider
        @Volatile
        internal lateinit var clientId: String
        @Volatile
        internal var isLogsEnabled = false

        fun logs(enable: Boolean) {
            isLogsEnabled = enable
        }

        fun identityProvider(): IdentityProvider = firstSimIdentityProvider
    }
}
