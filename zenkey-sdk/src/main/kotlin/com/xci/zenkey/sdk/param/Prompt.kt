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
 * This enum is representing the available Prompt parameters for an authorization request.
 */
enum class Prompt(val value: String) {
    /**
     * The prompt parameter will have inconsistent support across the different carriers.
     * At this time ZenKey is not a silent user experience.
     * A user will need to approve a transaction with each request.
     * So prompt None should always return “user interaction required” which is a standard oauth2 response to this request.
     */
    NONE("none"),
    /**
     * An SP can ask for a user to authenticate again. (even if the user authenticated within the last sso authentication period (most carriers this will be 30 min).
     */
    LOGIN("login"),
    /**
     * An SP can ask for a user to explicitly re-confirm that the user agrees to the exposure of their data.
     * The carrier will recapture user consent for the listed scopes.
     */
    CONSENT("consent");
}