/*
 * Copyright 2019 ZenKey, LLC.
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

package com.xci.zenkey.sdk.internal.model

enum class AndroidMessageDigestAlgorithm(
        val value: String
) {
    /**
     * The MD5 message digest algorithm defined in RFC 1321.
     */
    MD5("MD5"),
    /**
     * The SHA-1 hash algorithm defined in the FIPS PUB 180-2.
     */
    SHA_1("SHA-1"),
    /**
     * The SHA-224 hash algorithm defined in the FIPS PUB 180-3.
     *
     *
     * Present in Oracle Java 8.
     *
     *
     * @since 1.11
     */
    SHA_224("SHA-224"),
    /**
     * The SHA-256 hash algorithm defined in the FIPS PUB 180-2.
     */
    SHA_256("SHA-256"),
    /**
     * The SHA-384 hash algorithm defined in the FIPS PUB 180-2.
     */
    SHA_384("SHA-384"),
    /**
     * The SHA-512 hash algorithm defined in the FIPS PUB 180-2.
     */
    SHA_512("SHA-512")
}