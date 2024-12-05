package com.nqmgaming.lab6_minhnqph31902.api

import com.nqmgaming.lab6_minhnqph31902.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiServiceBuilder {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val retrofitGHN by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.API_GHN)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val apiGHN: ApiService by lazy {
        retrofitGHN.create(ApiService::class.java)
    }

}