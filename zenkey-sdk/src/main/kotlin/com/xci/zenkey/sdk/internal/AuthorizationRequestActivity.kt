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

import android.app.Activity
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.xci.zenkey.sdk.internal.model.AuthorizationRequest

class AuthorizationRequestActivity
    : Activity() {

    internal var authorizationService = DefaultContentProvider.authorizationService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authorizationService.onCreate(this, intent, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        authorizationService.onResume(this, intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        authorizationService.onSaveInstanceState(outState)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        authorizationService.onNewIntent(this, intent)
    }

    override fun onDestroy() {
        authorizationService.onDestroy(this)
        super.onDestroy()
    }

    internal fun startDiscoverUi(
            intentToStart: Intent,
            onActivityNotFound: () -> Unit
    ){
        startAuthorizationFlowActivity(intentToStart, onActivityNotFound,
                { intent, activity ->  activity.startActivity(intent) })
    }

    internal fun startAuthorize(
            intentToStart: Intent,
            onActivityNotFound: () -> Unit
    ){
        startAuthorizationFlowActivity(intentToStart, onActivityNotFound,
                { intent, activity ->  activity.startActivityForResult(intent, 0) })
    }

    private fun startAuthorizationFlowActivity(intentToStart: Intent,
                                                         onActivityNotFound: () -> Unit,
                                                         startActivity: (Intent, Activity) -> Unit){
        intent = intent.setData(null)
        try {
            startActivity.invoke(intentToStart, this)
        } catch (e: ActivityNotFoundException) {
            onActivityNotFound.invoke()
        }
    }

    companion object IntentFactory {

        fun createResponseHandlingIntent(context: Context, data: Uri): Intent {
            val intent = Intent(context, AuthorizationRequestActivity::class.java)
            intent.data = data
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            return intent
        }

        internal fun createStartForResultIntent(
                packageName: String,
                request: AuthorizationRequest,
                successIntent: PendingIntent?,
                failureIntent: PendingIntent?,
                completionIntent: PendingIntent?,
                cancellationIntent: PendingIntent?
        ): Intent {
            val intent = Intent()
            intent.component = ComponentName(packageName, AuthorizationRequestActivity::class.java.name)
            intent.putExtra(DefaultAuthorizationService.EXTRA_KEY_REQUEST, request)
            if (successIntent != null) {
                intent.putExtra(DefaultAuthorizationService.EXTRA_KEY_SUCCESS_INTENT, successIntent)
            }
            if (failureIntent != null) {
                intent.putExtra(DefaultAuthorizationService.EXTRA_KEY_FAILURE_INTENT, failureIntent)
            }
            if (completionIntent != null) {
                intent.putExtra(DefaultAuthorizationService.EXTRA_KEY_COMPLETION_INTENT, completionIntent)
            }
            if (cancellationIntent != null) {
                intent.putExtra(DefaultAuthorizationService.EXTRA_KEY_CANCELLATION_INTENT, cancellationIntent)
            }
            return intent
        }
    }
}
