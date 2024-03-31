package com.nqmgaming.lab6_minhnqph31902.viewmodel

import androidx.lifecycle.ViewModel
import com.nqmgaming.lab6_minhnqph31902.repository.Repository

class DistributorViewModel(private val repository: Repository):ViewModel(){
    suspend fun getDistributors(token: String) = repository.getDistributors(token)
    suspend fun deleteDistributor(token: String, distributorId: String) = repository.deleteDistributor(token, distributorId)
    suspend fun searchDistributor(token: String, name: String) = repository.searchDistributor(token, name)
    suspend fun addDistributor(token: String, name: String) = repository.addDistributor(token, name)
    suspend fun updateDistributor(token: String, distributorId: String, name: String) = repository.updateDistributor(token, distributorId, name)
}