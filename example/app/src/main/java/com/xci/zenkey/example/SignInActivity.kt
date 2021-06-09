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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xci.zenkey.example.client.AuthApiClient
import com.xci.zenkey.example.client.extension.body
import com.xci.zenkey.example.client.extension.error
import com.xci.zenkey.example.client.model.ErrorResponse
import com.xci.zenkey.example.client.model.TokenResponse
import com.xci.zenkey.sdk.AuthorizationError
import com.xci.zenkey.sdk.AuthorizationResponse
import com.xci.zenkey.sdk.ZenKey
import com.xci.zenkey.sdk.param.Scopes
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.layout_signin_form.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {

    /**
     * Override [Activity.onCreate] to configure the [zenkeyButton] with our authorization request
     * parameters.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        configureZenKeyButtonRequest()
    }

    /**
     * Configure the ZenKeyButton with the request parameters.
     * - Enable Logs for the ZenKey SDK (optional)
     * - Set any request parameters (Scopes...) (optional)
     * - Set a custom request code to use for `startActivityForResult()` (optional)
     * - Set a click listener to display a loading state.
     */
    private fun configureZenKeyButtonRequest() {
        //Enable the logs if it's a debug-able version of the application.
        ZenKey.logs(BuildConfig.DEBUG)
        // other scopes registered for this client_id through the developer portal should be added here.
        zenkeyButton.setScopes(Scopes.OPEN_ID, Scopes.ADDRESS, Scopes.BIRTH_DATE, Scopes.LAST_4_SOCIAL, Scopes.PROOFING)
        //Set a request code to use for starting the ZenKey activity for result
        //By default the ZenKeyButton will use the following default request code value [ZenKeyButton.DEFAULT_REQUEST_CODE]
        zenkeyButton.setRequestCode(ZENKEY_REQUEST_CODE)

        // In order to provide a better experience, we recommend to show a loading state when the ZenKeyButton is clicked
        zenkeyButton.setOnClickListener {
            showLoading()
        }
    }

    /**
     * Override [Activity.onActivityResult] to receive the authorization result from ZenKey SDK.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ZENKEY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                handleZenKeyResponse(data!!)
            } else {
                hideLoading()
            }
        }
    }

    /**
     * Handle ZenKey SDK [AuthorizationResponse]
     */
    private fun handleZenKeyResponse(data: Intent) {
        //All result from the ZenKey SDK contain an [AuthorizationResponse] in the data bundle.
        val authorizationResponse = AuthorizationResponse.fromIntent(data)!!
        //You should check if this response is successful
        if(authorizationResponse.isSuccessful){
            signInWithZenkey(authorizationResponse)
        } else {
            hideLoading()
            //The error cannot be null if the response is not successful
            showAuthorizationError(authorizationResponse.error!!)
        }
    }

    /**
     * Try exchanging authorization code to a token.
     * @param response the [AuthorizationResponse] received from the ZenKey SDK.
     * We recommended that you send the entire contents of the AuthorizationResponse
     * to your secure backend. An AuthorizationResponse contains the parameters needed for the
     * token request, except for your ZenKey secret. It also contains parameters that you can
     * use to validate the token response.
     *
     * This signInWithZenkey function is only an example of how you might set up your endpoint.
     *
     * In account migration scenarios, where a user of your app has changed from one phone
     * carrier to another, the carrier's token endpoint response will contain one or more
     * `port_token` values for previous carriers associated with this user. Your
     * backend can use that port token to update the user in your database, and return
     * the appropriate user for this sign-in request.
     *
     * However, there are some scenarios in which the backend will be unable to associate it
     * with an existing user and a returning user may appear to be a new user in this sign-in
     * response. (In the code example below, both a new user and a returning user that can't
     * be associated in the backend are represented with "unlinked_user".) For this reason,
     * you should always give what appears to be a new user the opportunity to link to an
     * existing account in your database.
     */
    private fun signInWithZenkey(response: AuthorizationResponse) {
        AuthApiClient.getInstance(
                        AuthApiClient.ApiConfig(
                                BuildConfig.SAMPLE_API_ENDPOINT,
                                BuildConfig.SAMPLE_API_KEY
                        ))
                .signWithZenKey(response.clientId,
                        response.authorizationCode!!,
                        response.redirectUri.toString(),
                        response.mccMnc!!,
                        response.codeVerifier!!,
                        response.correlationId,
                        response.nonce,
                        response.context,
                        response.acrValues)
                .enqueue(object : Callback<TokenResponse> {
                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        hideLoading()
                    }

                    override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                        if(response.isSuccessful){
                            onUserAuthenticated(response.body)
                        } else {
                            if(response.error.error == "unlinked_user") {
                                // Handle account linking
                                // This user has not signed in with ZenKey previously
                                onUnlinkedUser()
                            } else {
                                onZenKeyError(response.error)
                            }
                        }
                        hideLoading()
                    }
                })
    }

    /**
     * The token exchange network call succeed.
     */
    private fun onUserAuthenticated(tokenResponse: TokenResponse) {
        AccountActivity.start(this, tokenResponse.token)
    }

    /**
     * Sample error message
     */
    private fun onZenKeyError(error: ErrorResponse) {
        Toast.makeText(this, error.error, Toast.LENGTH_LONG).show()
    }

    /**
     * Sample unlinked user message
     */
    private fun onUnlinkedUser() {
        Toast.makeText(this, "User has not previously signed in with ZenKey", Toast.LENGTH_LONG).show()
    }

    /**
     * Hide loading UI
     */
    private fun hideLoading(){
        loading.visibility = View.GONE
    }

    /**
     * Show loading UI
     */
    private fun showLoading(){
        loading.visibility = View.VISIBLE
    }

    /**
     * Display error messages to the user depending of the ZenKey [AuthorizationError]
     */
    private fun showAuthorizationError(authorizationError: AuthorizationError) {
        val errorMessage: String = when(authorizationError){
            AuthorizationError.DISCOVERY_STATE -> getString(R.string.error_message_discovery_state)
            AuthorizationError.INVALID_CONFIGURATION -> getString(R.string.error_message_invalid_configuration)
            AuthorizationError.INVALID_REQUEST -> getString(R.string.error_message_invalid_request)
            AuthorizationError.REQUEST_DENIED -> getString(R.string.error_message_request_denied)
            AuthorizationError.REQUEST_TIMEOUT -> getString(R.string.error_message_request_timeout)
            AuthorizationError.SERVER_ERROR -> getString(R.string.error_message_server_error)
            AuthorizationError.NETWORK_FAILURE -> getString(R.string.error_message_network_failure)
            AuthorizationError.UNKNOWN -> getString(R.string.error_message_unknown)
        }
        AuthorizationErrorDialogFragment.show(supportFragmentManager, errorMessage)
    }

    /**
     * A custom request code can be used as needed.
     * The default request code value used by the ZenKey SDK to start the activity for result is
     * [com.xci.zenkey.sdk.widget.ZenKeyButton.DEFAULT_REQUEST_CODE] (1234)
     */
    companion object {
        const val ZENKEY_REQUEST_CODE = 1
    }
}
