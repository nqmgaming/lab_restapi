package com.nqmgaming.lab6_minhnqph31902.ui.fragment.fruit

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.nqmgaming.lab6_minhnqph31902.R
import com.nqmgaming.lab6_minhnqph31902.adapter.ImageFruitAdapter
import com.nqmgaming.lab6_minhnqph31902.databinding.FragmentAddFruitBinding
import com.nqmgaming.lab6_minhnqph31902.model.Distributor
import com.nqmgaming.lab6_minhnqph31902.repository.Repository
import com.nqmgaming.lab6_minhnqph31902.utils.RealPathUtil
import com.nqmgaming.lab6_minhnqph31902.utils.SharedPrefUtils
import com.nqmgaming.lab6_minhnqph31902.viewmodel.AddFruitViewModel
import com.nqmgaming.lab6_minhnqph31902.viewmodel.AddFruitViewModelFactory
import com.nqmgaming.lab6_minhnqph31902.viewmodel.DistributorViewModel
import com.nqmgaming.lab6_minhnqph31902.viewmodel.DistributorViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AddFruitFragment : Fragment() {

    private var _binding: FragmentAddFruitBinding? = null
    private val binding get() = _binding!!
    private var listImage = mutableListOf<Uri>()
    private lateinit var imageAdapter: ImageFruitAdapter
    private var token: String? = null
    private lateinit var viewModel: DistributorViewModel
    private lateinit var distributor: Distributor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddFruitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        val repository = Repository()
        val viewModelFactory = DistributorViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[DistributorViewModel::class.java]
        token = SharedPrefUtils.getString(requireContext(), "token")
        token?.let {
            CoroutineScope(Dispatchers.Main).launch {
                val response =
                    withContext(Dispatchers.IO) { viewModel.getDistributors("Bearer $token") }
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        binding.distributorSpinner.item = data
                    }
                } else {
                    Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.distributorSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    position: Int,
                    l: Long
                ) {
                    distributor = adapterView?.getItemAtPosition(position) as Distributor
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
        var status = 0
        binding.radioGroupStatus.setOnCheckedChangeListener { _, checkedId ->
            status = when (checkedId) {
                R.id.active_rb -> 1
                R.id.inactive_rb -> 0
                else -> 0
            }
        }
        binding.addBtn.setOnClickListener {
            val name = binding.nameEt.text.toString().trim()
            val description = binding.desEt.text.toString().trim()
            val price = binding.priceEt.text.toString().trim().toDouble()
            val quantity = binding.quantityEt.text.toString().trim().toInt()

            val distributorId = distributor.id
            if (name.isEmpty() || description.isEmpty() || price.toString()
                    .isEmpty() || quantity.toString()
                    .isEmpty() || listImage.isEmpty() || distributorId.isEmpty()
            ) {
                Toast.makeText(requireContext(),
                    getString(R.string.txt_please_fill_all_fields), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val imageList = mutableListOf<File>()

            listImage.forEach {
                val realPath = RealPathUtil.getRealPath(requireContext(), it)
                val file = realPath?.let { it1 -> File(it1) }
                file?.let { image -> imageList.add(image) }
            }

            val fruitViewModelFactory = AddFruitViewModelFactory(repository)
            val fruitViewModel =
                ViewModelProvider(this, fruitViewModelFactory)[AddFruitViewModel::class.java]
            CoroutineScope(Dispatchers.IO).launch {
                val response = fruitViewModel.addFruit(
                    token = "Bearer $token",
                    name = name,
                    quantity = quantity,
                    price = price,
                    status = status,
                    description = description,
                    distributorId = distributorId,
                    imageFiles = imageList
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.txt_add_fruit_success),
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.txt_add_fruit_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        imageAdapter = ImageFruitAdapter(requireContext(), listImage)
        binding.imageProductRv.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }

        binding.addPhotoIv.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .galleryOnly()

                .createIntent { intent ->
                    imagePicker.launch(intent)
                }
        }

    }

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = result.data?.data
                    Log.d("AddFruitFragment", "imagePicker: $fileUri")
                    listImage.add(fileUri!!)
                    imageAdapter.notifyDataSetChanged()
                }

                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        ImagePicker.getError(result.data),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    Toast.makeText(requireContext(),
                        getString(R.string.txt_task_cancelled), Toast.LENGTH_SHORT).show()
                }
            }
        }
}