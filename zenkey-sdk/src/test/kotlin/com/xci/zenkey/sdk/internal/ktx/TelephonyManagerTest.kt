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
package com.xci.zenkey.sdk.internal.ktx

import android.telephony.TelephonyManager
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TelephonyManagerTest {

    private val mockTelephonyManager = mock<TelephonyManager>()

    @Test
    fun shouldGetPrimarySimOperatorIfSIMReady() {
        whenever(mockTelephonyManager.simState).thenReturn(TelephonyManager.SIM_STATE_READY)
        whenever(mockTelephonyManager.simOperator).thenReturn(MCC_MNC)
        assertEquals(MCC_MNC, mockTelephonyManager.simOperatorReady)
    }

    @Test
    fun shouldNotGetPrimarySimOperatorIfSIMNotReady() {
        whenever(mockTelephonyManager.simState).thenReturn(TelephonyManager.SIM_STATE_NOT_READY)
        assertNull(mockTelephonyManager.simOperatorReady)
    }

    @Test
    fun shouldNotGetPrimarySimOperatorIfSIMAbsent() {
        whenever(mockTelephonyManager.simState).thenReturn(TelephonyManager.SIM_STATE_ABSENT)
        assertNull(mockTelephonyManager.simOperatorReady)
    }

    @Test
    fun shouldNotGetPrimarySimOperatorIfSIMIOError() {
        whenever(mockTelephonyManager.simState).thenReturn(TelephonyManager.SIM_STATE_CARD_IO_ERROR)
        assertNull(mockTelephonyManager.simOperatorReady)
    }

    @Test
    fun shouldNotGetPrimarySimOperatorIfSIMCardRestricted() {
        whenever(mockTelephonyManager.simState).thenReturn(TelephonyManager.SIM_STATE_CARD_RESTRICTED)
        assertNull(mockTelephonyManager.simOperatorReady)
    }

    @Test
    fun shouldNotGetPrimarySimOperatorIfSIMNetworkLocked() {
        whenever(mockTelephonyManager.simState).thenReturn(TelephonyManager.SIM_STATE_NETWORK_LOCKED)
        assertNull(mockTelephonyManager.simOperatorReady)
    }

    @Test
    fun shouldNotGetPrimarySimOperatorIfSIMDisabled() {
        whenever(mockTelephonyManager.simState).thenReturn(TelephonyManager.SIM_STATE_PERM_DISABLED)
        assertNull(mockTelephonyManager.simOperatorReady)
    }

    @Test
    fun shouldNotGetPrimarySimOperatorIfSIMPinRequired() {
        whenever(mockTelephonyManager.simState).thenReturn(TelephonyManager.SIM_STATE_PIN_REQUIRED)
        assertNull(mockTelephonyManager.simOperatorReady)
    }

    @Test
    fun shouldNotGetPrimarySimOperatorIfSIMPUKRequired() {
        whenever(mockTelephonyManager.simState).thenReturn(TelephonyManager.SIM_STATE_PUK_REQUIRED)
        assertNull(mockTelephonyManager.simOperatorReady)
    }

    @Test
    fun shouldNotGetPrimarySimOperatorIfSIMUnknownState() {
        whenever(mockTelephonyManager.simState).thenReturn(TelephonyManager.SIM_STATE_UNKNOWN)
        assertNull(mockTelephonyManager.simOperatorReady)
    }

    companion object {
        private const val MCC_MNC = "310230"
    }
}
