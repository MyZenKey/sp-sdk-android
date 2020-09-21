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
import androidx.appcompat.app.AppCompatActivity
import com.xci.zenkey.example.client.AccountApiClient
import com.xci.zenkey.example.client.model.UserResponse
import com.xci.zenkey.example.extension.restartApplication
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.layout_home_content.*
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountActivity : AppCompatActivity() {

    /**
     * Override [Activity.onCreate] to configure the UI and to request the UserInfo from the Back-End Sample
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        showLoading()

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(false)
        }

        signout.setOnClickListener {
            restartApplication()
        }

        intent.extras?.getString(EXTRA_ACCESS_TOKEN)?.let { getUserInfo(it) }
    }

    /**
     * Get the user info from the Back-End Sample
     */
    private fun getUserInfo(accessToken: String){
        AccountApiClient.getInstance(
                AccountApiClient.ApiConfig(
                BuildConfig.SAMPLE_API_ENDPOINT,
                BuildConfig.SAMPLE_API_KEY,
                accessToken))
                .userInfo()
                .enqueue(object : Callback<UserResponse>{
                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        onUserInfoFailure(t)
                    }

                    override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                        onUserInfoResponse(response)
                    }
                })
    }

    /**
     * For matter of simplicity, There is no refresh token mechanism.
     * If the userInfo call fails, the token is probably expired, then we just restart the demo application.
     */
    private fun onUserInfoFailure( t: Throwable){
        hideLoading()
        restartApplication()
    }

    /**
     * We received a userInfo response. If the response, is successful, we display the info.
     * If the response isn't successful, we restart the application like for expired token.
     */
    private fun onUserInfoResponse(response: Response<UserResponse>){
        hideLoading()
        if(response.isSuccessful){
            showUserInfo(response.body()!!)
        } else {
            restartApplication()
        }
    }

    /**
     * Display the userInfo for the UI.
     */
    private fun showUserInfo(response: UserResponse){
        nameTextView.text = response.name?.value  ?: response.username
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
     * Don't allow to come back to the SignInActivity using back-press, but put the application in background instead.
     */
    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    /**
     * Companion object to provide convenient start method for this activity.
     */
    companion object {
        fun start(
                activity: Activity,
                accessToken: String
        ){
            activity.startActivity(Intent(activity, AccountActivity::class.java)
                    .apply { putExtra(EXTRA_ACCESS_TOKEN, accessToken) })
        }

        private const val EXTRA_ACCESS_TOKEN = "EXTRA_ACCESS_TOKEN"
    }
}