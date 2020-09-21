package com.xci.zenkey.example.client.model

import com.squareup.moshi.Json

data class AddressResponse (
    @Json(name = "formatted")
    val formatted: String? = null,
    @Json(name = "street_address")
    val streetAddress: String? = null,
    @Json(name = "locality")
    val locality: String? = null,
    @Json(name = "region")
    val region: String? = null,
    @Json(name = "postalCode")
    val postalCode: String? = null,
    @Json(name = "country")
    val country: String? = null
)