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
package com.xci.zenkey.example.client

import com.xci.zenkey.example.client.api.AccountApi
import com.xci.zenkey.example.client.extension.converterFactory
import com.xci.zenkey.example.client.extension.loggingInterceptor
import com.xci.zenkey.example.client.model.UserResponse
import com.xci.zenkey.example.client.util.SingletonHolder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Retrofit

class AccountApiClient private constructor(
        private val apiConfig: ApiConfig
) {

    private val tokenInterceptor: Interceptor by lazy {
        Interceptor { chain ->
            val original: Request = chain.request()
            val request: Request = original.newBuilder()
                    .header("X-API-KEY", apiConfig.key)
                    .header("Authorization", "Bearer " + apiConfig.accessToken)
                    .method(original.method(), original.body())
                    .build()
            chain.proceed(request)
        }
    }

    private val accountApi: AccountApi by lazy {
        Retrofit.Builder()
                .baseUrl(apiConfig.endpoint)
                .addConverterFactory(converterFactory)
                .client(OkHttpClient.Builder()
                        .addInterceptor(loggingInterceptor)
                        .addInterceptor(tokenInterceptor)
                        .build())
                .build()
                .create(AccountApi::class.java)
    }

    fun userInfo(): Call<UserResponse> = accountApi.userinfo()

    companion object : SingletonHolder<AccountApiClient, ApiConfig>(::AccountApiClient)

    data class ApiConfig(val endpoint: String, val key: String, val accessToken: String)
}