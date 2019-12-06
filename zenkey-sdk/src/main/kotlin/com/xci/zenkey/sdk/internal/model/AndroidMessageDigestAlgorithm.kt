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