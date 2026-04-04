package com.hiskytechs.muhallinewuserapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiskytechs.muhallinewuserapp.Adapters.SupplierAdapter
import com.hiskytechs.muhallinewuserapp.Models.Supplier
import com.hiskytechs.muhallinewuserapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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
    }

    private fun setupRecyclerView() {
        val suppliers = listOf(
            Supplier("Al-Hamd Wholesale", "250 products", "1-2 days", "$500", "20 items"),
            Supplier("Al-Sadiqa Company", "180 products", "Same day", "$300", "15 items"),
            Supplier("Al-Far Imports", "320 products", "2-3 days", "$1000", "50 items"),
            Supplier("Premium Foods LLC", "210 products", "1-2 days", "$400", "25 items"),
            Supplier("Global Trade Supplies", "290 products", "Same day", "$600", "30 items")
        )

        binding.rvSuppliers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSuppliers.adapter = SupplierAdapter(suppliers)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
