package com.nqmgaming.lab6_minhnqph31902.model

import com.google.gson.annotations.SerializedName

data class ProvinceResponse(
    val code: Int,
    val message: String,
    val data: List<ProvinceData>
)

data class ProvinceData(
    @SerializedName("ProvinceID")
    val provinceID: Int,
    @SerializedName("ProvinceName")
    val provinceName: String,
    @SerializedName("Code")
    val code: String
){
    override fun toString(): String {
        return provinceName
    }
}