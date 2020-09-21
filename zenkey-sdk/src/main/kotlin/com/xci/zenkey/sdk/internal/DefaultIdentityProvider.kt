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

import android.content.Intent
import android.net.Uri
import com.xci.zenkey.sdk.AuthorizeIntentBuilder

import com.xci.zenkey.sdk.IdentityProvider
import com.xci.zenkey.sdk.ZenKey
import java.security.MessageDigest

/**
 * A [IdentityProvider] implementation.
 * This class is responsible to provide the [IdentityProvider] name's
 * and the [Intent] used to start the authorization code flow.
 *
 * This class isn't intended to be manually instantiated.
 * You can obtain an Instance of [IdentityProvider] using [ZenKey.identityProvider]
 */
internal class DefaultIdentityProvider internal constructor(
        private val packageName: String,
        private val clientId: String,
        private val defaultRedirectUri: Uri,
        private val messageDigest: MessageDigest?
) : IdentityProvider {

    override fun authorizeIntent(): AuthorizeIntentBuilder {
        return DefaultAuthorizeIntentBuilder(packageName, clientId, messageDigest, defaultRedirectUri)
    }
}
