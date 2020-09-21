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

/**
 * A browser filter which matches when a browser falls into a version range. Versions are
 * expected to match the semantics of [DelimitedVersion].
 */
internal class VersionRange private constructor(
        private val mLowerBound: DelimitedVersion?,
        private val mUpperBound: DelimitedVersion?
) {

    /**
     * Determines whether the specified version (parsed as an [DelimitedVersion] falls within
     * the version range.
     */
    internal fun matches(version: String): Boolean {
        return matches(DelimitedVersion.parse(version))
    }

    /**
     * Determines whether the specified version falls within the version range.
     */
    private fun matches(version: DelimitedVersion): Boolean {
        return if (mLowerBound != null && mLowerBound > version) {
            false
        } else mUpperBound == null || mUpperBound >= version

    }

    override fun toString(): String {
        if (mLowerBound == null) {
            return if (mUpperBound == null) {
                "any version"
            } else "$mUpperBound or lower"

        }

        return if (mUpperBound != null) {
            "between $mLowerBound and $mUpperBound"
        } else "$mLowerBound or higher"

    }

    companion object {

        /**
         * Creates a version range that will match any version at or above the specified version,
         * which will be parsed as a [DelimitedVersion].
         */
        internal fun atLeast(version: String): VersionRange {
            return atLeast(DelimitedVersion.parse(version))
        }

        /**
         * Creates a version range that will match any version at or above the specified version.
         */
        internal fun atLeast(version: DelimitedVersion): VersionRange {
            return VersionRange(version, null)
        }

        /**
         * Creates a version range that will match any version at or below the specified version,
         * which will be parsed as a [DelimitedVersion].
         */
        fun atMost(version: String): VersionRange {
            return atMost(DelimitedVersion.parse(version))
        }

        /**
         * Creates a version range that will match any version at or below the specified version.
         */
        fun atMost(version: DelimitedVersion): VersionRange {
            return VersionRange(null, version)
        }

        /**
         * Creates a version range that will match any version equal to or between the specified
         * versions, which will be parsed as [DelimitedVersion] instances.
         */
        fun between(lowerBound: String, upperBound: String): VersionRange {
            return VersionRange(
                    DelimitedVersion.parse(lowerBound),
                    DelimitedVersion.parse(upperBound))
        }
    }
}
