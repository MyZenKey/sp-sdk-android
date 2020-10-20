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
package com.xci.zenkey.sdk.internal.network.call.assetlinks

import android.net.Uri
import com.xci.zenkey.sdk.IdentityProvider
import com.xci.zenkey.sdk.internal.network.stack.HttpCall

/**
 * Contract for [IAssetLinksCallFactory]
 */
@Deprecated("Deprecated since Android 30 (R)")
internal interface IAssetLinksCallFactory {

    /**
     * Get the Assets associated with an [IdentityProvider]
     * @param authorizationUri the authorization [Uri] of the [IdentityProvider]
     * @return a [HttpCall] of [Map<String, List<String>>]
     * representing the assets associated with the [IdentityProvider]
     */
    fun create(authorizationUri: Uri): HttpCall<Map<String, List<String>>>

}
