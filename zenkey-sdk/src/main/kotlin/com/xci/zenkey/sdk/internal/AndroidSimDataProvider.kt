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
package com.xci.zenkey.sdk.internal

import android.content.Context
import android.telephony.TelephonyManager

import com.xci.zenkey.sdk.internal.contract.SimDataProvider

/**
 * A [SimDataProvider] implementation.
 * This class is responsible to provide the MCC/MNC info of the device SIM card.
 */
internal class AndroidSimDataProvider internal constructor(
        context: Context,
        private val telephonyManager: TelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
) : SimDataProvider {

    /**
     * Get the MCC/MNC tuple of the SIM Operator for the primary SIM slot.
     *
     * @return the MCC/MNC tuple of the SIM in the first slot
     * or null if the SIM Card isn't Ready.
     */
    override val simOperator: String?
        get() {
            return if (telephonyManager.simState == TelephonyManager.SIM_STATE_READY) {
                telephonyManager.simOperator
            } else null
        }
}
