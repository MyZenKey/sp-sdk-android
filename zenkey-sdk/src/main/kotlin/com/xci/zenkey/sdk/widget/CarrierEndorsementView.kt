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
package com.xci.zenkey.sdk.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.xci.zenkey.sdk.R
import com.xci.zenkey.sdk.internal.ktx.getColor
import com.xci.zenkey.sdk.internal.ktx.inflate
import java.util.*

internal class CarrierEndorsementView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
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