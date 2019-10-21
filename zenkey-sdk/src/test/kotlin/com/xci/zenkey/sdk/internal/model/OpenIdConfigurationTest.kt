/*
 * Copyright 2019 XCI JV, LLC.
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
        configuration = OpenIdConfiguration(ISSUER, AUTHORIZATION_ENDPOINT, MCC_MNC)
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
        val configuration = OpenIdConfiguration(ISSUER, AUTHORIZATION_ENDPOINT, MCC_MNC, calendar)
        assertFalse(configuration.isExpired)
    }

    @Test
    fun shouldNotBeExpired() {
        val configuration = OpenIdConfiguration(ISSUER, AUTHORIZATION_ENDPOINT, MCC_MNC, Calendar.getInstance())
        assertFalse(configuration.isExpired)
    }

    @Test
    fun shouldBeEqual() {
        val configuration1 = OpenIdConfiguration(ISSUER, AUTHORIZATION_ENDPOINT, MCC_MNC)
        val configuration2 = OpenIdConfiguration(configuration1)
        assertTrue(configuration1 == configuration2)
    }

    @Test
    fun shouldNotBeEqualIfIssuerDifferent() {
        val configuration1 = OpenIdConfiguration(ISSUER, AUTHORIZATION_ENDPOINT, MCC_MNC)
        val configuration2 = OpenIdConfiguration("any", AUTHORIZATION_ENDPOINT, MCC_MNC)
        assertFalse(configuration1 == configuration2)
    }

    @Test
    fun shouldNotBeEqualIfAuthorizationEndpointDifferent() {
        val configuration1 = OpenIdConfiguration(ISSUER, AUTHORIZATION_ENDPOINT, MCC_MNC)
        val configuration2 = OpenIdConfiguration(ISSUER, "any", MCC_MNC)
        assertFalse(configuration1 == configuration2)
    }

    @Test
    fun shouldNotBeEqualIfExpiredAtDifferent() {
        val calendar1 = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()
        calendar2.add(Calendar.MINUTE, 1)

        val configuration1 = OpenIdConfiguration(ISSUER, AUTHORIZATION_ENDPOINT, MCC_MNC, calendar1)
        val configuration2 = OpenIdConfiguration(ISSUER, AUTHORIZATION_ENDPOINT, MCC_MNC, calendar2)
        assertFalse(configuration1 == configuration2)
    }

    @Test
    fun shouldNotBeEqualIfMCCMNCDifferent() {
        val configuration1 = OpenIdConfiguration(ISSUER, AUTHORIZATION_ENDPOINT, "first")
        val configuration2 = OpenIdConfiguration(ISSUER, AUTHORIZATION_ENDPOINT, "second")
        assertFalse(configuration1 == configuration2)
    }

    @Test
    fun shouldNotBeEqualIfTypeDifferent() {
        assertFalse(OpenIdConfiguration(ISSUER, AUTHORIZATION_ENDPOINT, MCC_MNC) == Any())
    }

    @Test
    fun shouldSetAndGetPackages() {
        val packages = ArrayList<Package>()
        configuration!!.packages = packages
        assertEquals(packages, configuration!!.packages)
    }

    companion object {
        private const val ISSUER = "ISSUER"
        private const val MCC_MNC = "MCC_MNC"
        private const val AUTHORIZATION_ENDPOINT = "AUTHORIZATION_ENDPOINT"
    }
}
