package com.xci.zenkey.example.client.model

import com.squareup.moshi.Json

open class IALResponse<T> (
    @Json(name = "value")
    val value: T? = null,
    @Json(name = "verified")
    val verified: Int? = null
)