package com.nqmgaming.lab6_minhnqph31902.viewmodel

import androidx.lifecycle.ViewModel
import com.nqmgaming.lab6_minhnqph31902.repository.Repository
import java.io.File

class RegisterViewModel(
    private val repository: Repository
) : ViewModel() {
    suspend fun register(
        username: String,
        password: String,
        email: String,
        name: String,
        available: Boolean,
        avatarFile: File
    ) = repository.register(username, password, email, name, available, avatarFile)
}