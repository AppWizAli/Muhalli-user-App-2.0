package com.hiskytechs.muhallinewuserapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiskytechs.muhallinewuserapp.Adapters.CartAdapter
import com.hiskytechs.muhallinewuserapp.Adapters.CartSupplierAdapter
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.Ui.CheckoutAddressActivity
import com.hiskytechs.muhallinewuserapp.Utill.CartManager
import com.hiskytechs.muhallinewuserapp.Utill.SupplierCart
import com.hiskytechs.muhallinewuserapp.databinding.FragmentCartBinding
import java.util.Locale

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartAdapter: CartAdapter
    private lateinit var supplierAdapter: CartSupplierAdapter
    private var showHeader: Boolean = true
    private var selectedSupplierName: String? = null
    private var initialSupplierName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        showHeader = arguments?.getBoolean(ARG_SHOW_HEADER) ?: true
        initialSupplierName = arguments?.getString(ARG_INITIAL_SUPPLIER)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSupplierRecyclerView()
        setupCartRecyclerView()
        refreshCartUi(initialSupplierName)

        binding.btnCheckout.setOnClickListener {
            val activeSupplierName = selectedSupplierName
            val selectedCart = activeSupplierName?.let { CartManager.getSupplierCart(it) }

            when {
                selectedCart == null -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.cart_empty_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                !selectedCart.isMinimumMet -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.cart_checkout_requirements_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    startActivity(
                        Intent(requireContext(), CheckoutAddressActivity::class.java).apply {
                            putExtra(CheckoutAddressActivity.EXTRA_SUPPLIER_NAME, activeSupplierName)
                        }
                    )
                }
            }
        }

        if (!showHeader) {
            binding.tvTitle.visibility = View.GONE
            binding.tvItemCountBadge.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null && this::cartAdapter.isInitialized && this::supplierAdapter.isInitialized) {
            refreshCartUi(selectedSupplierName)
        }
    }

    private fun setupSupplierRecyclerView() {
        supplierAdapter = CartSupplierAdapter(
            supplierCarts = emptyList(),
            selectedSupplierName = null
        ) { supplierName ->
            refreshCartUi(supplierName)
        }
        binding.rvSuppliers.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvSuppliers.adapter = supplierAdapter
    }

    private fun setupCartRecyclerView() {
        cartAdapter = CartAdapter(
            emptyList(),
            onQuantityChanged = {
                refreshCartUi(selectedSupplierName)
            },
            onDeleteItem = { item ->
                CartManager.removeItem(item)
                refreshCartUi(selectedSupplierName)
            }
        )
        binding.rvCartItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCartItems.adapter = cartAdapter
    }

    private fun refreshCartUi(preferredSupplierName: String? = selectedSupplierName) {
        val supplierCarts = CartManager.getSupplierCarts()
        selectedSupplierName = supplierCarts
            .firstOrNull { it.supplierName.equals(preferredSupplierName, ignoreCase = true) }
            ?.supplierName
            ?: supplierCarts.firstOrNull()?.supplierName
        initialSupplierName = null

        val selectedCart = selectedSupplierName?.let { CartManager.getSupplierCart(it) }
        supplierAdapter.updateItems(supplierCarts, selectedSupplierName)
        cartAdapter.updateItems(selectedCart?.items.orEmpty())
        updateSummary(selectedCart, supplierCarts.isNotEmpty())
    }

    private fun updateSummary(selectedCart: SupplierCart?, hasSuppliers: Boolean) {
        binding.rvSuppliers.visibility = if (hasSuppliers) View.VISIBLE else View.GONE

        if (selectedCart == null) {
            binding.tvOrderProgressSubtitle.text = getString(R.string.complete_minimum_order_requirements)
            binding.tvItemCountBadge.text = getString(R.string.items_count_format, 0)
            binding.tvSubtotal.text = formatCurrency(0.0)
            binding.tvShipping.text = formatCurrency(0.0)
            binding.tvTotal.text = formatCurrency(0.0)
            binding.tvAmountProgress.text = getString(R.string.cart_progress_amount_format, 0.0, 0.0)
            binding.tvQuantityProgress.text = getString(R.string.cart_quantity_progress_format, 0, 0)
            binding.tvAmountRemaining.text = getString(R.string.cart_empty_message)
            binding.tvQuantityRemaining.text = getString(R.string.cart_empty_message)
            binding.tvAmountRemaining.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.status_processing_text)
            )
            binding.tvQuantityRemaining.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.status_processing_text)
            )
            binding.pbAmount.progress = 0
            binding.pbQuantity.progress = 0
            binding.btnCheckout.isEnabled = false
            binding.btnCheckout.alpha = 0.6f
            return
        }

        binding.tvOrderProgressSubtitle.text = selectedCart.supplierName
        binding.tvItemCountBadge.text = getString(
            R.string.items_count_format,
            selectedCart.lineItemCount
        )
        binding.tvSubtotal.text = formatCurrency(selectedCart.subtotal)
        binding.tvShipping.text = formatCurrency(selectedCart.shipping)
        binding.tvTotal.text = formatCurrency(selectedCart.total)
        binding.tvAmountProgress.text = getString(
            R.string.cart_progress_amount_format,
            selectedCart.subtotal,
            selectedCart.minimumAmount
        )
        binding.tvQuantityProgress.text = getString(
            R.string.cart_quantity_progress_format,
            selectedCart.totalQuantity,
            selectedCart.minimumQuantity
        )
        binding.tvAmountRemaining.text = if (selectedCart.remainingAmount > 0.0) {
            getString(R.string.cart_add_more_amount_format, selectedCart.remainingAmount)
        } else {
            getString(R.string.minimum_amount_reached)
        }
        binding.tvQuantityRemaining.text = if (selectedCart.remainingQuantity > 0) {
            getString(R.string.cart_add_more_quantity_format, selectedCart.remainingQuantity)
        } else {
            getString(R.string.minimum_quantity_reached)
        }

        val statusColor = ContextCompat.getColor(
            requireContext(),
            if (selectedCart.isMinimumMet) R.color.status_delivered_text else R.color.status_processing_text
        )
        binding.tvAmountRemaining.setTextColor(statusColor)
        binding.tvQuantityRemaining.setTextColor(statusColor)

        binding.pbAmount.progress = calculateProgress(
            currentValue = selectedCart.subtotal,
            minimumValue = selectedCart.minimumAmount
        )
        binding.pbQuantity.progress = calculateProgress(
            currentValue = selectedCart.totalQuantity.toDouble(),
            minimumValue = selectedCart.minimumQuantity.toDouble()
        )
        binding.btnCheckout.isEnabled = selectedCart.isMinimumMet
        binding.btnCheckout.alpha = if (selectedCart.isMinimumMet) 1f else 0.6f
    }

    private fun calculateProgress(currentValue: Double, minimumValue: Double): Int {
        if (minimumValue <= 0.0) {
            return if (currentValue > 0.0) 100 else 0
        }
        return ((currentValue / minimumValue) * 100).toInt().coerceIn(0, 100)
    }

    private fun formatCurrency(amount: Double): String {
        return String.format(
            Locale.getDefault(),
            getString(R.string.currency_amount_format),
            amount
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_SHOW_HEADER = "show_header"
        private const val ARG_INITIAL_SUPPLIER = "initial_supplier"

        fun newInstance(showHeader: Boolean, initialSupplierName: String? = null): CartFragment {
            return CartFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_SHOW_HEADER, showHeader)
                    putString(ARG_INITIAL_SUPPLIER, initialSupplierName)
                }
            }
        }
    }
}
