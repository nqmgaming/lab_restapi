package com.nqmgaming.lab6_minhnqph31902.model

data class Fruit(
    val id: String,
    val quantity: Int,
    val price: Double,
    val status: Int,
    val image: String,
    val description: String,
    val distributor: Distributor,
    val createdAt: String,
    val updatedAt: String
)
