package com.nqmgaming.lab6_minhnqph31902.ui.fragment.fruit

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.core.widget.NestedScrollView
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
    private var page = 1
    private var name: String? = null
    private var price: Int? = null
    private var sort: String? = null
    private lateinit var progressBar: ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFruitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = binding.progressBar
        binding.fab.setOnClickListener {
            findNavController().navigate((R.id.action_fruitFragment_to_addFruitFragment))
        }
        val spinner: Spinner = binding.spinnerSort
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
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
        binding.btnRefresh.setOnClickListener{
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
        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                loadMoreItems()
            }
        })

        binding.btnFilter.setOnClickListener {
            name = binding.etSearch.text.toString()
            price = binding.etFilterPrice.text.toString().toIntOrNull()
            val selectedItem = spinner.selectedItem.toString()
            sort = if (selectedItem == "Ascending") "asc" else "desc"
            token?.let {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response =
                            viewModel.getFruitsQuery("Bearer $it", name, price, 1, sort)
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                response.body()?.let { fruitResponse ->
                                    originalFruitList = fruitResponse.fruits
                                    adapter.setFruitList(fruitResponse.fruits)

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
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadMoreItems() {
        progressBar.visibility = View.VISIBLE  // Show the ProgressBar

        page++
        token?.let {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = viewModel.getFruitsQuery("Bearer $it", name, price, page, sort)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            response.body()?.let { fruitResponse ->
                                val updatedFruitList = originalFruitList.toMutableList()
                                updatedFruitList.addAll(fruitResponse.fruits)
                                originalFruitList = updatedFruitList
                                adapter.setFruitList(updatedFruitList)
                                binding.tvCount.text = "Total: ${fruitResponse.page} fruits"

                                //if the current page is the last page, then disable the scroll listener
                                if (fruitResponse.page == fruitResponse.pages) {
                                    binding.nestedScrollView.setOnClickListener(null)
                                    //reset the page to the previous page
                                }

                                // reset the page to the previous page if no more data to load
                                if (fruitResponse.page == fruitResponse.pages) {
                                    page--
                                }
                            }
                        } else {
                            Log.d("FruitFragment", "loadMoreItems: ${response.code()}")
                        }

                        progressBar.visibility = View.GONE  // Hide the ProgressBar
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
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