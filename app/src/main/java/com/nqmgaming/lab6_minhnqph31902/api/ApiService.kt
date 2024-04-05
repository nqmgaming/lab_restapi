package com.nqmgaming.lab6_minhnqph31902.api

import com.nqmgaming.lab6_minhnqph31902.model.Distributor
import com.nqmgaming.lab6_minhnqph31902.model.DistrictResponse
import com.nqmgaming.lab6_minhnqph31902.model.Fruit
import com.nqmgaming.lab6_minhnqph31902.model.FruitResponse
import com.nqmgaming.lab6_minhnqph31902.model.FruitResponseSort
import com.nqmgaming.lab6_minhnqph31902.model.ProvinceResponse
import com.nqmgaming.lab6_minhnqph31902.model.User
import com.nqmgaming.lab6_minhnqph31902.model.WardResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
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

    // Register
    @Multipart
    @POST("users/register")
    suspend fun register(
        @Part("username") username: RequestBody,
        @Part("password") password: RequestBody,
        @Part("email") email: RequestBody,
        @Part("name") name: RequestBody,
        @Part("available") available: RequestBody,
        @Part avatar: MultipartBody.Part
    ): Response<User>

    // Get all distributors use bearer token
    @GET("distributors")
    suspend fun getDistributors(@Header("Authorization") token: String): Response<List<Distributor>>

    @GET("distributors/{distributorId}")
    suspend fun getDistributor(
        @Header("Authorization") token: String,
        @Path("distributorId") distributorId: String
    ): Response<Distributor>

    @DELETE("distributors/{distributorId}")
    suspend fun deleteDistributor(
        @Header("Authorization") token: String,
        @Path("distributorId") distributorId: String
    ): Response<Distributor>

    @GET("distributors/search")
    suspend fun searchDistributor(
        @Header("Authorization") token: String,
        @Query("name") name: String
    ): Response<List<Distributor>>

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

    //get list of fruits
    @GET("fruits")
    suspend fun getFruits(@Header("Authorization") token: String): Response<List<Fruit>>

    @GET("fruits/query")
    suspend fun getFruitsQuery(
        @Header("Authorization") token: String,
        @Query("name") name: String?,
        @Query("price") price: Int?,
        @Query("page") page: Int?,
        @Query("sort") sort: String?
    ): Response<FruitResponseSort>

    @GET("fruits/{fruitId}")
    suspend fun getFruit(
        @Header("Authorization") token: String,
        @Path("fruitId") fruitId: String
    ): Response<Fruit>

    @DELETE("fruits/{fruitId}")
    suspend fun deleteFruit(
        @Header("Authorization") token: String,
        @Path("fruitId") fruitId: String
    ): Response<Fruit>

    @Multipart
    @POST("fruits/create")
    suspend fun createFruit(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part("price") price: RequestBody,
        @Part("status") status: RequestBody,
        @Part("description") description: RequestBody,
        @Part("distributor") distributor: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Response<Fruit>

    @Multipart
    @PATCH("fruits/{fruitId}")
    suspend fun updateFruit(
        @Header("Authorization") token: String,
        @Path("fruitId") fruitId: String,
        @Part("name") name: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part("price") price: RequestBody,
        @Part("status") status: RequestBody,
        @Part("description") description: RequestBody,
        @Part("distributor") distributor: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Response<FruitResponse>


    @GET("province")
    suspend fun getProvinces(@Header("token") token: String): Response<ProvinceResponse>

    @GET("district")
    suspend fun getDistricts(@Header("Token") token: String, @Query("province_id") provinceId: Int): Response<DistrictResponse>

    @GET("ward")
    suspend fun getWards(@Header("Token") token: String, @Query("district_id") districtId: Int): Response<WardResponse>
}