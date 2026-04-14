package com.hiskytechs.muhallinewuserapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiskytechs.muhallinewuserapp.Adapters.SupplierAdapter
import com.hiskytechs.muhallinewuserapp.Data.AppData
import com.hiskytechs.muhallinewuserapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var supplierAdapter: SupplierAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadSuppliers()
    }

    private fun setupRecyclerView() {
        binding.rvSuppliers.layoutManager = LinearLayoutManager(requireContext())
        supplierAdapter = SupplierAdapter(emptyList())
        binding.rvSuppliers.adapter = supplierAdapter
    }

    private fun loadSuppliers() {
        AppData.loadHomeSuppliers(
            onSuccess = { suppliers ->
                if (_binding == null) return@loadHomeSuppliers
                supplierAdapter = SupplierAdapter(suppliers)
                binding.rvSuppliers.adapter = supplierAdapter
            },
            onError = { message ->
                if (_binding == null) return@loadHomeSuppliers
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
