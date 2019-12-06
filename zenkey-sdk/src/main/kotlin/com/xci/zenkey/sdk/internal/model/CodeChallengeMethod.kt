package com.xci.zenkey.sdk.internal.model

import java.lang.Exception

internal enum class CodeChallengeMethod(
        val value: String
) {
    SHA_256("S256"), PLAIN("plain");


    internal companion object {

        fun fromValue(v: String): CodeChallengeMethod {
            for (value in values()){
                if(value.value == v)
                    return value
            }
            throw Exception("invalid CodeChallengeMethod key")
        }
    }
}