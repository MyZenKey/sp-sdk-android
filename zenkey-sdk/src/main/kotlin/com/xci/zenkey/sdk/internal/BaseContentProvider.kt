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
package com.xci.zenkey.sdk.internal

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.support.annotation.VisibleForTesting
import com.xci.zenkey.sdk.IdentityProvider
import com.xci.zenkey.sdk.R
import com.xci.zenkey.sdk.internal.model.AndroidMessageDigestAlgorithm
import java.security.MessageDigest

internal abstract class BaseContentProvider
    : ContentProvider() {

    private val applicationContext: Context
        get() = getApplicationContext(context!!)

    override fun onCreate(): Boolean {
        val packageName = applicationContext.packageName
        clientId = getMetadata(
                applicationContext.packageManager,
                packageName,
                getClientIdKey(applicationContext))
        firstSimIdentityProvider = DefaultIdentityProvider(packageName,
                clientId,
                Uri.Builder()
                        .scheme(clientId)
                        .authority("com.xci.provider.sdk")
                        .build(),
                MessageDigest.getInstance(AndroidMessageDigestAlgorithm.SHA_256.value))
        create(clientId, applicationContext)
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

    @VisibleForTesting
    internal fun getApplicationContext(context: Context): Context {
        return context as? Application ?: context.applicationContext
    }

    @VisibleForTesting
    internal fun getClientIdKey(context: Context): String {
        return context.getString(R.string.zenkey_client_id)
    }

    fun getMetadata(packageManager: PackageManager, packageName: String, metadataKey: String): String {
        val appInfo: ApplicationInfo?
        try {
            appInfo = packageManager.getApplicationInfo(packageName,
                    PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            return ""
        }

        if (appInfo?.metaData != null) {
            val value = appInfo.metaData.get(metadataKey)
            if (value != null) return value.toString()
        }

        return ""
    }

    protected abstract fun create(clientId: String, context: Context)

    companion object Config {

        @Volatile
        @VisibleForTesting
        internal lateinit var firstSimIdentityProvider: IdentityProvider
        @Volatile
        @VisibleForTesting
        internal lateinit var clientId: String
        @Volatile
        @VisibleForTesting
        internal var isLogsEnabled = false

        fun logs(enable: Boolean) {
            isLogsEnabled = enable
        }

        fun identityProvider(): IdentityProvider = firstSimIdentityProvider
    }
}
