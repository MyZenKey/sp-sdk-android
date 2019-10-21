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
package com.xci.zenkey.sdk.internal.network.call.assetlinks

import android.net.Uri

import com.xci.zenkey.sdk.internal.model.Package
import com.xci.zenkey.sdk.internal.network.stack.HttpCall
import com.xci.zenkey.sdk.IdentityProvider

/**
 * Contract for [IAssetLinksCallFactory]
 */
internal interface IAssetLinksCallFactory {

    /**
     * Get the list of [Package] associated with an [IdentityProvider]
     * @param authorizationUri the authorization [Uri] of the [IdentityProvider]
     * @return a [HttpCall] of a [List] of [Package]
     */
    fun create(authorizationUri: Uri): HttpCall<List<Package>>

}
