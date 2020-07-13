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

import com.xci.zenkey.example.client.api.AuthApi
import com.xci.zenkey.example.client.extension.converterFactory
import com.xci.zenkey.example.client.extension.loggingInterceptor
import com.xci.zenkey.example.client.model.SignInRequest
import com.xci.zenkey.example.client.util.SingletonHolder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit

class AuthApiClient private constructor(
        private val apiConfig: ApiConfig
){

    private val tokenInterceptor: Interceptor by lazy {
        Interceptor { chain ->
            val original: Request = chain.request()
            val request: Request = original.newBuilder()
                    .header("X-API-KEY", apiConfig.key)
                    .method(original.method(), original.body())
                    .build()
            chain.proceed(request)
        }
    }

    private val authApi: AuthApi by lazy {
        Retrofit.Builder()
                .baseUrl(apiConfig.endpoint)
                .addConverterFactory(converterFactory)
                .client(OkHttpClient.Builder()
                        .addInterceptor(loggingInterceptor)
                        .addInterceptor(tokenInterceptor)
                        .build())
                .build()
                .create(AuthApi::class.java)
    }

    fun createUser(
            username: String,
            password: String,
            zenkeySub: String,
            name: String,
            phoneNumber: String,
            postalCode: String,
            email: String
    ) = authApi.createUser(
            username,
            password,
            zenkeySub,
            name,
            phoneNumber,
            postalCode,
            email)

    fun endSession() = authApi.endSession()

    fun refreshToken(
            grantType: String,
            refreshToken: String
    ) = authApi.refreshToken(grantType, refreshToken)

    fun signWithZenKey(
            clientId: String,
            code: String,
            redirectUri: String,
            mccmnc: String,
            codeVerifier: String,
            correlationId: String? = null,
            nonce: String? = null,
            context: String? = null,
            acrValues: String? = null
    ) = authApi.signIn(
            SignInRequest(
                    clientId,
                    code,
                    redirectUri,
                    mccmnc, codeVerifier,
                    correlationId,
                    nonce,
                    context,
                    acrValues))

    companion object : SingletonHolder<AuthApiClient, ApiConfig>(::AuthApiClient)

    data class ApiConfig(val endpoint: String, val key: String)
}