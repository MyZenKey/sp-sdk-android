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
package com.xci.zenkey.sdk

/**
 * This class is an enum representing the various error a developer might receive from a ZenKey SDK authorization request.
 */
enum class AuthorizationError {

    /**
     * The configuration is invalid, please check the clientId you registered.
     */
    INVALID_CONFIGURATION,
    /**
     * One or more parameter in invalid in the request.
     */
    INVALID_REQUEST,
    /**
     * The request was denied by the user.
     */
    REQUEST_DENIED,
    /**
     * The request timed-out.
     */
    REQUEST_TIMEOUT,
    /**
     * A Server error occurred.
     */
    SERVER_ERROR,
    /**
     * The network isn't available, the device is offline.
     */
    NETWORK_FAILURE,
    /**
     * A problem occurred during discovery phase.
     */
    DISCOVERY_STATE,
    /**
     * The error isn't expected by the ZenKey SDK, please check the error description.
     */
    UNKNOWN;

    private var description: String? = null

    /**
     * Set the error description.
     * @param description the error description.
     * @return this [AuthorizationError] instance.
     */
    fun withDescription(description: String?): AuthorizationError {
        this.description = description
        return this
    }

    /**
     * Get the error description.
     * @return the error description.
     */
    fun description(): String? {
        return description
    }

    override fun toString(): String {
        return "AuthorizationError($name - description=$description)"
    }
}

