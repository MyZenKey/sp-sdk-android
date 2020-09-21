/**
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
package com.xci.zenkey.example.client.model

import com.squareup.moshi.Json

data class UserResponse (
    @Json(name = "username")
    val username: String? = null,
    @Json(name = "user_id")
    val userId: Long? = null,
    @Json(name = "zenkey_sub")
    val zenkeySub: String? = null,
    @Json(name = "name")
    val name: NameResponse? = null,
    @Json(name = "email")
    val email: StringResponse? = null,
    @Json(name = "postal_code")
    val postalCode: StringResponse? = null,
    @Json(name = "phone_number")
    val phoneNumber: StringResponse? = null,
    @Json(name = "birthdate")
    val birthdate: StringResponse? = null,
    @Json(name = "address")
    val address: AddressResponse? = null
)