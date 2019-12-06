package com.xci.zenkey.sdk.internal.model

internal data class Branding(val carrierText: String,
                             val carrierLogo: String?){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Branding

        if (carrierText != other.carrierText) return false
        if (carrierLogo != other.carrierLogo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = carrierText.hashCode()
        result = 31 * result + (carrierLogo?.hashCode() ?: 0)
        return result
    }
}