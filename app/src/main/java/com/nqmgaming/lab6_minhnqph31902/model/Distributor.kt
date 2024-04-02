package com.nqmgaming.lab6_minhnqph31902.model

import com.google.gson.annotations.SerializedName

data class Distributor(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val createdAt: String,
    val updatedAt: String
){
    override fun toString(): String {
        return name
    }
}
