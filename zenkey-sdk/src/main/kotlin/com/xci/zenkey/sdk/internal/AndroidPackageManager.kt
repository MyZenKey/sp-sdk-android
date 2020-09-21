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

import com.xci.zenkey.sdk.internal.contract.PackageManager
import com.xci.zenkey.sdk.internal.ktx.getPackageInfoCompat
import com.xci.zenkey.sdk.internal.ktx.signaturesCompat
import com.xci.zenkey.sdk.internal.security.FingerprintFactory

import java.security.cert.CertificateException
import java.util.ArrayList

internal class AndroidPackageManager internal constructor(
        private val androidPackageManager: android.content.pm.PackageManager,
        private val fingerprintFactory: FingerprintFactory?
) : PackageManager {

    /**
     * Check if any package responding to the URI is matching both name and certificate fingerprints.
     * If
     * @param expected the expected packages.
     * @return true if any package handling the Uri is matching both expected name and certificates fingerprints
     */
    override fun anyValidPackageFor(authorizationUri: Uri, expected: Map<String, List<String>>): Boolean {
        val availablePackages = getAvailablePackages(authorizationUri)
        for (availablePackageName in availablePackages) {
            for (expectedPackage in expected) {
                if (availablePackageName == expectedPackage.key) {
                    if (allFingerprintsAreValid(expectedPackage.value, getCertificateFingerprints(availablePackageName)))
                        return true
                }
            }
        }
        return false
    }

    /**
     * Get all the [List] of package name responding to the Uri.
     *
     * @param uri the Uri to use.
     * @return the [List] of package name responding to the Uri
     */
    internal fun getAvailablePackages(uri: Uri): List<String> {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.data = uri
        val resolvedActivities = androidPackageManager.queryIntentActivities(intent,
                android.content.pm.PackageManager.MATCH_DEFAULT_ONLY)
        val packages = ArrayList<String>()
        for (resolveInfo in resolvedActivities) {
            packages.add(resolveInfo.activityInfo.packageName)
        }
        return packages
    }

    /**
     * Get the [List] of certificates fingerprints for a package name.
     *
     * @param packageName the package name to use.
     * @return the [List] of certificates fingerprints for a package name.
     */
    internal fun getCertificateFingerprints(packageName: String): List<String> {
        val signatures = ArrayList<String>()
        if (fingerprintFactory != null) {
            try {
                val packageInfo = androidPackageManager.getPackageInfoCompat(packageName)
                for (signature in packageInfo.signaturesCompat) {
                    try {
                        signatures.add(fingerprintFactory.create(signature.toByteArray()))
                    } catch (e: CertificateException) {
                        e.printStackTrace()
                    }
                }
            } catch (e: android.content.pm.PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
        return signatures
    }

    /**
     * Check if all actual fingerprints are matching an expected values.
     *
     * @param expectedFingerprints the expected fingerprints.
     * @param actualFingerprints   the actual fingerprints.
     * @return true if all the actual fingerprints are contained in the expected fingerprints,
     * false else.
     */
    internal fun allFingerprintsAreValid(expectedFingerprints: List<String>, actualFingerprints: List<String>): Boolean {

        if (actualFingerprints.isEmpty())
            return false

        for (fingerprint in actualFingerprints) {
            if (!expectedFingerprints.contains(fingerprint))
                return false
        }

        return true
    }
}
