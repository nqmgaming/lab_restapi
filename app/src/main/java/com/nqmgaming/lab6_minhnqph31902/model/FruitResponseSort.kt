package com.nqmgaming.lab6_minhnqph31902.model

import com.google.gson.annotations.SerializedName

data class FruitResponseSort(
    val fruits: List<Fruit>,
    val page: Int,
    val pages: Int,
    val perPage: Int,
    val count: Int
)



