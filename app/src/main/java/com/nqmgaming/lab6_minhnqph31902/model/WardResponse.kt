package com.nqmgaming.lab6_minhnqph31902.model

import com.google.gson.annotations.SerializedName

data class WardResponse(
    val code: Int,
    val message: String,
    val data: List<WardData>
)

data class WardData(
    @SerializedName("WardCode")
    val wardCode: Int,
    @SerializedName("DistrictID")
    val districtID: Int,
    @SerializedName("WardName")
    val wardName: String
) {
    override fun toString(): String {
        return wardName
    }
}