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
package com.xci.zenkey.sdk.internal.contract;

import android.net.Uri

/**
 * Contract for platform PackageManager
 */
@Deprecated("Deprecated since Android 30 (R)")
internal interface PackageManager {

    /**
     * Check if any package responding to the URI is matching both name and certificate fingerprints.
     * If
     * @param expected the expected list of packages.
     * @return true if any package handling the Uri is matching both expected name and certificates fingerprints
     */
    fun anyValidPackageFor(authorizationUri: Uri,
                           expected: Map<String, List<String>>): Boolean
}
