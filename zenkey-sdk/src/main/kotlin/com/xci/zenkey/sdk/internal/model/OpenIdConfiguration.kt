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
package com.xci.zenkey.sdk.internal.model

import java.util.*

/**
 * Model for OpenId configuration.
 * This class represent an OpenId configuration.
 */
internal class OpenIdConfiguration {

    /**
     * Get the MCC/MNC associated with the configuration.
     *
     * @return the MCC/MNC associated with the configuration
     */
    val mccMnc: String?
    /**
     * Get the OpenId Authorization Endpoint
     *
     * @return the OpenId Authorization Endpoint
     */
    val authorizationEndpoint: String
    private val expiredAt: Date
    lateinit var packages: Map<String, List<String>>

    /**
     * Get the configuration Issuer.
     *
     * @return the issuer url of the configuration
     */
    val issuer: String


    val branding: Branding

    /**
     * Check if the OpenId configuration is considered expired.
     * The OpenId configuration validity is 15 minutes.
     *
     * @return true if the configuration is expired, false else.
     */
    val isExpired: Boolean
        get() = Calendar.getInstance().after(expiredAt)

    /**
     * Constructor for [OpenIdConfiguration].
     *
     * @param issuer                the Issuer endpoint.
     * @param authorizationEndpoint the authorization endpoint.
     * @param mcc_mnc               the MCC/MNC to use with this configuration
     */
    constructor(issuer: String,
                authorizationEndpoint: String,
                mcc_mnc: String?,
                branding: Branding) : this(issuer, authorizationEndpoint, mcc_mnc, branding, Calendar.getInstance())

    /**
     * Constructor for [OpenIdConfiguration].
     * Only used for testing purpose.
     *
     * @param issuer                the Issuer endpoint.
     * @param authorizationEndpoint the authorization endpoint.
     * @param mcc_mnc               the MCC/MNC to use with this configuration
     * @param receiveAt             a [Calendar] object reflecting the time
     * this [OpenIdConfiguration] has be received.
     */
    internal constructor(issuer: String,
                         authorizationEndpoint: String,
                         mcc_mnc: String?,
                         branding: Branding,
                         receiveAt: Calendar) {
        this.issuer = issuer
        this.authorizationEndpoint = authorizationEndpoint
        this.mccMnc = mcc_mnc
        this.branding = branding
        receiveAt.add(EXPIRATION_UNIT, EXPIRATION_AMOUNT)
        this.expiredAt = receiveAt.time
    }

    /**
     * Constructor to clone an [OpenIdConfiguration]
     *
     * @param configuration the configuration to clone.
     */
    internal constructor(configuration: OpenIdConfiguration) {
        this.issuer = configuration.issuer
        this.authorizationEndpoint = configuration.authorizationEndpoint
        this.expiredAt = configuration.expiredAt
        this.mccMnc = configuration.mccMnc
        this.packages = configuration.packages
        this.branding = configuration.branding
    }

    companion object {
        private const val EXPIRATION_UNIT = Calendar.MINUTE
        private const val EXPIRATION_AMOUNT = 15
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OpenIdConfiguration

        if (mccMnc != other.mccMnc) return false
        if (authorizationEndpoint != other.authorizationEndpoint) return false
        if (expiredAt != other.expiredAt) return false
        if (packages != other.packages) return false
        if (issuer != other.issuer) return false
        if (branding != other.branding) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mccMnc?.hashCode() ?: 0
        result = 31 * result + authorizationEndpoint.hashCode()
        result = 31 * result + expiredAt.hashCode()
        result = 31 * result + packages.hashCode()
        result = 31 * result + issuer.hashCode()
        result = 31 * result + branding.hashCode()
        return result
    }
}
