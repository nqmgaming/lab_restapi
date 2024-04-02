package com.nqmgaming.lab6_minhnqph31902.ui.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.gun0912.tedpermission.coroutine.TedPermission
import com.nqmgaming.lab6_minhnqph31902.MainActivity
import com.nqmgaming.lab6_minhnqph31902.databinding.ActivityRegisterBinding
import com.nqmgaming.lab6_minhnqph31902.repository.Repository
import com.nqmgaming.lab6_minhnqph31902.utils.ImageUtils
import com.nqmgaming.lab6_minhnqph31902.utils.RealPathUtil
import com.nqmgaming.lab6_minhnqph31902.utils.SharedPrefUtils
import com.nqmgaming.lab6_minhnqph31902.viewmodel.RegisterViewModel
import com.nqmgaming.lab6_minhnqph31902.viewmodel.RegisterViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var imageUri: Uri? = null
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (SharedPrefUtils.getBoolean(this, "isLoggedIn")) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.imageView.setOnClickListener {
            GlobalScope.launch {
                checkPermissionAndOpenCameraOrGallery()
            }
        }
        binding.registerBtn.setOnClickListener {
            registerUser()
        }
        binding.loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    private suspend fun checkPermissionAndOpenCameraOrGallery() {
        val permissionsResult = TedPermission.create()
            .setPermissions(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_MEDIA_IMAGES
            )
            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
            .check()

        if (permissionsResult.isGranted) {
            withContext(Dispatchers.Main) {
                openCameraOrGallery()
            }
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }

    }

    private fun openCameraOrGallery() {
        ImagePicker.with(this)
            .crop()
            .cropSquare()
            .galleryMimeTypes(arrayOf("image/png", "image/jpeg"))
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                getResult.launch(intent)

            }
    }


    private fun registerUser() {
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }
        binding.progressBar.visibility = ProgressBar.VISIBLE

        val realPath = RealPathUtil.getRealPath(this, imageUri!!)
        val avatarFile = realPath?.let { File(it) }
        Log.d("RegisterActivity", "Avatar file: $avatarFile")
        val repository = Repository()
        val viewModelFactory = RegisterViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[RegisterViewModel::class.java]

        CoroutineScope(Dispatchers.IO).launch {
            val response = avatarFile?.let {
                viewModel.register(
                    binding.usernameEt.text.toString().trim(),
                    binding.passwordEt.text.toString().trim(),
                    binding.emailEt.text.toString().trim(),
                    binding.nameEt.text.toString().trim(),
                    true,
                    it
                )
            }
            withContext(Dispatchers.Main) {
                if (response != null) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Register success",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.progressBar.visibility = ProgressBar.GONE
                        Intent(this@RegisterActivity, LoginActivity::class.java).also {
                            startActivity(it)
                            finish()
                        }
                    } else {
                        if (response.code() == 409) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Username or email already exists",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Register failed",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
                }


            }
        }
    }

    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show()
                val uri = result.data?.data
                if (uri != null) {
                    imageUri = uri
                    Log.d("RegisterActivity", "Image uri: $uri")
                    Glide.with(this).load(uri).into(binding.imageView)
                } else {
                    Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show()
                }
            }
        }

}
