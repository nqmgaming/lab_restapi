package com.nqmgaming.lab6_minhnqph31902.viewmodel

import androidx.lifecycle.ViewModel
import com.nqmgaming.lab6_minhnqph31902.repository.Repository
import java.io.File

class EditFruitViewModel(private val repository: Repository) : ViewModel() {
    //update fruit
    suspend fun updateFruit(
        token: String,
        fruitId: String,
        name: String,
        quantity: Int,
        price: Double,
        status: Int,
        description: String,
        distributor: String,
        imageList: List<File>
    ) =
        repository.updateFruit(
            token,
            fruitId,
            name,
            quantity,
            price,
            status,
            description,
            distributor,
            imageList
        )

    //get fruit
    suspend fun getFruit(token: String, fruitId: String) = repository.getFruit(token, fruitId)
}