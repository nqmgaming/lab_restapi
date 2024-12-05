package com.nqmgaming.lab6_minhnqph31902.model

import com.google.gson.annotations.SerializedName

data class DistrictResponse(
    val code: Int,
    val message: String,
    val data: List<DistrictData>
)

data class DistrictData(
    @SerializedName("DistrictID")
    val districtID: Int,
    @SerializedName("ProvinceID")
    val provinceID: Int,
    @SerializedName("DistrictName")
    val districtName: String,
    @SerializedName("Code")
    val code: String,
    @SerializedName("Type")
    val type: Int,
    @SerializedName("SupportType")
    val supportType: Int
) {
    override fun toString(): String {
        return districtName
    }
}