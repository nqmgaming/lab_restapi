package com.nqmgaming.lab6_minhnqph31902.model

import com.google.gson.annotations.SerializedName

data class User (
    @SerializedName("_id")
    val id: String,
    val username: String,
    val password: String,
    val email: String,
    val name: String,
    val avatar: String,
    val available: Boolean
)