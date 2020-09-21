package com.xci.zenkey.example.client.model

import com.squareup.moshi.Json

class NameResponse: IALResponse<String>() {

    @Json(name = "given_name")
    var givenName: String? = null

    @Json(name = "family_name")
    var familyName: String? = null

}