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

import org.junit.Before
import org.junit.Test

import java.util.ArrayList
import java.util.Calendar

import androidx.test.filters.SmallTest

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

@SmallTest
class OpenIdConfigurationTest {
    private var configuration: OpenIdConfiguration? = null

    @Before
    fun setUp() {
        configuration = OpenIdConfiguration(ISSUER, AUTHORIZATION_ENDPOINT, MCC_MNC, BRANDING)
    }

    @Test
    fun shouldHaveValidValues() {
        assertEquals(ISSUER, configuration!!.issuer)
        assertEquals(AUTHORIZATION_ENDPOINT, configuration!!.authorizationEndpoint)
        assertEquals(MCC_MNC, configuration!!.mccMnc)
    }

    @Test
    fun shouldBeExpired() {
        val calendar = Calendar.getInstance()
        calendar.roll(Calendar.MINUTE, 20)
        val configuration = OpenIdConfiguration(ISSUER, AUTHORIZATION_ENDPOINT, MCC_MNC, BRANDING, calendar)
        assertFalse(configuration.isExpired)
    }

    @Test
    fun shouldNotBeExpired() {
        val configuration = OpenIdConfiguration(ISSUER, AUTHORIZATION_ENDPOINT, MCC_MNC, BRANDING, Calendar.getInstance())
        assertFalse(configuration.isExpired)
    }

    @Test
    fun shouldBeEqual() {
        val configuration1 = OpenIdConfiguration(ISSUER, AUTHORIZATION_ENDPOINT, MCC_MNC, BRANDING)
        val configuration2 = OpenIdConfiguration(configuration1)
        assertTrue(configuration1 == configuration2)
    }

    @Test
    fun shouldNotBeEqualIfIssuerDifferent() {
        val configuration1 = OpenIdConfiguration(ISSUER, AUTHORIZATION_ENDPOINT, MCC_MNC, BRANDING)
        val configuration2 = OpenIdConfiguration("any", AUTHORIZATION_ENDPOINT, MCC_MNC, BRANDING)
        assertFalse(configuration1 == configuration2)
    }

    @Test
    fun shouldNotBeEqualIfAuthorizationEndpointDifferent() {
        val configuration1 = OpenIdConfiguration(ISSUER, AUTHORIZATION_ENDPOINT, MCC_MNC, BRANDING)
        val configuration2 = OpenIdConfiguration(ISSUER, "any", MCC_MNC, BRANDING)
        assertFalse(configuration1 == configuration2)
    }

    @Test
    fun shouldNotBeEqualIfExpiredAtDifferent() {
        val calendar1 = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()
        calendar2.add(Calendar.MINUTE, 1)

        val configuration1 = OpenIdConfiguration(ISSUER, AUTHORIZATION_ENDPOINT, MCC_MNC, BRANDING, calendar1)
        val configuration2 = OpenIdConfiguration(ISSUER, AUTHORIZATION_ENDPOINT, MCC_MNC, BRANDING ,calendar2)
        assertFalse(configuration1 == configuration2)
    }

    @Test
    fun shouldNotBeEqualIfMCCMNCDifferent() {
        val configuration1 = OpenIdConfiguration(ISSUER, AUTHORIZATION_ENDPOINT, "first", BRANDING)
        val configuration2 = OpenIdConfiguration(ISSUER, AUTHORIZATION_ENDPOINT, "second", BRANDING)
        assertFalse(configuration1 == configuration2)
    }

    @Test
    fun shouldNotBeEqualIfTypeDifferent() {
        assertFalse(OpenIdConfiguration(ISSUER, AUTHORIZATION_ENDPOINT, MCC_MNC, BRANDING) == Any())
    }

    @Test
    fun shouldSetAndGetPackages() {
        val packages = ArrayList<Package>()
        configuration!!.packages = packages
        assertEquals(packages, configuration!!.packages)
    }

    companion object {
        private const val CARRIER_TEXT = "CARRIER_TEXT"
        private const val CARRIER_LOGO = "CARRIER_LOGO"
        private const val ISSUER = "ISSUER"
        private const val MCC_MNC = "MCC_MNC"
        private const val AUTHORIZATION_ENDPOINT = "AUTHORIZATION_ENDPOINT"
        private val BRANDING = Branding(CARRIER_TEXT, CARRIER_LOGO)
    }
}
