package com.nqmgaming.lab6_minhnqph31902.viewmodel

import androidx.lifecycle.ViewModel
import com.nqmgaming.lab6_minhnqph31902.repository.Repository

class FruitViewModel(
    private val repository: Repository
):ViewModel() {

    suspend fun getFruits(token: String) = repository.getFruits(token)

    suspend fun getFruitsQuery(
        token: String,
        name: String?,
        price: Int?,
        page: Int?,
        sort: String?
    ) = repository.getFruitsQuery(token, name, price, page, sort)

    suspend fun deleteFruit(token: String, fruitId: String) = repository.deleteFruit(token, fruitId)

}