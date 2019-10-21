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
package com.xci.zenkey.sdk.internal

import androidx.test.filters.SmallTest

import org.junit.Test

import org.junit.Assert.assertEquals

@SmallTest
class JsonTest {

    @Test
    fun shouldUseValidKeys() {
        assertEquals("client_id", Json.KEY_CLIENT_ID)
        assertEquals("scope", Json.KEY_SCOPE)
        assertEquals("redirect_uri", Json.KEY_REDIRECT_URI)
        assertEquals("state", Json.KEY_STATE)
        assertEquals("response_type", Json.KEY_RESPONSE_TYPE)
        assertEquals("nonce", Json.KEY_NONCE)
        assertEquals("acr_values", Json.KEY_ACR_VALUES)
        assertEquals("prompt", Json.KEY_PROMPT)
        assertEquals("correlation_id", Json.KEY_CORRELATION_ID)
        assertEquals("context", Json.KEY_CONTEXT)
        assertEquals("error", Json.KEY_ERROR)
        assertEquals("error_description", Json.KEY_ERROR_DESCRIPTION)
        assertEquals("code", Json.KEY_CODE)
        assertEquals("mccmnc", Json.KEY_MCC_MNC)
    }
}
