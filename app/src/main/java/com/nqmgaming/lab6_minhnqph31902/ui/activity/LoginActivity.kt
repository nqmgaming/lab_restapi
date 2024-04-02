package com.nqmgaming.lab6_minhnqph31902.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.nqmgaming.lab6_minhnqph31902.viewmodel.LoginViewModelFactory
import com.nqmgaming.lab6_minhnqph31902.MainActivity
import com.nqmgaming.lab6_minhnqph31902.R
import com.nqmgaming.lab6_minhnqph31902.databinding.ActivityLoginBinding
import com.nqmgaming.lab6_minhnqph31902.repository.Repository
import com.nqmgaming.lab6_minhnqph31902.utils.SharedPrefUtils
import com.nqmgaming.lab6_minhnqph31902.viewmodel.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (SharedPrefUtils.getBoolean(this, "isLoggedIn")) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        enableEdgeToEdge()
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val repository = Repository()
        val viewModelFactory = LoginViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

        binding.login.setOnClickListener {
            val username = binding.username.text.toString().trim()
            val password = binding.password.text.toString().trim()
            Log.d("LoginActivity", "Username: $username, Password: $password")

            CoroutineScope(Dispatchers.IO).launch {
                val response = viewModel.login(username, password)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {

                        //save token
                        val token = response.body()?.token
                        Log.d("LoginActivity", "Token: $token")
                        val id = response.body()?.id
                        Log.d("LoginActivity", "ID: $id")
                        SharedPrefUtils.saveString(this@LoginActivity, "token", token.toString())
                        SharedPrefUtils.saveString(this@LoginActivity, "id", id.toString())
                        SharedPrefUtils.saveBoolean(this@LoginActivity, "isLoggedIn", true)

                        Intent(this@LoginActivity, MainActivity::class.java).also {
                            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(it)
                        }

                    } else {
                        Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}