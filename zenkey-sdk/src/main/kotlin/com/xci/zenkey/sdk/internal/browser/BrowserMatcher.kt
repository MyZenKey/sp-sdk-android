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
package com.xci.zenkey.sdk.internal.browser

/**
 * Determines whether a [BrowserDescriptor] matches some set of criteria.
 * Implementations of this type can be used to control the set of browsers used by AppAuth
 * for authorization.
 */
internal interface BrowserMatcher {

    /**
     * @return true if the browser matches some set of criteria.
     */
    fun matches(descriptor: BrowserDescriptor): Boolean

}