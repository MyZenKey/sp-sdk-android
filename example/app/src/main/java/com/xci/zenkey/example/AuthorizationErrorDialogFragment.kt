/**
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
package com.xci.zenkey.example

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

/**
 * A simple [DialogFragment] to display an error message to the user.
 */
class AuthorizationErrorDialogFragment: DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setMessage(arguments!!.getString(ERROR_MESSAGE))
                .setIcon(R.drawable.ic_zenkey_white)
                .setTitle(getString(R.string.zenkey_authorization_error))
                .setPositiveButton(getString(R.string.ok)
                ) { _, _ -> dismiss() }
                .create()
    }

    companion object {
        //The key used in the argument [Bundle] for the error message.
        private const val ERROR_MESSAGE = "ERROR_MESSAGE"
        //The Tag used to display this fragment with the [FragmentManager]
        private const val TAG = "AuthorizationErrorDialogFragment"
        /**
         * Show an error dialog fragment.
         * @param fragmentManager the [FragmentManager] used to display this alert dialog fragment.
         * @param message the error message to display
         */
        fun show(fragmentManager: FragmentManager, message: String) {
            AuthorizationErrorDialogFragment()
                    .apply {
                        arguments = Bundle().apply {
                            putString(ERROR_MESSAGE, message)
                        }
                    }.show(fragmentManager, TAG)
        }
    }
}