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
package com.xci.zenkey.sdk.internal.contract

/**
 * Contract for a cache implementation.
 *
 * @param <KEY>   the cache KEY type.
 * @param <VALUE> the cached VALUE type.
</VALUE></KEY> */
internal interface ICache<KEY, VALUE> {

    /**
     * Check if the cache contain a [VALUE] for the [KEY]
     *
     * @param key the [KEY] to lookup.
     * @return true if the cache contain the [KEY], false else.
     */
    fun contain(key: KEY): Boolean

    /**
     * Get the [VALUE] for the [KEY]
     *
     * @param key the [KEY] key to lookup.
     * @return the [VALUE] associated with the [KEY],
     * or null if the cache doesn't contain any [VALUE] for the [KEY].
     */
    operator fun get(key: KEY): VALUE?

    /**
     * Put a [VALUE] in the cache with the associated [KEY].
     *
     * @param key   the [KEY] key to use.
     * @param value the [VALUE] to put in cache.
     */
    fun put(key: KEY, value: VALUE)

    /**
     * Remove a [VALUE] from cache associated with [KEY].
     *
     * @param key the [KEY] to remove from cache.
     */
    fun remove(key: KEY)
}
