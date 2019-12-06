package com.xci.zenkey.sdk.widget

import android.content.Context
import android.os.Build
import android.support.annotation.Keep
import android.support.annotation.RequiresApi
import android.support.annotation.VisibleForTesting
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.xci.zenkey.sdk.R
import com.xci.zenkey.sdk.internal.ktx.getColor
import com.xci.zenkey.sdk.internal.ktx.inflate
import java.util.*

@Suppress("unused", "MemberVisibilityCanBePrivate")
@Keep
internal class CarrierEndorsementView
    : FrameLayout {

    @VisibleForTesting
    internal lateinit var poweredByTextView: TextView
    @VisibleForTesting
    internal lateinit var poweredByImageView: ImageView

    /**
     * Constructor for [ZenKeyButton]
     * @param context the [Context] used to create this view.
     */
    constructor(context: Context)
            : super(context) {
        inflate()
    }

    /**
     * Constructor for [ZenKeyButton]
     * @param context the [Context] used to create this view.
     * @param attrs the attributes set inside the XML
     */
    constructor(context: Context, attrs: AttributeSet)
            : super(context, attrs) {
        inflate()
    }

    /**
     * Constructor for [ZenKeyButton]
     * @param context the [Context] used to create this view.
     * @param attrs the attributes set inside the XML
     * @param defStyleAttr the default style attributes
     */
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        inflate()
    }

    /**
     * Constructor for [ZenKeyButton]
     * @param context the [Context] used to create this view.
     * @param attrs the attributes set inside the XML
     * @param defStyleAttr the default style attributes
     * @param defStyleRes the default style resources.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
        inflate()
    }

    private fun inflate() {
        inflate(R.layout.powered_by_layout)
    }

    @UseExperimental(ExperimentalStdlibApi::class)
    internal fun setCarrier(carrierText: String,
                            carrierLogo: String?,
                            mode: ZenKeyButton.Mode) {
        val textView = findViewById<TextView>(R.id.zenKeyPoweredByTextView)
        val carrierLayout = findViewById<View>(R.id.carrierLayout)

        if (carrierLogo == null) {
            textView.setTextColor(if (mode == ZenKeyButton.Mode.DARK) getColor(android.R.color.black) else getColor(android.R.color.white))
            textView.text = carrierText.capitalize(Locale.US)
        }

        carrierLayout.visibility = View.VISIBLE
    }
}