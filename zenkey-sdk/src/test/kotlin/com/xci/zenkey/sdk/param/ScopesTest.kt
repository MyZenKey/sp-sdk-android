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

import androidx.test.filters.SmallTest

import org.junit.Test

import org.junit.Assert.assertEquals

@SmallTest
class ScopesTest {

    @Test
    fun shouldUseExpectedValues() {
        assertEquals("name", Scopes.NAME.value)
        assertEquals("email", Scopes.EMAIL.value)
        assertEquals("phone", Scopes.PHONE.value)
        assertEquals("openid", Scopes.OPEN_ID.value)
        assertEquals("postal_code", Scopes.POSTAL_CODE.value)
        assertEquals("address", Scopes.ADDRESS.value)
        assertEquals("birthdate", Scopes.BIRTH_DATE.value)
    }
}
