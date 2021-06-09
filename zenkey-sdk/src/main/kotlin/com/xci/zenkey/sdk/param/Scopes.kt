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
package com.xci.zenkey.sdk.param

/**
 * This enum is representing the available Scope parameters for an authorization request.
 */
enum class Scopes(override val value: String)
    : Scope {
    OPEN_ID("openid"),
    EMAIL("email"),
    NAME("name"),
    PHONE("phone"),
    POSTAL_CODE("postal_code"),
    ADDRESS("address"),
    BIRTH_DATE("birthdate"),
    PROOFING("proofing"),
    LAST_4_SOCIAL("last_4_social"),
}