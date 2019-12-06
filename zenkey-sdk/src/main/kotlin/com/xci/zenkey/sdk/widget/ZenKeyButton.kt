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
@file:Suppress("DEPRECATION")

package com.xci.zenkey.sdk.widget

import android.app.Activity
import android.app.Fragment
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.support.annotation.Keep
import android.support.annotation.NonNull
import android.support.annotation.RequiresApi
import android.support.annotation.VisibleForTesting
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.xci.zenkey.sdk.AuthorizeIntentBuilder
import com.xci.zenkey.sdk.R
import com.xci.zenkey.sdk.internal.BaseContentProvider
import com.xci.zenkey.sdk.internal.DefaultContentProvider
import com.xci.zenkey.sdk.internal.DiscoveryService
import com.xci.zenkey.sdk.internal.ktx.activity
import com.xci.zenkey.sdk.internal.ktx.getColor
import com.xci.zenkey.sdk.internal.ktx.getDrawable
import com.xci.zenkey.sdk.internal.ktx.inflate
import com.xci.zenkey.sdk.param.ACR
import com.xci.zenkey.sdk.param.Prompt
import com.xci.zenkey.sdk.param.Scope
import java.lang.ref.WeakReference

/**
 * This class is a custom component representing the [ZenKeyButton].
 * This custom component is following ZenKey design guidelines.
 * All the parameters for the request associated with this button can be set using the appropriate methods.
 * If you use this button inside a Fragment, you must also set Fragment using [ZenKeyButton.setFragment] in order to receive the result inside your [Fragment.onActivityResult]
 * When the button is clicked, this class will start the authorization request [Intent] automatically.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
@Keep
class ZenKeyButton
    : FrameLayout {

    @VisibleForTesting
    internal var requestCode = DEFAULT_REQUEST_CODE
    @VisibleForTesting
    internal lateinit var intentBuilder: AuthorizeIntentBuilder
    @VisibleForTesting
    internal lateinit var button: Button
    @VisibleForTesting
    internal var fragment: WeakReference<Fragment>? = null
    @VisibleForTesting
    internal var mode: Mode = Mode.DARK
    @VisibleForTesting
    internal var text: Text = Text.SIGN_IN
    @VisibleForTesting
    internal var enablePoweredBy: Boolean = false

    @Keep
    enum class Mode constructor(internal val contentColor: Int, internal val backgroundDrawable: Int) {
        DARK(android.R.color.white, R.drawable.ripple_dark),
        LIGHT(R.color.zenkey_green, R.drawable.ripple_light)
    }

    @Keep
    enum class Text constructor(internal val stringResId: Int) {
        CONTINUE(R.string.continue_with_zenkey),
        SIGN_IN(R.string.sign_in_with_zenkey)
    }

    /**
     * Constructor for [ZenKeyButton]
     * @param context the [Context] used to create this view.
     */
    constructor(context: Context) : super(context) {
        inflate()
        init(null)
    }

    /**
     * Constructor for [ZenKeyButton]
     * @param context the [Context] used to create this view.
     * @param attrs the attributes set inside the XML
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        inflate()
        init(attrs)
    }

    /**
     * Constructor for [ZenKeyButton]
     * @param context the [Context] used to create this view.
     * @param attrs the attributes set inside the XML
     * @param defStyleAttr the default style attributes
     */
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        inflate()
        init(attrs)
    }

    /**
     * Constructor for [ZenKeyButton]
     * @param context the [Context] used to create this view.
     * @param attrs the attributes set inside the XML
     * @param defStyleAttr the default style attributes
     * @param defStyleRes the default style resources.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        inflate()
        init(attrs)
    }

    /**
     * Inflate the XML associated with this custom component.
     * @param context the context to use for the inflation.
     */
    private fun inflate() {
        inflate(R.layout.zenkey_button)
    }

    /**
     * Init the child views and apply style from XML attributes.
     * @param attrs the XML attributes.
     */
    @VisibleForTesting
    internal fun init(attrs: AttributeSet?) {
        if (!isInEditMode) {
            this.intentBuilder = BaseContentProvider.identityProvider().authorizeIntent()
        }
        this.button = findViewById(R.id.zenkeyInternalButton)
        this.button.setOnClickListener { this@ZenKeyButton.onClick(context.activity()) }
        setBackgroundColor(getColor(android.R.color.transparent))

        extractAttributes(attrs)
        applyPoweredBy()
        applyMode()
        applyText()
    }

    @VisibleForTesting
    internal fun extractAttributes(attrs: AttributeSet?) {
        attrs?.let {
            val arr = context.obtainStyledAttributes(attrs, R.styleable.ZenKeyButton)
            mode = Mode.values()[arr.getInt(R.styleable.ZenKeyButton_mode, mode.ordinal)]
            text = Text.values()[arr.getInt(R.styleable.ZenKeyButton_text, text.ordinal)]
            enablePoweredBy = arr.getBoolean(R.styleable.ZenKeyButton_enablePoweredBy, enablePoweredBy)
            arr.recycle()
        }
    }

    /**
     * Set the text of the Button.
     * @param text a {@Text} enum value.
     */
    fun setText(@NonNull text: Text) {
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
    @VisibleForTesting
    internal fun onClick(activity: Activity?) {
        startRequest(activity, buildAuthorizationIntent())
    }

    /**
     * Start the authorization request.
     * @param view the clicked button.
     * @param intent the intent to start.
     */
    @VisibleForTesting
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
    @VisibleForTesting
    internal fun buildAuthorizationIntent(): Intent {
        return this.intentBuilder.build()
    }

    @VisibleForTesting
    internal fun applyMode() {
        val icon = getDrawable(R.drawable.ic_zenkey_white)
        this.button.setTextColor(getColor(mode.contentColor))
        icon.setColorFilter(getColor(mode.contentColor), PorterDuff.Mode.SRC_ATOP)
        this.button.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
        this.button.background = getDrawable(mode.backgroundDrawable)
    }

    @VisibleForTesting
    internal fun applyText() {
        this.button.setText(text.stringResId)
        this.button.contentDescription = context.getString(text.stringResId)
    }

    @VisibleForTesting
    internal fun applyPoweredBy() {
        if (enablePoweredBy) {
            val carrierEndorsementView = findViewById<CarrierEndorsementView>(R.id.carrierEndorsementView)
            carrierEndorsementView.visibility = View.VISIBLE
            if(!isInEditMode) {
                DefaultContentProvider.discoveryService.discoverConfiguration(
                        DefaultContentProvider.simDataProvider.simOperator,
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

    companion object {
        const val DEFAULT_REQUEST_CODE = 1234
    }
}
