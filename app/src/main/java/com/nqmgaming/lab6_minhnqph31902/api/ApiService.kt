package com.nqmgaming.lab6_minhnqph31902.api

import com.nqmgaming.lab6_minhnqph31902.model.Distributor
import com.nqmgaming.lab6_minhnqph31902.model.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    //Login
    @FormUrlEncoded
    @POST("users/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<User>

    // Get all distributors use bearer token
    @GET("distributors")
    suspend fun getDistributors(@Header("Authorization") token: String): Response<List<Distributor>>
    @DELETE("distributors/{distributorId}")
    suspend fun deleteDistributor(@Header("Authorization") token: String, @Path("distributorId") distributorId: String): Response<Distributor>

    @GET("distributors/search")
    suspend fun searchDistributor(@Header("Authorization") token: String, @Query("name") name: String): Response<List<Distributor>>

    @POST("distributors/create")
    @FormUrlEncoded
    suspend fun addDistributor(
        @Header("Authorization") token: String,
        @Field("name") name: String,
    ): Response<Distributor>

    @PATCH("distributors/{distributorId}")
    @FormUrlEncoded
    suspend fun updateDistributor(
        @Header("Authorization") token: String,
        @Path("distributorId") distributorId: String,
        @Field("name") name: String,
    ): Response<Distributor>
}