package com.nqmgaming.lab6_minhnqph31902.viewmodel

import androidx.lifecycle.ViewModel
import com.nqmgaming.lab6_minhnqph31902.repository.Repository

class AddressViewModel(val repository: Repository) :ViewModel(){

    suspend fun getProvinces(token: String) = repository.getProvinces(token)

    suspend fun getDistricts(token: String, provinceId: Int) = repository.getDistricts(token, provinceId)

    suspend fun getWards(token: String, districtId: Int) = repository.getWards(token, districtId)

}