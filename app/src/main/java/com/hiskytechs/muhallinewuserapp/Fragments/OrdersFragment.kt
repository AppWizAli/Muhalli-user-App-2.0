package com.hiskytechs.muhallinewuserapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiskytechs.muhallinewuserapp.Adapters.OrderAdapter
import com.hiskytechs.muhallinewuserapp.Models.Order
import com.hiskytechs.muhallinewuserapp.databinding.FragmentOrdersBinding

class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderAdapter: OrderAdapter
    private var allOrders = mutableListOf<Order>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadDummyData()
        setupFilters()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(allOrders) { order ->
            // Handle view details
        }
        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrders.adapter = orderAdapter
    }

    private fun loadDummyData() {
        allOrders = mutableListOf(
            Order("ORD-12345", "March 14, 2026", "Delivered", "Al-Hamd Wholesale", 3, 231.97),
            Order("ORD-12344", "March 12, 2026", "In Transit", "Al-Sadiqa Company", 2, 269.98, "March 17, 2026"),
            Order("ORD-12343", "March 10, 2026", "Processing", "Al-Far Imports", 5, 445.50),
            Order("ORD-12342", "March 8, 2026", "Delivered", "Premium Foods LLC", 4, 389.99),
            Order("ORD-12341", "March 5, 2026", "Cancelled", "Global Trade Supplies", 2, 178.00)
        )
        orderAdapter.updateOrders(allOrders)
    }

    private fun setupFilters() {
        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val filteredOrders = when (checkedId) {
                    binding.btnProcessing.id -> allOrders.filter { it.status.lowercase() == "processing" }
                    binding.btnDelivered.id -> allOrders.filter { it.status.lowercase() == "delivered" }
                    else -> allOrders
                }
                orderAdapter.updateOrders(filteredOrders)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
