package com.hiskytechs.muhallinewuserapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiskytechs.muhallinewuserapp.Adapters.CartAdapter
import com.hiskytechs.muhallinewuserapp.Models.CartItem
import com.hiskytechs.muhallinewuserapp.Utill.CartManager
import com.hiskytechs.muhallinewuserapp.databinding.FragmentCartBinding
import java.util.Locale

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        updateSummary()
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
        val shipping = if (items.isEmpty()) 0.0 else 25.0
        val total = subtotal + shipping

        binding.tvSubtotal.text = String.format(Locale.getDefault(), "$%.2f", subtotal)
        binding.tvShipping.text = String.format(Locale.getDefault(), "$%.2f", shipping)
        binding.tvTotal.text = String.format(Locale.getDefault(), "$%.2f", total)
        binding.tvItemCountBadge.text = "${items.size} items"
        
        // Update progress bars (using dummy target values)
        binding.pbAmount.progress = ((subtotal / 500.0) * 100).toInt().coerceAtMost(100)
        binding.pbQuantity.progress = ((items.sumOf { it.quantity } / 20.0) * 100).toInt().coerceAtMost(100)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
