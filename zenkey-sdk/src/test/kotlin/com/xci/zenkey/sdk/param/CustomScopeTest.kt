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

import org.junit.Test

import androidx.test.filters.SmallTest

import com.xci.zenkey.sdk.param.CustomScope
import com.xci.zenkey.sdk.param.Scope

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull

@SmallTest
class CustomScopeTest {

    @Test
    fun shouldBuildScopeObjectFromValue() {
        val scope = CustomScope.fromValue(VALUE)
        assertNotNull(scope)
        assertEquals(VALUE, scope.value)
    }

    companion object {
        private const val VALUE = "VALUE"
    }
}
