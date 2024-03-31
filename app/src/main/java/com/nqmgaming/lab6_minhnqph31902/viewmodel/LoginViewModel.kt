package com.nqmgaming.lab6_minhnqph31902.viewmodel

import androidx.lifecycle.ViewModel
import com.nqmgaming.lab6_minhnqph31902.repository.Repository

class LoginViewModel(private val repository: Repository) : ViewModel() {

    suspend fun login(username: String, password: String) = repository.login(username, password)

}