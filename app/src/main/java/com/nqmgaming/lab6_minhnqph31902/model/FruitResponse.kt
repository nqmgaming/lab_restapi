package com.nqmgaming.lab6_minhnqph31902.model


import com.google.gson.annotations.SerializedName

data class FruitResponse(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val quantity: Int,
    val price: Double,
    val status: Int,
    val image: List<Images>,
    val description: String,
    val distributor: String,
    val createdAt: String,
    val updatedAt: String
)
