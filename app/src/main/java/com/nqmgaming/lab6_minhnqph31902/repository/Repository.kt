package com.nqmgaming.lab6_minhnqph31902.repository

import com.nqmgaming.lab6_minhnqph31902.api.ApiServiceBuilder
import com.nqmgaming.lab6_minhnqph31902.model.Distributor
import com.nqmgaming.lab6_minhnqph31902.model.DistrictResponse
import com.nqmgaming.lab6_minhnqph31902.model.Fruit
import com.nqmgaming.lab6_minhnqph31902.model.FruitResponse
import com.nqmgaming.lab6_minhnqph31902.model.FruitResponseSort
import com.nqmgaming.lab6_minhnqph31902.model.ProvinceResponse
import com.nqmgaming.lab6_minhnqph31902.model.User
import com.nqmgaming.lab6_minhnqph31902.model.WardResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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
        val usernameRequestBody = username.toRequestBody("text/plain".toMediaTypeOrNull())
        val passwordRequestBody = password.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailRequestBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val nameRequestBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val availableRequestBody =
            available.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val avatarPart = MultipartBody.Part.createFormData(
            "avatar",
            avatarFile.name,
            avatarFile.asRequestBody("image/*".toMediaTypeOrNull())
        )
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

    suspend fun getFruitsQuery(
        token: String,
        name: String?,
        price: Int?,
        page: Int?,
        sort: String?
    ): Response<FruitResponseSort> {
        return ApiServiceBuilder.api.getFruitsQuery(token, name, price, page, sort)
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
        val nameRequestBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val quantityRequestBody =
            quantity.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val priceRequestBody = price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val statusRequestBody = status.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionRequestBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val distributorIdRequestBody =
            distributorId.toRequestBody("text/plain".toMediaTypeOrNull())
        val imagePartList = mutableListOf<MultipartBody.Part>()
        imageList.forEach {
            val imagePart = MultipartBody.Part.createFormData(
                "images",
                it.name,
                it.asRequestBody("image/*".toMediaTypeOrNull())
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
        val nameRequestBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val quantityRequestBody =
            quantity.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val priceRequestBody = price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val statusRequestBody = status.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionRequestBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val distributorIdRequestBody =
            distributorId.toRequestBody("text/plain".toMediaTypeOrNull())
        val imagePartList = mutableListOf<MultipartBody.Part>()
        imageList.forEach {
            val imagePart = MultipartBody.Part.createFormData(
                "images",
                it.name,
                it.asRequestBody("image/*".toMediaTypeOrNull())
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

    suspend fun getProvinces(token: String): Response<ProvinceResponse> {
        return ApiServiceBuilder.apiGHN.getProvinces(token)
    }

    suspend fun getDistricts(token: String, provinceId: Int): Response<DistrictResponse> {
        return ApiServiceBuilder.apiGHN.getDistricts(token, provinceId)
    }

    suspend fun getWards(token: String, districtId: Int): Response<WardResponse> {
        return ApiServiceBuilder.apiGHN.getWards(token, districtId)
    }
}