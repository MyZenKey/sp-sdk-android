package com.xci.zenkey.sdk.internal.ktx

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@RunWith(AndroidJUnit4::class)
class ThrowableTest {
    @Test
    fun socketTimeoutExceptionShouldBeANetworkError() {
        Assert.assertTrue(SocketTimeoutException().isNetworkFailure)
    }

    @Test
    fun unknownHostExceptionShouldBeANetworkError() {
        Assert.assertTrue(UnknownHostException().isNetworkFailure)
    }

    @Test
    fun anyOtherExceptionShouldNotBeANetworkError() {
        Assert.assertFalse(Exception().isNetworkFailure)
    }
}
