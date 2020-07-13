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
package com.xci.zenkey.sdk.internal

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.util.Base64
import com.xci.zenkey.sdk.AuthorizeIntentBuilder
import com.xci.zenkey.sdk.internal.ktx.proofKeyForCodeExchange
import com.xci.zenkey.sdk.internal.ktx.encodeToString
import com.xci.zenkey.sdk.internal.ktx.random
import com.xci.zenkey.sdk.internal.model.AuthorizationRequest
import com.xci.zenkey.sdk.param.ACR
import com.xci.zenkey.sdk.param.Prompt
import com.xci.zenkey.sdk.param.Scope
import com.xci.zenkey.sdk.param.Scopes
import com.xci.zenkey.sdk.param.Theme
import java.security.MessageDigest
import java.util.*

/**
 * This class is an @[Intent] build for authorization request.
 * This class is responsible to build the authorization intent containing all the requested or default parameters.
 */
class DefaultAuthorizeIntentBuilder(
        private val packageName: String,
        private val clientId: String,
        private val messageDigest: MessageDigest?,
        private var redirectUri: Uri
) : AuthorizeIntentBuilder {

    private var scopes: List<Scope> = listOf(Scopes.OPEN_ID)
    internal var state: String? = null
    private var acrValues: List<ACR>? = null
    private var nonce: String? = null
    private var correlationId: String? = null
    private var context: String? = null
    private var prompt: List<Prompt>? = null
    private var completedIntent: PendingIntent? = null
    private var canceledIntent: PendingIntent? = null
    private var successIntent: PendingIntent? = null
    private var failureIntent: PendingIntent? = null
    private var theme: Theme? = null

    init {
        this.state = random().encodeToString(Base64.NO_WRAP or Base64.NO_PADDING or Base64.URL_SAFE)
    }

    /**
     * Set the scopes for the request.
     * @param scopes the scopes to use for the request.
     * @return this [AuthorizeIntentBuilder] instance
     */
    override fun withScopes(vararg scopes: Scope): AuthorizeIntentBuilder {
        this.scopes = listOf(*scopes).distinct()
        return this
    }

    /**
     * Set the redirect [Uri] for the request.
     * @param redirectUri the redirectUri to use for the request.
     * @return this [AuthorizeIntentBuilder] instance
     */
    override fun withRedirectUri(redirectUri: Uri): AuthorizeIntentBuilder {
        this.redirectUri = redirectUri
        return this
    }

    /**
     * Set the state for the request.
     * @param state the state to use for the request.
     * @return this [DefaultAuthorizeIntentBuilder] instance
     */
    override fun withState(state: String): AuthorizeIntentBuilder {
        this.state = state.encodeToString(flags = Base64.NO_WRAP or Base64.NO_PADDING or Base64.URL_SAFE)
        return this
    }

    /**
     * Remove the default state for the request.
     * @return this [DefaultAuthorizeIntentBuilder] instance
     */
    override fun withoutState(): AuthorizeIntentBuilder {
        this.state = null
        return this
    }

    /**
     * Set the [ACR] values for the request.
     * @param acrValues the [ACR] values to use for the request.
     * @return this [DefaultAuthorizeIntentBuilder] instance
     */
    override fun withAcrValues(vararg acrValues: ACR): AuthorizeIntentBuilder {
        this.acrValues = listOf(*acrValues)
        return this
    }

    /**
     * Set the nonce for the request.
     * @param nonce the nonce value to use for the request.
     * @return this [DefaultAuthorizeIntentBuilder] instance
     */
    override fun withNonce(nonce: String): AuthorizeIntentBuilder {
        this.nonce = nonce
        return this
    }

    /**
     * Set the correlationId for the request.
     * @param correlationId the correlationId value to use for the request.
     * @return this [DefaultAuthorizeIntentBuilder] instance
     */
    override fun withCorrelationId(correlationId: String): AuthorizeIntentBuilder {
        this.correlationId = correlationId
        return this
    }

    /**
     * Set the prompts for the request.
     * @param prompts the prompts values to use for the request.
     * @return this [DefaultAuthorizeIntentBuilder] instance
     */
    override fun withPrompt(vararg prompts: Prompt): AuthorizeIntentBuilder {
        this.prompt = ArrayList(listOf(*prompts))
        return this
    }

    /**
     * Set the context for the request.
     * @param context the context value to use for the request.
     * @return this [DefaultAuthorizeIntentBuilder] instance
     */
    override fun withContext(context: String): AuthorizeIntentBuilder {
        this.context = context
        return this
    }


    /**
     * Set the theme (dark or light) for CCID app to use when displaying
     * the request.
     * @param theme the Theme value to use for the request (dark or light).
     * @return this [AuthorizeIntentBuilder] instance
     */
    override fun withTheme(theme: Theme?): AuthorizeIntentBuilder {
        this.theme = theme
        return this
    }

    /**
     * Set a pending intent to start in case of success.
     * @param successIntent the pending intent to start in case of success.
     * @return this [DefaultAuthorizeIntentBuilder] instance
     */
    override fun withSuccessIntent(successIntent: PendingIntent?): AuthorizeIntentBuilder {
        this.successIntent = successIntent
        return this
    }

    /**
     * Set a pending intent to start in case of failure.
     * This intent isn't started in case of cancellation.
     * @param failureIntent the pending intent to start in case of failure.
     * @return this [DefaultAuthorizeIntentBuilder] instance
     */
    override fun withFailureIntent(failureIntent: PendingIntent?): AuthorizeIntentBuilder {
        this.failureIntent = failureIntent
        return this
    }

    /**
     * Set a pending intent to start in case of completion.
     * If the request is successful and the [DefaultAuthorizeIntentBuilder.withSuccessIntent] is present, this intent will not be started.
     * If the request isn't successful and the [DefaultAuthorizeIntentBuilder.withFailureIntent] is present, this intent will not be started.
     * @param completedIntent the pending intent to start in case of completion.
     */
    override fun withCompletionIntent(completedIntent: PendingIntent?): AuthorizeIntentBuilder {
        this.completedIntent = completedIntent
        return this
    }

    /**
     * Set a pending intent to start in case of cancellation.
     * @param canceledIntent the pending intent to start in case of cancellation.
     * @return this [DefaultAuthorizeIntentBuilder] instance
     */
    override fun withCancellationIntent(canceledIntent: PendingIntent?): AuthorizeIntentBuilder {
        this.canceledIntent = canceledIntent
        return this
    }

    /**
     * Build this request [Intent]
     * @return an [Intent] containing all the parameters, to start in order to perform the request.
     * The [Intent] must be started using [android.app.Activity.startActivityForResult]
     */
    override fun build(): Intent {
        return AuthorizationRequestActivity.createStartForResultIntent(packageName,
                AuthorizationRequest(clientId, redirectUri,
                        scope(), state, acr(), nonce, prompt(), correlationId, context, messageDigest.proofKeyForCodeExchange, options()),
                successIntent, failureIntent, completedIntent, canceledIntent)
    }

    /**
     * Build the scope parameter.
     *
     * @return the scope parameter [String]
     */
    internal fun scope(): String? {
        return if (scopes.isNotEmpty())
            scopes.joinToString(separator = " ") { it.value }
        else
            null
    }

    /**
     * Build the Prompt parameter.
     *
     * @return the prompt parameter [String]
     */
    internal fun prompt(): String? {
        return prompt?.let { prompts ->
            if (prompts.isEmpty()) null
            else prompts.joinToString(separator = " ") { it.value }
        }
    }

    /**
     * Build the ACR parameter.
     *
     * @return the ACR parameter [String]
     */
    internal fun acr(): String? {
        return acrValues?.let { ACR ->
            if (ACR.isEmpty()) null
            else ACR.joinToString(separator = " ") { it.value }
        }
    }

    /**
     * Build the options parameter.
     * This parameter covers the theme and will be expanded for more in the future
     *
     * @return the options parameter [String]
     */
    internal fun options(): String? {
        return theme?.value
    }
}
