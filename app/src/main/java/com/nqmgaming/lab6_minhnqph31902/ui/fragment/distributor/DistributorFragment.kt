package com.nqmgaming.lab6_minhnqph31902.ui.fragment.distributor

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nqmgaming.lab6_minhnqph31902.R
import com.nqmgaming.lab6_minhnqph31902.adapter.DistributorAdapter
import com.nqmgaming.lab6_minhnqph31902.databinding.FragmentDistributorBinding
import com.nqmgaming.lab6_minhnqph31902.model.Distributor
import com.nqmgaming.lab6_minhnqph31902.repository.Repository
import com.nqmgaming.lab6_minhnqph31902.utils.SharedPrefUtils
import com.nqmgaming.lab6_minhnqph31902.viewmodel.DistributorViewModel
import com.nqmgaming.lab6_minhnqph31902.viewmodel.DistributorViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DistributorFragment : Fragment() {

    private var _binding: FragmentDistributorBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DistributorViewModel
    private val adapter by lazy {
        DistributorAdapter(
            onDelete = { distributor -> deleteDistributor(distributor.id) },
            onUpdate = { distributor -> showDialogToUpdateDistributor(distributor) })
    }
    private var originalDistributorList = emptyList<Distributor>()
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDistributorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = Repository()
        val viewModelFactory = DistributorViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[DistributorViewModel::class.java]

        setupRecyclerView()

        token = SharedPrefUtils.getString(requireContext(), "token")
        token?.let {
            CoroutineScope(Dispatchers.Main).launch {
                val response = withContext(Dispatchers.IO) { viewModel.getDistributors("Bearer $token") }
                if (response.isSuccessful) {
                    val data = response.body()
                    adapter.setData(data!!)
                } else {
                    Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.fab.setOnClickListener { showDialogToAddDistributor() }
        setupSearchView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupSearchView() {
        binding.persistentSearchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { text ->
                    if (text.isNotEmpty()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val response = viewModel.searchDistributor("Bearer $token", text)
                            if (response.isSuccessful) {
                                response.body()?.let { distributors ->
                                    withContext(Dispatchers.Main) { adapter.setData(distributors) }
                                }
                            }
                        }
                    } else {
                        adapter.setData(originalDistributorList)
                    }
                }
                return false
            }
        })
    }

    private fun showDialogToAddDistributor() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.layout_add_distributor, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.name_et)
        builder.setView(dialogLayout)

        builder.setPositiveButton("Add") { _, _ ->
            val name = editText.text.toString()
            if (name.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val response = viewModel.addDistributor("Bearer $token", name)
                    if (response.isSuccessful) {
                        val res = viewModel.getDistributors("Bearer $token")
                        if (res.isSuccessful) {
                            withContext(Dispatchers.Main) {
                                res.body()?.let { adapter.setData(it) }
                            }
                        }
                    }
                }
            }
        }

        builder.setNegativeButton("Cancel") { _, _ -> }
        builder.setCancelable(true)
        builder.create().show()
    }

    private fun showDialogToUpdateDistributor(distributor: Distributor) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.layout_add_distributor, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.name_et)
        editText.setText(distributor.name)
        builder.setView(dialogLayout)

        builder.setPositiveButton("Update") { _, _ ->
            val name = editText.text.toString()
            if (name.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val response = viewModel.updateDistributor(
                        "Bearer $token",
                        distributor.id,
                        name
                    )
                    if (response.isSuccessful) {
                        val res = viewModel.getDistributors("Bearer $token")
                        if (res.isSuccessful) {
                            withContext(Dispatchers.Main) {
                                res.body()?.let { adapter.setData(it) }
                            }
                        }
                    }
                }
            }
        }

        builder.setNegativeButton("Cancel") { _, _ -> }
        builder.setCancelable(true)
        builder.create().show()
    }

    private fun deleteDistributor(distributorId: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Distributor")
        builder.setMessage("Are you sure you want to delete this distributor?")
        builder.setPositiveButton("Yes") { _, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                val response = viewModel.deleteDistributor("Bearer $token", distributorId)
                if (response.isSuccessful) {
                    val res = viewModel.getDistributors("Bearer $token")
                    if (res.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            res.body()?.let { adapter.setData(it) }
                        }
                    }
                }
            }
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}