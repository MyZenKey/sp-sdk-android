/*
 * Copyright 2019 ZenKey, LLC.
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

package com.xci.zenkey.sdk.internal.ktx

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.drawable.Drawable
import android.os.Build
import android.telephony.TelephonyManager

internal fun Context.getColorCompat(id: Int)
        : Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    getColor(id)
} else {
    @Suppress("DEPRECATION")
    resources.getColor(id)
}

internal fun Context.getDrawableCompat(id: Int)
        : Drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    getDrawable(id)!!
} else {
    @Suppress("DEPRECATION")
    resources.getDrawable(id)
}

tailrec fun Context?.activity(): Activity? = when (this) {
    is Activity -> this
    else -> (this as? ContextWrapper)?.baseContext?.activity()
}

internal val Context.telephonyManager: TelephonyManager
    get() {
        return getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }
