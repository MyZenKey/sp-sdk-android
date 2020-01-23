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

internal data class Branding(val carrierText: String,
                             val carrierLogo: String?){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Branding

        if (carrierText != other.carrierText) return false
        if (carrierLogo != other.carrierLogo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = carrierText.hashCode()
        result = 31 * result + (carrierLogo?.hashCode() ?: 0)
        return result
    }
}