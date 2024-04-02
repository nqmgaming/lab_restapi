package com.nqmgaming.lab6_minhnqph31902.ui.fragment.fruit

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nqmgaming.lab6_minhnqph31902.R
import com.nqmgaming.lab6_minhnqph31902.adapter.FruitAdapter
import com.nqmgaming.lab6_minhnqph31902.databinding.FragmentFruitBinding
import com.nqmgaming.lab6_minhnqph31902.model.Fruit
import com.nqmgaming.lab6_minhnqph31902.repository.Repository
import com.nqmgaming.lab6_minhnqph31902.utils.SharedPrefUtils
import com.nqmgaming.lab6_minhnqph31902.viewmodel.FruitViewModel
import com.nqmgaming.lab6_minhnqph31902.viewmodel.FruitViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FruitFragment : Fragment() {
    private var _binding: FragmentFruitBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FruitViewModel
    private val adapter = FruitAdapter(
        onDelete = { fruit -> deleteFruit(fruit.id) },
        onUpdate = { fruit -> showDialogToUpdateFruit(fruit) }
    )
    private var originalFruitList = emptyList<Fruit>()
    private var token: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFruitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fab.setOnClickListener {
            findNavController().navigate((R.id.action_fruitFragment_to_addFruitFragment))
        }

        val repository = Repository()
        val viewModelFactory = FruitViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[FruitViewModel::class.java]

        setupRecyclerView()

        token = SharedPrefUtils.getString(requireContext(), "token")


        token?.let {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = viewModel.getFruits("Bearer $it")
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            response.body()?.let { fruits ->
                                originalFruitList = fruits
                                adapter.setFruitList(fruits)
                            }
                        } else {
                            Log.d("FruitFragment", "onViewCreated: ${response.message()}")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showDialogToUpdateFruit(fruit: Fruit) {
        val fruitId = fruit.id.toString()
        val args = Bundle()
        args.putString("fruitId", fruitId)
        args.putString("fruitName", fruit.name)
        args.putInt("fruitQuantity", fruit.quantity)
        args.putDouble("fruitPrice", fruit.price)
        args.putInt("fruitStatus", fruit.status)
        args.putString("fruitDescription", fruit.description)
        args.putString("fruitDistributor", fruit.distributor?.id)
        findNavController().navigate(R.id.action_fruitFragment_to_editFruitFragment, args)
    }

    private fun deleteFruit(fruitId: String) {
        Toast.makeText(requireContext(), "Delete fruit $fruitId", Toast.LENGTH_SHORT).show()
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Fruit")
        builder.setMessage("Are you sure you want to delete this fruit?")
        builder.setPositiveButton("Yes") { dialog, which ->
            token?.let {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = viewModel.deleteFruit("Bearer $it", fruitId)
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                val updatedFruitList = originalFruitList.toMutableList()
                                updatedFruitList.removeIf { it.id.toString() == fruitId }
                                adapter.setFruitList(updatedFruitList)
                            } else {
                                Log.d("FruitFragment", "deleteFruit: ${response.message()}")
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        builder.setNegativeButton("No") { dialog, which -> }
        builder.create().show()
    }

}