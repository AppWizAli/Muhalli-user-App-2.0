package com.hiskytechs.muhallinewuserapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.hiskytechs.muhallinewuserapp.Adapters.SupplierProductAdapter
import com.hiskytechs.muhallinewuserapp.Data.AppData
import com.hiskytechs.muhallinewuserapp.Models.Product
import com.hiskytechs.muhallinewuserapp.Ui.CartActivity
import com.hiskytechs.muhallinewuserapp.Utill.CartManager
import com.hiskytechs.muhallinewuserapp.databinding.FragmentSupplierProductsBinding

class SupplierProductsFragment : Fragment() {

    private var _binding: FragmentSupplierProductsBinding? = null
    private val binding get() = _binding!!
    private var supplierName: String = ""
    private lateinit var supplierProductAdapter: SupplierProductAdapter

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
        loadProducts()
    }

    private fun setupRecyclerView() {
        binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProducts.isNestedScrollingEnabled = false
        supplierProductAdapter = SupplierProductAdapter(emptyList(), supplierName) { cartItem ->
            CartManager.addItem(cartItem)

            startActivity(
                Intent(requireContext(), CartActivity::class.java).apply {
                    putExtra(CartActivity.EXTRA_SUPPLIER_NAME, supplierName)
                }
            )
        }
        binding.rvProducts.adapter = supplierProductAdapter
    }

    private fun loadProducts() {
        AppData.loadSupplierProducts(
            supplierName = supplierName,
            onSuccess = { products: List<Product> ->
                if (_binding == null) return@loadSupplierProducts
                supplierProductAdapter = SupplierProductAdapter(products, supplierName) { cartItem ->
                    CartManager.addItem(cartItem)
                    startActivity(
                        Intent(requireContext(), CartActivity::class.java).apply {
                            putExtra(CartActivity.EXTRA_SUPPLIER_NAME, supplierName)
                        }
                    )
                }
                binding.rvProducts.adapter = supplierProductAdapter
            },
            onError = { message ->
                if (_binding == null) return@loadSupplierProducts
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
