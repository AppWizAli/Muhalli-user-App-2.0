package com.hiskytechs.muhallinewuserapp.supplier.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.databinding.FragmentSupplierStoreProductsBinding
import com.hiskytechs.muhallinewuserapp.supplier.Adapters.SupplierStoreProductAdapter
import com.hiskytechs.muhallinewuserapp.supplier.Data.SupplierData
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierProductFilter
import com.hiskytechs.muhallinewuserapp.supplier.Ui.SupplierAddProductActivity
import com.hiskytechs.muhallinewuserapp.supplier.Ui.SupplierInventoryManagementActivity

class SupplierProductsFragment : Fragment() {

    private var _binding: FragmentSupplierStoreProductsBinding? = null
    private val binding get() = _binding!!
    private lateinit var productAdapter: SupplierStoreProductAdapter
    private var currentFilter = SupplierProductFilter.ALL

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupplierStoreProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productAdapter = SupplierStoreProductAdapter(
            items = emptyList(),
            onEdit = {
                startActivity(Intent(requireContext(), SupplierInventoryManagementActivity::class.java))
            },
            onToggle = { product, checked ->
                SupplierData.setProductAvailability(
                    productId = product.id,
                    isActive = checked,
                    onSuccess = {
                        if (_binding == null) return@setProductAvailability
                        loadProducts()
                    },
                    onError = { message ->
                        if (_binding == null) return@setProductAvailability
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )
        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProducts.adapter = productAdapter

        binding.etSearchProducts.addTextChangedListener { loadProducts() }

        val chips = listOf(binding.chipAll, binding.chipActive, binding.chipInactive, binding.chipLowStock)
        binding.chipAll.setOnClickListener {
            currentFilter = SupplierProductFilter.ALL
            updateChipState(chips, binding.chipAll)
            loadProducts()
        }
        binding.chipActive.setOnClickListener {
            currentFilter = SupplierProductFilter.ACTIVE
            updateChipState(chips, binding.chipActive)
            loadProducts()
        }
        binding.chipInactive.setOnClickListener {
            currentFilter = SupplierProductFilter.INACTIVE
            updateChipState(chips, binding.chipInactive)
            loadProducts()
        }
        binding.chipLowStock.setOnClickListener {
            currentFilter = SupplierProductFilter.LOW_STOCK
            updateChipState(chips, binding.chipLowStock)
            loadProducts()
        }

        binding.fabAddProduct.setOnClickListener {
            startActivity(Intent(requireContext(), SupplierAddProductActivity::class.java))
        }

        updateChipState(chips, binding.chipAll)
        refreshProducts()
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            refreshProducts()
        }
    }

    private fun refreshProducts() {
        SupplierData.refreshProducts(
            onSuccess = {
                if (_binding == null) return@refreshProducts
                loadProducts()
            },
            onError = { message ->
                if (_binding == null) return@refreshProducts
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun loadProducts() {
        productAdapter.updateItems(
            SupplierData.getProducts(
                filter = currentFilter,
                query = binding.etSearchProducts.text?.toString().orEmpty()
            )
        )
    }

    private fun updateChipState(chips: List<TextView>, selectedChip: TextView) {
        chips.forEach { chip ->
            val isSelected = chip == selectedChip
            chip.setBackgroundResource(
                if (isSelected) R.drawable.bg_supplier_pill_selected else R.drawable.bg_supplier_pill_default
            )
            chip.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isSelected) R.color.white else R.color.supplier_text_secondary
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
