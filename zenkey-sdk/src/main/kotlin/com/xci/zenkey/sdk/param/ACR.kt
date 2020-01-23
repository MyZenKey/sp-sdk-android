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
package com.xci.zenkey.sdk.param

/**
 * This enum is representing the available ACR parameters for an authorization request.
 * SPs may ask for more than one, and will get the first one the user has achieved.
 *
 * SP’s should ask for aal1 when they need a low level of authentication,
 * users will not be asked for their pin or biometrics.
 * Any user holding the device will be able to authenticate/authorize the transaction unless
 * the user has configured their account to always require 2nd factor (pin | bio).
 *
 * SP’s should ask for aal2 or aal3 anytime they want to ensure the user has provided their (pin | bio).
 */
enum class ACR(val value: String) {
    /**
     * Device token
     */
    AAL1("a1"),
    /**
     *
     */
    AAL2("a2"),
    /**
     * New device registered + carrier auth + 2nd factor.
     * Remembered Device (aka) + user (pin or bio)
     */
    AAL3("a3");
}