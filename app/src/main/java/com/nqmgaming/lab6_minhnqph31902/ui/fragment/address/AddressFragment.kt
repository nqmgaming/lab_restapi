package com.nqmgaming.lab6_minhnqph31902.ui.fragment.address

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.nqmgaming.lab6_minhnqph31902.R
import com.nqmgaming.lab6_minhnqph31902.databinding.FragmentAddressBinding
import com.nqmgaming.lab6_minhnqph31902.model.DistrictData
import com.nqmgaming.lab6_minhnqph31902.model.ProvinceData
import com.nqmgaming.lab6_minhnqph31902.model.WardData
import com.nqmgaming.lab6_minhnqph31902.repository.Repository
import com.nqmgaming.lab6_minhnqph31902.utils.Constants
import com.nqmgaming.lab6_minhnqph31902.viewmodel.AddressViewModel
import com.nqmgaming.lab6_minhnqph31902.viewmodel.AddressViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class AddressFragment : Fragment() {

    private var _binding: FragmentAddressBinding? = null
    private val binding get() = _binding!!
    val token = Constants.TOKEN
    private var provinceId by Delegates.notNull<Int>()
    private var districtId by Delegates.notNull<Int>()
    private var wardId by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = Repository()
        val viewModelFactory = AddressViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[AddressViewModel::class.java]

        binding.districtSpinner.isEnabled = false
        binding.districtSpinner.errorText = getString(R.string.txt_please_choose_province_first)
        binding.wardSpinner.isEnabled = false
        binding.wardSpinner.errorText = getString(R.string.txt_please_choose_district_first)


        CoroutineScope(Dispatchers.IO).launch {
            val response = viewModel.getProvinces(token)
            if (response.isSuccessful) {
                val provinces = response.body()?.data
                CoroutineScope(Dispatchers.Main).launch {

                    binding.districtSpinner.isEnabled = true
                    binding.provinceSpinner.item = provinces

                    binding.provinceSpinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                adapterView: AdapterView<*>?,
                                view: View,
                                position: Int,
                                l: Long
                            ) {
                                val province =
                                    adapterView?.getItemAtPosition(position) as ProvinceData
                                provinceId = province.provinceID
                                CoroutineScope(Dispatchers.IO).launch {
                                    val districtResponseResponse = viewModel.getDistricts(token, provinceId)
                                    if (districtResponseResponse.isSuccessful) {
                                        val districts = districtResponseResponse.body()?.data
                                        CoroutineScope(Dispatchers.Main).launch {
                                            binding.districtSpinner.item = districts
                                            binding.districtSpinner.isEnabled = true
                                            binding.districtSpinner.errorText = null
                                            binding.districtSpinner.onItemSelectedListener =
                                                object : AdapterView.OnItemSelectedListener {
                                                    override fun onItemSelected(
                                                        adapterView: AdapterView<*>?,
                                                        view: View,
                                                        position: Int,
                                                        l: Long
                                                    ) {
                                                        val district =
                                                            adapterView?.getItemAtPosition(position) as DistrictData
                                                        districtId = district.districtID
                                                        CoroutineScope(Dispatchers.IO).launch {
                                                            val wardResponseResponse = viewModel.getWards(
                                                                token,
                                                                districtId
                                                            )
                                                            if (wardResponseResponse.isSuccessful) {
                                                                val wards = wardResponseResponse.body()?.data
                                                                CoroutineScope(Dispatchers.Main).launch {
                                                                    binding.wardSpinner.item = wards
                                                                    binding.wardSpinner.isEnabled = true
                                                                    binding.wardSpinner.errorText = null
                                                                    binding.wardSpinner.onItemSelectedListener =
                                                                        object :
                                                                            AdapterView.OnItemSelectedListener {
                                                                            override fun onItemSelected(
                                                                                adapterView: AdapterView<*>?,
                                                                                view: View,
                                                                                position: Int,
                                                                                l: Long
                                                                            ) {
                                                                                val ward =
                                                                                    adapterView?.getItemAtPosition(
                                                                                        position
                                                                                    ) as WardData
                                                                                wardId =
                                                                                    ward.wardCode
                                                                            }

                                                                            override fun onNothingSelected(
                                                                                adapterView: AdapterView<*>?
                                                                            ) {
                                                                            }
                                                                        }
                                                                }
                                                            } else {
                                                                Log.d(
                                                                    "AddressFragment",
                                                                    "onViewCreated: ${wardResponseResponse.code()}"
                                                                )
                                                            }
                                                        }
                                                    }

                                                    override fun onNothingSelected(adapterView: AdapterView<*>?) {}
                                                }
                                        }
                                    } else {
                                        Log.d(
                                            "AddressFragment",
                                            "onViewCreated: ${districtResponseResponse.code()}"
                                        )
                                    }
                                }
                            }

                            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
                        }
                }
            } else {
                Log.d("AddressFragment", "onViewCreated: ${response.code()}")
            }
        }

        binding.btnSave.setOnClickListener {
            val province = binding.provinceSpinner.selectedItem as? ProvinceData
            val district = binding.districtSpinner.selectedItem as? DistrictData
            val ward = binding.wardSpinner.selectedItem as? WardData

            if (province != null && district != null && ward != null) {
                val addressDetail = "${province.provinceName}, ${district.districtName}, ${ward.wardName}"
                binding.tvAddressResultDetail.text = addressDetail
            } else {
               Toast.makeText(context,
                   getString(R.string.txt_not_enough_info_yet), Toast.LENGTH_SHORT).show()
            }
        }
    }
}