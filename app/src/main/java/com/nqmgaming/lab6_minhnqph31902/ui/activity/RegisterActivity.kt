package com.nqmgaming.lab6_minhnqph31902.ui.activity

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nqmgaming.lab6_minhnqph31902.R
import com.nqmgaming.lab6_minhnqph31902.databinding.ActivityRegisterBinding
import com.nqmgaming.lab6_minhnqph31902.repository.Repository
import com.nqmgaming.lab6_minhnqph31902.viewmodel.RegisterViewModel
import com.nqmgaming.lab6_minhnqph31902.viewmodel.RegisterViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileOutputStream

class RegisterActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: ActivityRegisterBinding
    private var imageUri: Uri? = null
    private lateinit var viewModel: RegisterViewModel
    companion object {
        private const val REQUEST_CODE_CAMERA = 1001
        private const val REQUEST_CODE_READ_MEDIA_IMAGES = 1002
    }
    private var imageSource: ImageSource = ImageSource.GALLERY
    enum class ImageSource {
        CAMERA,
        GALLERY
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
        binding.imageView.setImageURI(uri)
        imageSource = ImageSource.GALLERY
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        binding.imageView.setImageBitmap(bitmap)
        imageUri = bitmap?.let { saveBitmapToFile(it) }
        imageSource = ImageSource.CAMERA
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView.setOnClickListener {
            checkPermissionAndOpenCameraOrGallery()
        }
        binding.registerBtn.setOnClickListener {
            checkPermissionAndRegisterUser()
        }
    }

    private fun checkPermissionAndOpenCameraOrGallery() {
        val perms = arrayOf(Manifest.permission.CAMERA)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            openCameraOrGallery()
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.camera_rationale), REQUEST_CODE_CAMERA, *perms)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissionAndRegisterUser() {
        val perms = arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            registerUser()
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.storage_rationale), REQUEST_CODE_READ_MEDIA_IMAGES, *perms)
        }
    }

    private fun openCameraOrGallery() {
        val items = arrayOf("Camera", "Gallery")
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Choose an action")
        builder.setItems(items) { _, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun openGallery() {
        getContent.launch("image/*")
    }

    private fun openCamera() {
        takePicture.launch(null)
    }


    private fun saveBitmapToFile(bitmap: Bitmap): Uri {
        val file = File(externalCacheDir, "${System.currentTimeMillis()}.jpg")
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()
        return Uri.fromFile(file)
    }

    private fun registerUser() {
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val avatarFile = when (imageSource) {
            ImageSource.CAMERA -> File(imageUri!!.path!!)
            ImageSource.GALLERY -> uriToFile(imageUri!!)
        }
        Log.d("RegisterActivity", "Avatar file: $avatarFile")
        val repository = Repository()
        val viewModelFactory = RegisterViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[RegisterViewModel::class.java]

        CoroutineScope(Dispatchers.IO).launch {
            val response = viewModel.register(
                binding.usernameEt.text.toString().trim(),
                binding.passwordEt.text.toString().trim(),
                binding.emailEt.text.toString().trim(),
                binding.nameEt.text.toString().trim(),
                true,
                avatarFile
            )
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@RegisterActivity,
                    if (response.isSuccessful) "Registered successfully" else "Registration failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            it.moveToFirst()
            val index = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            val path = it.getString(index)
            return File(path)
        }
        throw IllegalArgumentException("Uri not found")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        when (requestCode) {
            REQUEST_CODE_CAMERA -> openCameraOrGallery()
            REQUEST_CODE_READ_MEDIA_IMAGES -> registerUser()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }
}