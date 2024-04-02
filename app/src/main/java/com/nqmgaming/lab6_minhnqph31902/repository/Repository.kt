package com.nqmgaming.lab6_minhnqph31902.repository

import android.util.Log
import com.nqmgaming.lab6_minhnqph31902.api.ApiServiceBuilder
import com.nqmgaming.lab6_minhnqph31902.model.Distributor
import com.nqmgaming.lab6_minhnqph31902.model.Fruit
import com.nqmgaming.lab6_minhnqph31902.model.FruitResponse
import com.nqmgaming.lab6_minhnqph31902.model.User
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

class Repository {
    suspend fun login(username: String, password: String): Response<User> {
        return ApiServiceBuilder.api.login(username, password)
    }

    suspend fun register(
        username: String,
        password: String,
        email: String,
        name: String,
        available: Boolean,
        avatarFile: File
    ): Response<User> {
        val usernameRequestBody = RequestBody.create(MediaType.parse("text/plain"), username)
        val passwordRequestBody = RequestBody.create(MediaType.parse("text/plain"), password)
        val emailRequestBody = RequestBody.create(MediaType.parse("text/plain"), email)
        val nameRequestBody = RequestBody.create(MediaType.parse("text/plain"), name)
        val availableRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), available.toString())
        val avatarPart = MultipartBody.Part.createFormData(
            "avatar",
            avatarFile.name,
            RequestBody.create(MediaType.parse("image/*"), avatarFile)
        )
        Log.e("Uploaddddd", avatarFile.toString())
        return ApiServiceBuilder.api.register(
            usernameRequestBody,
            passwordRequestBody,
            emailRequestBody,
            nameRequestBody,
            availableRequestBody,
            avatarPart
        )
    }

    suspend fun getDistributors(token: String): Response<List<Distributor>> {
        return ApiServiceBuilder.api.getDistributors(token)
    }

    suspend fun getDistributor(token: String, distributorId: String): Response<Distributor> {
        return ApiServiceBuilder.api.getDistributor(token, distributorId)
    }

    suspend fun deleteDistributor(token: String, distributorId: String): Response<Distributor> {
        return ApiServiceBuilder.api.deleteDistributor(token, distributorId)
    }

    suspend fun searchDistributor(token: String, name: String): Response<List<Distributor>> {
        return ApiServiceBuilder.api.searchDistributor(token, name)
    }

    suspend fun addDistributor(token: String, name: String): Response<Distributor> {
        return ApiServiceBuilder.api.addDistributor(token, name)
    }

    suspend fun updateDistributor(
        token: String,
        distributorId: String,
        name: String
    ): Response<Distributor> {
        return ApiServiceBuilder.api.updateDistributor(token, distributorId, name)
    }

    suspend fun getFruits(token: String): Response<List<Fruit>> {
        return ApiServiceBuilder.api.getFruits(token)
    }

    suspend fun getFruit(token: String, fruitId: String): Response<Fruit> {
        return ApiServiceBuilder.api.getFruit(token, fruitId)
    }

    suspend fun deleteFruit(token: String, fruitId: String): Response<Fruit> {
        return ApiServiceBuilder.api.deleteFruit(token, fruitId)
    }

    //add fruit
    suspend fun addFruit(
        token: String,
        name: String,
        quantity: Int,
        price: Double,
        status: Int,
        description: String,
        distributorId: String,
        imageList: List<File>
    ): Response<Fruit> {
        val nameRequestBody = RequestBody.create(MediaType.parse("text/plain"), name)
        val quantityRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), quantity.toString())
        val priceRequestBody = RequestBody.create(MediaType.parse("text/plain"), price.toString())
        val statusRequestBody = RequestBody.create(MediaType.parse("text/plain"), status.toString())
        val descriptionRequestBody = RequestBody.create(MediaType.parse("text/plain"), description)
        val distributorIdRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), distributorId)
        val imagePartList = mutableListOf<MultipartBody.Part>()
        imageList.forEach {
            val imagePart = MultipartBody.Part.createFormData(
                "images",
                it.name,
                RequestBody.create(MediaType.parse("image/*"), it)
            )
            imagePartList.add(imagePart)
        }
        return ApiServiceBuilder.api.createFruit(
            token,
            nameRequestBody,
            quantityRequestBody,
            priceRequestBody,
            statusRequestBody,
            descriptionRequestBody,
            distributorIdRequestBody,
            imagePartList
        )
    }

    //update fruit
    suspend fun updateFruit(
        token: String,
        fruitId: String,
        name: String,
        quantity: Int,
        price: Double,
        status: Int,
        description: String,
        distributorId: String,
        imageList: List<File>
    ): Response<FruitResponse> {
        val nameRequestBody = RequestBody.create(MediaType.parse("text/plain"), name)
        val quantityRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), quantity.toString())
        val priceRequestBody = RequestBody.create(MediaType.parse("text/plain"), price.toString())
        val statusRequestBody = RequestBody.create(MediaType.parse("text/plain"), status.toString())
        val descriptionRequestBody = RequestBody.create(MediaType.parse("text/plain"), description)
        val distributorIdRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), distributorId)
        val imagePartList = mutableListOf<MultipartBody.Part>()
        imageList.forEach {
            val imagePart = MultipartBody.Part.createFormData(
                "images",
                it.name,
                RequestBody.create(MediaType.parse("image/*"), it)
            )
            imagePartList.add(imagePart)
        }
        return ApiServiceBuilder.api.updateFruit(
            token,
            fruitId,
            nameRequestBody,
            quantityRequestBody,
            priceRequestBody,
            statusRequestBody,
            descriptionRequestBody,
            distributorIdRequestBody,
            imagePartList
        )
    }
}