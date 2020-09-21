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
package com.xci.zenkey.sdk

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import com.xci.zenkey.sdk.param.ACR
import com.xci.zenkey.sdk.param.Prompt
import com.xci.zenkey.sdk.param.Scope
import com.xci.zenkey.sdk.param.Theme

/**
 * This class is a contract for an [Intent] builder for authorization request.
 */
interface AuthorizeIntentBuilder {

    /**
     * Set the scopes for the request.
     * @param scopes the scopes to use for the request.
     * @return this [AuthorizeIntentBuilder] instance
     */
    fun withScopes(vararg scopes: Scope): AuthorizeIntentBuilder

    /**
     * Set the redirect [Uri] for the request.
     * @param redirectUri the redirectUri to use for the request.
     * @return this [AuthorizeIntentBuilder] instance
     */
    fun withRedirectUri(redirectUri: Uri): AuthorizeIntentBuilder

    /**
     * Set the state for the request.
     * @param state the state to use for the request.
     * @return this [AuthorizeIntentBuilder] instance
     */
    fun withState(state: String): AuthorizeIntentBuilder

    /**
     * Remove the default state for the request.
     * @return this [AuthorizeIntentBuilder] instance
     */
    fun withoutState(): AuthorizeIntentBuilder

    /**
     * Set the [ACR] values for the request.
     * @param acrValues the [ACR] values to use for the request.
     * @return this [AuthorizeIntentBuilder] instance
     */
    fun withAcrValues(vararg acrValues: ACR): AuthorizeIntentBuilder

    /**
     * Set the nonce for the request.
     * @param nonce the nonce value to use for the request.
     * @return this [AuthorizeIntentBuilder] instance
     */
    fun withNonce(nonce: String): AuthorizeIntentBuilder

    /**
     * Set the correlationId for the request.
     * @param correlationId the correlationId value to use for the request.
     * @return this [AuthorizeIntentBuilder] instance
     */
    fun withCorrelationId(correlationId: String): AuthorizeIntentBuilder

    /**
     * Set the prompts for the request.
     * @param prompts the prompts values to use for the request.
     * @return this [AuthorizeIntentBuilder] instance
     */
    fun withPrompt(vararg prompts: Prompt): AuthorizeIntentBuilder

    /**
     * Set the context for the request.
     * @param context the context value to use for the request.
     * @return this [AuthorizeIntentBuilder] instance
     */
    fun withContext(context: String): AuthorizeIntentBuilder

    /**
     * Set the theme (dark or light) for CCID app to use when displaying
     * the request.
     * @param theme the Theme value to use for the request (dark or light).
     * @return this [AuthorizeIntentBuilder] instance
     */
    fun withTheme(theme: Theme?): AuthorizeIntentBuilder

    /**
     * Set a pending intent to start in case of success.
     * @param successIntent the pending intent to start in case of success.
     * @return this [AuthorizeIntentBuilder] instance
     */
    fun withSuccessIntent(successIntent: PendingIntent?): AuthorizeIntentBuilder

    /**
     * Set a pending intent to start in case of failure.
     * This intent isn't started in case of cancellation.
     * @param failureIntent the pending intent to start in case of failure.
     * @return this [AuthorizeIntentBuilder] instance
     */
    fun withFailureIntent(failureIntent: PendingIntent?): AuthorizeIntentBuilder

    /**
     * Set a pending intent to start in case of completion.
     * If the request is successful and the [AuthorizeIntentBuilder.withSuccessIntent] is present, this intent will not be started.
     * If the request isn't successful and the [AuthorizeIntentBuilder.withFailureIntent] is present, this intent will not be started.
     * @param completedIntent the pending intent to start in case of completion.
     */
    fun withCompletionIntent(completedIntent: PendingIntent?): AuthorizeIntentBuilder

    /**
     * Set a pending intent to start in case of cancellation.
     * @param canceledIntent the pending intent to start in case of cancellation.
     * @return this [AuthorizeIntentBuilder] instance
     */
    fun withCancellationIntent(canceledIntent: PendingIntent?): AuthorizeIntentBuilder

    /**
     * Build this request [Intent]
     * @return an [Intent] containing all the parameters, to start in order to perform the request.
     * The [Intent] must be started using [android.app.Activity.startActivityForResult]
     */
    fun build(): Intent
}
