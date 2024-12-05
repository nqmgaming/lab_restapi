package com.nqmgaming.lab6_minhnqph31902.model

data class FruitResponseSort(
    val fruits: List<Fruit>,
    val page: Int,
    val pages: Int,
    val perPage: Int,
    val count: Int
)



