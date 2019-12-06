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

package com.xci.zenkey.sdk.internal.ktx

import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.view.View
import android.view.ViewGroup

internal fun View.inflate(
        @LayoutRes layoutRes: Int,
        viewGroup: ViewGroup?
): View = View.inflate(context, layoutRes, viewGroup)

internal fun View.getColor(@ColorRes id: Int)
        : Int = context.getColorCompat(id)

internal fun View.getDrawable(@DrawableRes id: Int)
        : Drawable = context.getDrawableCompat(id)