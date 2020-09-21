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

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.xci.zenkey.sdk.param.Scopes
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.annotation.Config
import java.security.MessageDigest

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class DefaultIdentityProviderTest {

    private var identityProvider: DefaultIdentityProvider? = null
    private val mockMessageDigest = Mockito.mock(MessageDigest::class.java)


    @Before
    fun setUp() {
        identityProvider = DefaultIdentityProvider(PACKAGE_NAME, CLIENT_ID, DEFAULT_REDIRECT_URI, mockMessageDigest)
    }

    @Test
    fun shouldGetAuthorizationIntent() {
        /*Intent authIntent = identityProvider.authorizeIntent().withScopes(SCOPE_1, SCOPE_2).build();

        ComponentName component = authIntent.getComponent();
        assertNotNull(component);
        assertEquals(AuthorizationRequestActivity.class.getName(), authIntent.getComponent().getClassName());

        Bundle extra = authIntent.getExtras();
        assertNotNull(extra);
        AuthorizationRequest request = extra.getParcelable(AuthorizationRequestActivity.KEY_AUTHORIZATION_REQUEST);
        assertNotNull(request);

        assertEquals(CLIENT_ID, request.getRedirectUri().getScheme());
        assertEquals(BuildConfig.APPLICATION_ID, request.getRedirectUri().getAuthority());
        assertEquals(SCOPES, request.getScope());

        assertEquals(AuthorizationRequestActivity.class.getName(), authIntent.getComponent().getClassName());*/
    }

    companion object {

        private const val PACKAGE_NAME = "com.package"
        private const val CLIENT_ID = "client-id"
        private val SCOPE_1 = Scopes.OPEN_ID
        private val SCOPE_2 = Scopes.EMAIL
        private val SCOPES = SCOPE_1.value + " " + SCOPE_2.value

        private val DEFAULT_REDIRECT_URI = Uri.Builder()
                .scheme(CLIENT_ID)
                .authority("com.xci.provider.sdk")
                .build()
    }
}
