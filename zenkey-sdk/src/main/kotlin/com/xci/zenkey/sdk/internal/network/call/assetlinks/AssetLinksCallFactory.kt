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
import com.xci.zenkey.sdk.internal.network.stack.HttpCallFactory
import com.xci.zenkey.sdk.internal.network.stack.HttpRequest
import java.net.MalformedURLException

/**
 * A [IAssetLinksCallFactory] implementation
 * This class is responsible to get the content of an assetlinks.json file
 * associated with an [IdentityProvider] authorization endpoint.
 */
internal class AssetLinksCallFactory
    : HttpCallFactory(), IAssetLinksCallFactory {

    /**
     * Get the packages associated with an [IdentityProvider]
     *
     * @param authorizationUri the authorization [Uri] of the [IdentityProvider]
     */
    override fun create(authorizationUri: Uri): HttpCall<Map<String, List<String>>> {
        return create(buildPackageRequest(
                authorizationUri.scheme,
                authorizationUri.authority)!!,
                AssetLinksResponseConverter())
    }

    /**
     * Build a package request.
     *
     * @param scheme    the scheme to use.
     * @param authority the authority to use.
     * @return an [HttpRequest]
     */
    internal fun buildPackageRequest(scheme: String?, authority: String?): HttpRequest? {
        try {
            return HttpRequest.get(
                    Uri.Builder()
                            .scheme(scheme)
                            .authority(authority)
                            .appendPath(WELL_KNOW_PATH)
                            .appendPath(ASSET_LINKS_PATH)
                            .build().toString()).build()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }

        return null
    }

    companion object {
        internal const val TARGET_KEY = "target"
        internal const val PACKAGE_NAME_KEY = "package_name"
        internal const val FINGERPRINTS_KEY = "sha256_cert_fingerprints"
        internal const val WELL_KNOW_PATH = ".well-known"
        internal const val ASSET_LINKS_PATH = "assetlinks.json"
    }
}
