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
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.Ui.CheckoutAddressActivity
import com.hiskytechs.muhallinewuserapp.Utill.CartManager
import com.hiskytechs.muhallinewuserapp.databinding.FragmentCartBinding
import java.util.Locale

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartAdapter: CartAdapter
    private var showHeader: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        showHeader = arguments?.getBoolean(ARG_SHOW_HEADER) ?: true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        updateSummary()
        binding.btnCheckout.setOnClickListener {
            if (CartManager.getItems().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.cart_empty_message),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                startActivity(Intent(requireContext(), CheckoutAddressActivity::class.java))
            }
        }

        if (!showHeader) {
            binding.tvTitle.visibility = View.GONE
            binding.tvItemCountBadge.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null && this::cartAdapter.isInitialized) {
            cartAdapter.updateItems(CartManager.getItems().toMutableList())
            updateSummary()
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            CartManager.getItems().toMutableList(),
            onQuantityChanged = { updateSummary() },
            onDeleteItem = { item ->
                CartManager.removeItem(item)
                cartAdapter.updateItems(CartManager.getItems().toMutableList())
                updateSummary()
            }
        )
        binding.rvCartItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCartItems.adapter = cartAdapter
    }

    private fun updateSummary() {
        val items = CartManager.getItems()
        val subtotal = CartManager.getSubtotal()
        val shipping = CartManager.getShipping()
        val total = CartManager.getTotal()
        val totalQuantity = items.sumOf { it.quantity }
        val minimumAmount = 500.0
        val minimumQuantity = 20
        val remainingAmount = (minimumAmount - subtotal).coerceAtLeast(0.0)
        val remainingQuantity = (minimumQuantity - totalQuantity).coerceAtLeast(0)

        binding.tvSubtotal.text = String.format(
            Locale.getDefault(),
            getString(R.string.currency_amount_format),
            subtotal
        )
        binding.tvShipping.text = String.format(
            Locale.getDefault(),
            getString(R.string.currency_amount_format),
            shipping
        )
        binding.tvTotal.text = String.format(
            Locale.getDefault(),
            getString(R.string.currency_amount_format),
            total
        )
        binding.tvItemCountBadge.text = getString(R.string.items_count_format, items.size)
        binding.tvAmountProgress.text = String.format(
            Locale.getDefault(),
            getString(R.string.cart_progress_amount_format),
            subtotal,
            minimumAmount
        )
        binding.tvQuantityProgress.text = getString(
            R.string.cart_quantity_progress_format,
            totalQuantity,
            minimumQuantity
        )
        binding.tvAmountRemaining.text = if (remainingAmount > 0.0) {
            String.format(
                Locale.getDefault(),
                getString(R.string.cart_add_more_amount_format),
                remainingAmount
            )
        } else {
            getString(R.string.minimum_amount_reached)
        }
        binding.tvQuantityRemaining.text = if (remainingQuantity > 0) {
            getString(R.string.cart_add_more_quantity_format, remainingQuantity)
        } else {
            getString(R.string.minimum_quantity_reached)
        }

        val statusColor = if (remainingAmount > 0.0 || remainingQuantity > 0) {
            ContextCompat.getColor(requireContext(), R.color.status_processing_text)
        } else {
            ContextCompat.getColor(requireContext(), R.color.status_delivered_text)
        }
        binding.tvAmountRemaining.setTextColor(statusColor)
        binding.tvQuantityRemaining.setTextColor(statusColor)

        binding.pbAmount.progress = ((subtotal / minimumAmount) * 100).toInt().coerceAtMost(100)
        binding.pbQuantity.progress =
            ((totalQuantity / minimumQuantity.toDouble()) * 100).toInt().coerceAtMost(100)
        binding.btnCheckout.isEnabled = items.isNotEmpty()
        binding.btnCheckout.alpha = if (items.isEmpty()) 0.6f else 1f
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_SHOW_HEADER = "show_header"

        fun newInstance(showHeader: Boolean): CartFragment {
            return CartFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_SHOW_HEADER, showHeader)
                }
            }
        }
    }
}
