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
import com.nqmgaming.lab6_minhnqph31902.databinding.FragmentEditFruitBinding
import com.nqmgaming.lab6_minhnqph31902.model.Distributor
import com.nqmgaming.lab6_minhnqph31902.model.Fruit
import com.nqmgaming.lab6_minhnqph31902.model.Images
import com.nqmgaming.lab6_minhnqph31902.repository.Repository
import com.nqmgaming.lab6_minhnqph31902.utils.RealPathUtil
import com.nqmgaming.lab6_minhnqph31902.utils.SharedPrefUtils
import com.nqmgaming.lab6_minhnqph31902.viewmodel.DistributorViewModel
import com.nqmgaming.lab6_minhnqph31902.viewmodel.DistributorViewModelFactory
import com.nqmgaming.lab6_minhnqph31902.viewmodel.EditFruitViewModel
import com.nqmgaming.lab6_minhnqph31902.viewmodel.EditFruitViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class EditFruitFragment : Fragment() {
    private var _binding: FragmentEditFruitBinding? = null
    private val binding get() = _binding!!
    private var token: String? = null
    private lateinit var viewModel: DistributorViewModel
    private var distributor: Distributor? = null
    private lateinit var imageAdapter: ImageFruitAdapter
    private var fruit: Fruit? = null
    private lateinit var uriList: MutableList<Uri>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditFruitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.nameEt.setText(arguments?.getString("fruitName"))
        binding.quantityEt.setText(arguments?.getInt("fruitQuantity").toString())
        binding.priceEt.setText(arguments?.getDouble("fruitPrice").toString())
        binding.desEt.setText(arguments?.getString("fruitDescription"))
        val distributorId = arguments?.getString("fruitDistributor")
        if (arguments?.getInt("fruitStatus") == 1) {
            binding.activeRb.isChecked = true
        } else {
            binding.inactiveRb.isChecked = true
        }


        val repository = Repository()
        val viewModelFactory = DistributorViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[DistributorViewModel::class.java]
        token = SharedPrefUtils.getString(requireContext(), "token")
        token?.let {
            CoroutineScope(Dispatchers.Main).launch {
                val response =
                    withContext(Dispatchers.IO) {
                        viewModel.getDistributor(
                            "Bearer $token",
                            distributorId!!
                        )
                    }
                if (response.isSuccessful) {
                    val data = response.body()
                    distributor = data ?: distributor
                } else {
                    Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            }
        }

        token?.let {
            CoroutineScope(Dispatchers.Main).launch {
                val response =
                    withContext(Dispatchers.IO) { viewModel.getDistributors("Bearer $token") }
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        binding.distributorSpinner.item = data
                        val position = data.indexOf(distributor)
                        binding.distributorSpinner.setSelection(position)
                    }
                } else {
                    Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val editFruitViewModelFactory = EditFruitViewModelFactory(repository)
        val editFruitViewModel =
            ViewModelProvider(this, editFruitViewModelFactory)[EditFruitViewModel::class.java]

        //get fruit by id and set image
        val fruitId = arguments?.getString("fruitId")
        token?.let {
            CoroutineScope(Dispatchers.Main).launch {
                val response = withContext(Dispatchers.IO) {
                    editFruitViewModel.getFruit("Bearer $token", fruitId!!)
                }
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        fruit = data
                        Log.d("EditFruitFragment", "onViewCreated: $data")
                        val imagesList: List<Images> = fruit?.image ?: emptyList()
                        uriList = imagesList.map { Uri.parse(it.url) }.toMutableList()

                        imageAdapter = ImageFruitAdapter(requireContext(), uriList)
                        binding.imageProductRv.apply {
                            layoutManager = LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            adapter = imageAdapter
                        }
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

                override fun onNothingSelected(adapterView: AdapterView<*>?) {

                }
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
        var status = 0
        binding.radioGroupStatus.setOnCheckedChangeListener { _, checkedId ->
            status = when (checkedId) {
                R.id.active_rb -> 1
                R.id.inactive_rb -> 0
                else -> 0
            }
        }
        binding.addSave.setOnClickListener {

            val name = binding.nameEt.text.toString().trim()
            val description = binding.desEt.text.toString().trim()
            val price = binding.priceEt.text.toString().trim().toDouble()
            val quantity = binding.quantityEt.text.toString().trim().toInt()
            val distributor = distributor?.id
            if (name.isEmpty() || description.isEmpty() || price.toString()
                    .isEmpty() || quantity.toString()
                    .isEmpty() || distributor.isNullOrEmpty()
            ) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val fileList = mutableListOf<File>()

            uriList.forEach { uri ->
                if (uri.scheme == "file") {
                    val realPath = RealPathUtil.getRealPath(requireContext(), uri)
                    val file = realPath?.let { it1 -> File(it1) }
                    file?.let { fileList.add(it) }
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                val response = editFruitViewModel.updateFruit(
                    "Bearer $token",
                    fruitId!!,
                    name,
                    quantity,
                    price,
                    status,
                    description,
                    distributor,
                    fileList
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Update fruit success",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Update fruit failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        }

    }

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = result.data?.data
                    Log.d("AddFruitFragment", "imagePicker: $fileUri")
                    uriList.add(fileUri!!)
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
                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
}