package com.hiskytechs.muhallinewuserapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.hiskytechs.muhallinewuserapp.Adapters.SupplierProductAdapter
import com.hiskytechs.muhallinewuserapp.Models.Product
import com.hiskytechs.muhallinewuserapp.Ui.CartActivity
import com.hiskytechs.muhallinewuserapp.Utill.CartManager
import com.hiskytechs.muhallinewuserapp.databinding.FragmentSupplierProductsBinding

class SupplierProductsFragment : Fragment() {

    private var _binding: FragmentSupplierProductsBinding? = null
    private val binding get() = _binding!!
    private var supplierName: String = ""

    companion object {
        fun newInstance(supplierName: String): SupplierProductsFragment {
            val fragment = SupplierProductsFragment()
            val args = Bundle()
            args.putString("supplier_name", supplierName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupplierProductsBinding.inflate(inflater, container, false)
        supplierName = arguments?.getString("supplier_name") ?: ""
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val products = listOf(
            Product("1", "Premium Potato Chips", 45.99, "carton", 0, supplierName),
            Product("2", "Mixed Nuts Pack", 89.99, "carton", 0, supplierName),
            Product("3", "Chocolate Cookies", 65.50, "carton", 0, supplierName),
            Product("4", "Energy Drinks 24pk", 120.0, "carton", 0, supplierName),
            Product("5", "Mineral Water 500ml", 15.99, "carton", 0, supplierName),
            Product("6", "Orange Juice 1L", 55.0, "carton", 0, supplierName)
        )

        binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProducts.isNestedScrollingEnabled = false
        binding.rvProducts.adapter = SupplierProductAdapter(products, supplierName) { cartItem ->
            CartManager.addItem(cartItem)

            startActivity(Intent(requireContext(), CartActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
