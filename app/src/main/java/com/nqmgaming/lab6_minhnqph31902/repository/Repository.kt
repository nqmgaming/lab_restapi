package com.nqmgaming.lab6_minhnqph31902.repository

import com.nqmgaming.lab6_minhnqph31902.api.ApiServiceBuilder
import com.nqmgaming.lab6_minhnqph31902.model.User
import retrofit2.Response

class Repository {
    suspend fun login(username: String, password: String) : Response<User> {
        return ApiServiceBuilder.api.login(username, password)
    }
}