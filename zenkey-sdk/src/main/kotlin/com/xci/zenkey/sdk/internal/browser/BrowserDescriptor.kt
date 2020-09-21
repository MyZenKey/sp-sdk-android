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
package com.xci.zenkey.sdk.internal.browser

import android.content.pm.PackageInfo
import android.content.pm.Signature
import android.util.Base64
import com.xci.zenkey.sdk.internal.ktx.signaturesCompat

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.HashSet

/**
 * Represents a browser that may be used for an authorization flow.
 */
internal class BrowserDescriptor private constructor(
        val packageName: String,
        val signatureHashes: Set<String>,
        val version: String,
        val useCustomTab: Boolean
) {

    internal constructor(packageInfo: PackageInfo, useCustomTab: Boolean) : this(
            packageInfo.packageName,
            generateSignatureHashes(packageInfo.signaturesCompat),
            packageInfo.versionName,
            useCustomTab)

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is BrowserDescriptor) {
            return false
        }

        return (this.packageName == other.packageName
                && this.version == other.version
                && this.useCustomTab == other.useCustomTab
                && this.signatureHashes == other.signatureHashes)
    }

    override fun hashCode(): Int {
        var hash = packageName.hashCode()

        hash = PRIME_HASH_FACTOR * hash + version.hashCode()
        hash = PRIME_HASH_FACTOR * hash + if (useCustomTab) 1 else 0

        for (signatureHash in signatureHashes) {
            hash = PRIME_HASH_FACTOR * hash + signatureHash.hashCode()
        }

        return hash
    }

    companion object {

        // See: http://stackoverflow.com/a/2816747
        private const val PRIME_HASH_FACTOR = 92821

        private const val DIGEST_SHA_512 = "SHA-512"

        /**
         * Generates a SHA-512 hash, Base64 url-safe encoded, from a [Signature].
         */
        private fun generateSignatureHash(signature: Signature): String {
            try {
                val digest = MessageDigest.getInstance(DIGEST_SHA_512)
                val hashBytes = digest.digest(signature.toByteArray())
                return Base64.encodeToString(hashBytes, Base64.URL_SAFE or Base64.NO_WRAP)
            } catch (e: NoSuchAlgorithmException) {
                throw IllegalStateException(
                        "Platform does not support$DIGEST_SHA_512 hashing")
            }

        }

        /**
         * Generates a set of SHA-512, Base64 url-safe encoded signature hashes from the provided
         * array of signatures.
         */
        private fun generateSignatureHashes(signatures: Array<Signature>): Set<String> {
            val signatureHashes = HashSet<String>()
            for (signature in signatures) {
                signatureHashes.add(generateSignatureHash(signature))
            }

            return signatureHashes
        }
    }
}