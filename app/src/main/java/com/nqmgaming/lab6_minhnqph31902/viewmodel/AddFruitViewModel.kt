package com.nqmgaming.lab6_minhnqph31902.viewmodel

import androidx.lifecycle.ViewModel
import com.nqmgaming.lab6_minhnqph31902.model.Fruit
import com.nqmgaming.lab6_minhnqph31902.repository.Repository
import retrofit2.Response
import java.io.File

class AddFruitViewModel(private val repository: Repository) : ViewModel() {

    suspend fun addFruit(
        token: String,
        name: String,
        quantity: Int,
        price: Double,
        status: Int,
        description: String,
        distributorId: String,
        imageFiles: List<File>
    ): Response<Fruit> {
        return repository.addFruit(token, name, quantity, price, status, description, distributorId, imageFiles)
    }

}