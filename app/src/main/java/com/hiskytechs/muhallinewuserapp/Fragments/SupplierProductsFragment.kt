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
import com.hiskytechs.muhallinewuserapp.R
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
        val unitCarton = getString(R.string.data_unit_carton)
        val products = listOf(
            Product("1", getString(R.string.data_product_premium_potato_chips), 45.99, unitCarton, 0, supplierName),
            Product("2", getString(R.string.data_product_mixed_nuts_pack), 89.99, unitCarton, 0, supplierName),
            Product("3", getString(R.string.data_product_chocolate_cookies), 65.50, unitCarton, 0, supplierName),
            Product("4", getString(R.string.data_product_energy_drinks_24pk), 120.0, unitCarton, 0, supplierName),
            Product("5", getString(R.string.data_product_mineral_water_500ml), 15.99, unitCarton, 0, supplierName),
            Product("6", getString(R.string.data_product_orange_juice_1l), 55.0, unitCarton, 0, supplierName)
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
