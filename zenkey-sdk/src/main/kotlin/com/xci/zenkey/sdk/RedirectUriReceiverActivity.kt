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
package com.xci.zenkey.sdk

import android.app.Activity
import android.os.Bundle
import com.xci.zenkey.sdk.internal.AuthorizationRequestActivity
import com.xci.zenkey.sdk.internal.contract.Logger

/**
 * This [Activity] is catching the redirect for the ZenKey SDK.
 * This [Activity] have the responsibility to re-start [AuthorizationRequestActivity] and clear the activity stack.
 */
class RedirectUriReceiverActivity : Activity() {

    public override fun onCreate(savedInstanceBundle: Bundle?) {
        super.onCreate(savedInstanceBundle)
        // while this does not appear to be achieving much, handling the redirect in this way
        // ensures that we can remove the browser tab/CCID from the back stack. See the documentation
        // on AuthorizationManagementActivity for more details.
        val redirect = intent.data
        if (redirect != null) {
            Logger.get().redirect(redirect)
            startActivity(AuthorizationRequestActivity.createResponseHandlingIntent(
                    this, intent.data!!))
        } else {
            Logger.get().e("RedirectUriReceiverActivity started without redirectUri")
        }
        finish()
    }
}
