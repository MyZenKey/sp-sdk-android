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

import android.content.Intent
import android.os.Bundle
import com.xci.zenkey.sdk.internal.AuthorizationRequestActivity

internal interface AuthorizationService {

    fun onCreate(activity: AuthorizationRequestActivity, intent: Intent, savedInstanceState: Bundle?)

    fun onResume(activity: AuthorizationRequestActivity, intent: Intent)

    fun onSaveInstanceState(outState: Bundle)

    fun onNewIntent(activity: AuthorizationRequestActivity, intent: Intent)

    fun onDestroy(activity: AuthorizationRequestActivity)
}
