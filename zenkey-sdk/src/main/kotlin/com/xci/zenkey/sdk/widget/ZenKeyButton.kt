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
@file:Suppress("DEPRECATION")
package com.xci.zenkey.sdk.widget

import android.app.Activity
import android.app.Fragment
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import com.xci.zenkey.sdk.AuthorizeIntentBuilder
import com.xci.zenkey.sdk.R
import com.xci.zenkey.sdk.internal.BaseContentProvider
import com.xci.zenkey.sdk.internal.DefaultContentProvider
import com.xci.zenkey.sdk.internal.ktx.*
import com.xci.zenkey.sdk.internal.ktx.getColor
import com.xci.zenkey.sdk.internal.ktx.getDrawable
import com.xci.zenkey.sdk.internal.ktx.inflate
import com.xci.zenkey.sdk.param.ACR
import com.xci.zenkey.sdk.param.Prompt
import com.xci.zenkey.sdk.param.Scope
import com.xci.zenkey.sdk.param.Theme
import java.lang.ref.WeakReference

/**
 * This class is a custom component representing the [ZenKeyButton].
 * This custom component is following ZenKey design guidelines.
 * All the parameters for the request associated with this button can be set using the appropriate methods.
 * If you use this button inside a Fragment, you must also set Fragment using [ZenKeyButton.setFragment] in order to receive the result inside your [Fragment.onActivityResult]
 * When the button is clicked, this class will start the authorization request [Intent] automatically.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class ZenKeyButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    enum class Mode constructor(
            internal val contentColor: Int,
            internal val backgroundDrawable: Int
    ) {
        DARK(android.R.color.white, R.drawable.ripple_dark),
        LIGHT(R.color.zenkey_green, R.drawable.ripple_light)
    }

    enum class Text constructor(
            internal val stringResId: Int
    ) {
        CONTINUE(R.string.continue_with_zenkey),
        SIGN_IN(R.string.sign_in_with_zenkey)
    }

    internal var requestCode = DEFAULT_REQUEST_CODE
    internal lateinit var intentBuilder: AuthorizeIntentBuilder
    internal lateinit var button: Button
    internal var fragment: WeakReference<Fragment>? = null
    internal var enablePoweredBy: Boolean = false
    internal var mode: Mode = Mode.DARK
    internal var text: Text = Text.SIGN_IN
    internal var clickListener: OnClickListener? = null

    init {
        inflate(R.layout.zenkey_button)
        init(attrs)
    }

    /**
     * Init the child views and apply style from XML attributes.
     */
    internal fun init(attrs: AttributeSet?) {
        if (!isInEditMode) {
            this.intentBuilder = BaseContentProvider.identityProvider().authorizeIntent()
        }
        this.button = findViewById(R.id.zenkeyInternalButton)
        this.button.setOnClickListener { this@ZenKeyButton.onClick(context.activity()) }
        setBackgroundColor(getColor(android.R.color.transparent))

        extractAttributes(attrs)
        //applyPoweredBy()
        applyMode()
        applyText()
    }

    internal fun extractAttributes(attrs: AttributeSet?) {
        attrs?.let {
            val arr = context.obtainStyledAttributes(attrs, R.styleable.ZenKeyButton)
            mode = Mode.values()[arr.getInt(R.styleable.ZenKeyButton_ZenKeyButtonMode, mode.ordinal)]
            text = Text.values()[arr.getInt(R.styleable.ZenKeyButton_ZenKeyButtonText, text.ordinal)]
            //enablePoweredBy = arr.getBoolean(R.styleable.ZenKeyButton_enablePoweredBy, enablePoweredBy)
            arr.recycle()
        }
    }

    /**
     * Set the text of the Button.
     * @param text a {@Text} enum value.
     */
    fun setText(text: Text) {
        this.text = text
        applyText()
    }

    /**
     * Set the [com.xci.zenkey.sdk.internal.model.AuthorizationRequest] scopes.
     * @param scopes the [Scope] list of the [com.xci.zenkey.sdk.internal.model.AuthorizationRequest]
     */
    fun setScopes(vararg scopes: Scope) {
        this.intentBuilder.withScopes(*scopes)
    }

    /**
     * Set the [com.xci.zenkey.sdk.internal.model.AuthorizationRequest] redirect [Uri].
     * @param redirectUri the redirect [Uri] of the [com.xci.zenkey.sdk.internal.model.AuthorizationRequest]
     */
    fun setRedirectUri(redirectUri: Uri) {
        this.intentBuilder.withRedirectUri(redirectUri)
    }

    /**
     * Set the request code to use for the request when starting the [Intent].
     * @param requestCode the request code to use.
     */
    fun setRequestCode(requestCode: Int) {
        this.requestCode = requestCode
    }

    /**
     * Set the [com.xci.zenkey.sdk.internal.model.AuthorizationRequest] state.
     * @param state the state of the [com.xci.zenkey.sdk.internal.model.AuthorizationRequest]
     */
    fun setState(state: String) {
        this.intentBuilder.withState(state)
    }

    /**
     * Set the [com.xci.zenkey.sdk.internal.model.AuthorizationRequest] [ACR].
     * @param acrValues the [ACR] of the [com.xci.zenkey.sdk.internal.model.AuthorizationRequest]
     */
    fun setAcrValues(vararg acrValues: ACR) {
        this.intentBuilder.withAcrValues(*acrValues)
    }

    /**
     * Set the [com.xci.zenkey.sdk.internal.model.AuthorizationRequest] nonce.
     * @param nonce the nonce of the [com.xci.zenkey.sdk.internal.model.AuthorizationRequest]
     */
    fun setNonce(nonce: String) {
        this.intentBuilder.withNonce(nonce)
    }

    /**
     * Set the [com.xci.zenkey.sdk.internal.model.AuthorizationRequest] correlation Id.
     * @param correlationId the correlation id of the [com.xci.zenkey.sdk.internal.model.AuthorizationRequest]
     */
    fun setCorrelationId(correlationId: String) {
        this.intentBuilder.withCorrelationId(correlationId)
    }

    /**
     * Set the [com.xci.zenkey.sdk.internal.model.AuthorizationRequest] [Prompt].
     * @param prompt the [Prompt] id of the [com.xci.zenkey.sdk.internal.model.AuthorizationRequest]
     */
    fun setPrompt(vararg prompt: Prompt) {
        this.intentBuilder.withPrompt(*prompt)
    }

    /**
     * Set the [com.xci.zenkey.sdk.internal.model.AuthorizationRequest] context.
     * @param context the context id of the [com.xci.zenkey.sdk.internal.model.AuthorizationRequest]
     */
    fun setContext(context: String) {
        this.intentBuilder.withContext(context)
    }

    /**
     * Set the visual Theme (DARK or LIGHT) to be used when the user consents to the request
     * @param theme the Theme to use when asking the user for consent
     */
    fun setTheme(theme: Theme?) {
        this.intentBuilder.withTheme(theme)
    }

    /**
     * Set the requesting [Fragment].
     * @param fragment the requesting [Fragment]
     */
    fun setFragment(fragment: Fragment) {
        this.fragment = WeakReference(fragment)
    }

    /**
     * Set a pending intent to start in case of success.
     * @param successIntent the pending intent to start in case of success.
     */
    fun setSuccessIntent(successIntent: PendingIntent?) {
        this.intentBuilder.withSuccessIntent(successIntent)
    }

    /**
     * Set a pending intent to start in case of failure.
     * This intent isn't started in case of cancellation.
     * @param failureIntent the pending intent to start in case of failure.
     */
    fun setFailureIntent(failureIntent: PendingIntent?) {
        this.intentBuilder.withFailureIntent(failureIntent)
    }

    /**
     * Set a pending intent to start in case of completion.
     * If the request is successful and the [ZenKeyButton.setSuccessIntent] is present, this intent will not be started.
     * If the request isn't successful and the [ZenKeyButton.setFailureIntent] is present, this intent will not be started.
     * @param completedIntent the pending intent to start in case of completion.
     */
    fun setCompletionIntent(completedIntent: PendingIntent?) {
        this.intentBuilder.withCompletionIntent(completedIntent)
    }

    /**
     * Set a pending intent to start in case of cancellation.
     * @param canceledIntent the pending intent to start in case of cancellation.
     */
    fun setCancellationIntent(canceledIntent: PendingIntent?) {
        this.intentBuilder.withCancellationIntent(canceledIntent)
    }

    /**
     * Set the [android.widget.Button] [Mode].
     * @param mode The [android.widget.Button] [Mode]
     */
    @Suppress("unused")
    fun setMode(mode: Mode) {
        this.mode = mode
        applyMode()
    }

    /**
     * Called when the button is clicked.
     */
    internal fun onClick(activity: Activity?) {
        clickListener?.onClick(this)
        startRequest(activity, buildAuthorizationIntent())
    }

    /**
     * Start the authorization request.
     * @param intent the intent to start.
     */
    internal fun startRequest(activity: Activity?, intent: Intent) {
        if (fragment != null) {
            fragment!!.get()?.startActivityForResult(intent, requestCode)
        } else {
            activity?.startActivityForResult(intent, requestCode)
        }
    }

    /**
     * Build the authorization [Intent]
     * @return the authorization [Intent]
     */
    internal fun buildAuthorizationIntent(): Intent {
        return this.intentBuilder.build()
    }

    internal fun applyMode() {
        applyTextColor()
        applyIcon()
        applyBackGround()
    }

    private fun applyTextColor(){
        this.button.setTextColor(getColor(mode.contentColor))
    }

    private fun applyIcon(){
        getDrawable(R.drawable.ic_zenkey_white)?.let {
            it.setColorFilter(getColor(mode.contentColor), PorterDuff.Mode.SRC_ATOP)
            this.button.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
        }
    }

    private fun applyBackGround(){
        getDrawable(mode.backgroundDrawable)?.let {
            this.button.background = it
        }
    }

    internal fun applyText() {
        this.button.setText(text.stringResId)
        this.button.contentDescription = context.getString(text.stringResId)
    }

    internal fun applyPoweredBy() {
        if (enablePoweredBy) {
            val carrierEndorsementView = findViewById<CarrierEndorsementView>(R.id.carrierEndorsementView)
            carrierEndorsementView.visibility = View.VISIBLE
            if(!isInEditMode) {
                DefaultContentProvider.discoveryService.discoverConfiguration(
                        context.telephonyManager.simOperatorReady,
                        false,
                        {
                            carrierEndorsementView.setCarrier(it.branding.carrierText, null, mode)
                        }, {

                })
            } else {
                carrierEndorsementView.setCarrier("Powered By MNO", null, mode)
            }
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        clickListener = l
    }

    companion object {
        const val DEFAULT_REQUEST_CODE = 1234
    }
}
