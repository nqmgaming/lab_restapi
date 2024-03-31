package com.nqmgaming.lab6_minhnqph31902.repository

import com.nqmgaming.lab6_minhnqph31902.api.ApiServiceBuilder
import com.nqmgaming.lab6_minhnqph31902.model.Distributor
import com.nqmgaming.lab6_minhnqph31902.model.User
import retrofit2.Response

class Repository {
    suspend fun login(username: String, password: String) : Response<User> {
        return ApiServiceBuilder.api.login(username, password)
    }

    suspend fun getDistributors(token: String): Response<List<Distributor>> {
        return ApiServiceBuilder.api.getDistributors(token)
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

    suspend fun updateDistributor(token: String, distributorId: String, name: String): Response<Distributor> {
        return ApiServiceBuilder.api.updateDistributor(token, distributorId, name)
    }
}