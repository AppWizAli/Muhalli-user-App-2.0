package com.hiskytechs.muhallinewuserapp.Ui

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.hiskytechs.muhallinewuserapp.Adapters.SupplierProductAdapter
import com.hiskytechs.muhallinewuserapp.Data.AppData
import com.hiskytechs.muhallinewuserapp.Models.CartItem
import com.hiskytechs.muhallinewuserapp.Models.Product
import com.hiskytechs.muhallinewuserapp.Models.Supplier
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.Utill.CartManager
import com.hiskytechs.muhallinewuserapp.Utill.SupplierCart
import com.hiskytechs.muhallinewuserapp.databinding.ActivitySupplierDetailsBinding
import com.hiskytechs.muhallinewuserapp.network.CurrencyFormatter

class SupplierDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierDetailsBinding
    private var supplierName: String = ""
    private var supplier: Supplier? = null
    private lateinit var productAdapter: SupplierProductAdapter
    private var activeLoadingCount = 0
    private var currentQuery: String = ""
    private var selectedCategoryName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supplierName = intent.getStringExtra("supplier_name").orEmpty()
        selectedCategoryName = intent.getStringExtra("category_name").orEmpty()
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecycler()
        setupInteractions()
        loadSupplier()
        loadProducts()
    }

    override fun onResume() {
        super.onResume()
        updateOrderProgress()
    }

    private fun setupRecycler() {
        productAdapter = SupplierProductAdapter(emptyList(), supplierName, ::onProductQuantityChanged)
        binding.rvProducts.layoutManager = GridLayoutManager(this, 2)
        binding.rvProducts.adapter = productAdapter
    }

    private fun setupInteractions() {
        binding.etSellerSearch.addTextChangedListener {
            currentQuery = it?.toString().orEmpty()
            loadProducts()
        }
    }

    private fun loadSupplier() {
        showInlineLoading(R.string.loading_supplier_catalog)
        AppData.loadSuppliers(
            searchQuery = supplierName,
            cityFilter = "",
            forceRefresh = true,
            onSuccess = { suppliers ->
                hideInlineLoading()
                supplier = suppliers.firstOrNull { it.name.equals(supplierName, ignoreCase = true) }
                    ?: AppData.findSupplierByName(supplierName)
                if (supplier == null) {
                    Toast.makeText(this, R.string.supplier_not_found, Toast.LENGTH_SHORT).show()
                    finish()
                    return@loadSuppliers
                }
                bindSupplier(requireNotNull(supplier))
                updateOrderProgress()
            },
            onError = { message ->
                hideInlineLoading()
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun bindSupplier(item: Supplier) {
        binding.tvSupplierName.text = item.name
        binding.tvLocation.text = item.location
        binding.tvDeliveryTime.text = item.deliveryTime
        binding.tvMinAmount.text = CurrencyFormatter.format(item.minimumAmount)
        binding.tvMinQty.text = getString(R.string.supplier_items_count_format, item.minimumQuantity)
        binding.tvVerified.visibility = if (item.isVerified) android.view.View.VISIBLE else android.view.View.GONE

        binding.layoutCategories.removeAllViews()
        item.categories.ifEmpty { listOf(getString(R.string.general_category)) }.forEach { category ->
            val chip = Chip(this).apply {
                text = category
                textSize = 12f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                isCheckable = false
                isClickable = true
                isFocusable = true
                chipMinHeight = 36f
                chipStartPadding = 12f
                chipEndPadding = 12f
                textStartPadding = 0f
                textEndPadding = 0f
                shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                    .setAllCornerSizes(18f)
                    .build()
                chipStrokeWidth = 0f
                setOnClickListener {
                    selectedCategoryName = if (selectedCategoryName.equals(category, ignoreCase = true)) {
                        ""
                    } else {
                        category
                    }
                    bindSupplier(item)
                    loadProducts()
                }
            }
            val isSelected = selectedCategoryName.equals(category, ignoreCase = true)
            chip.setTextColor(
                ContextCompat.getColor(
                    this,
                    if (isSelected) R.color.white else R.color.primary
                )
            )
            chip.chipBackgroundColor = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this,
                    if (isSelected) R.color.primary else R.color.status_transit_bg
                )
            )
            binding.layoutCategories.addView(chip)
        }
    }

    private fun loadProducts() {
        showInlineLoading(R.string.loading_supplier_catalog)
        AppData.loadSupplierProducts(
            supplierName = supplierName,
            query = currentQuery,
            categoryName = selectedCategoryName,
            forceRefresh = supplier == null,
            onSuccess = { products ->
                hideInlineLoading()
                productAdapter = SupplierProductAdapter(products, supplierName, ::onProductQuantityChanged)
                binding.rvProducts.adapter = productAdapter
            },
            onError = { message ->
                hideInlineLoading()
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun onProductQuantityChanged(product: Product, delta: Int) {
        val wasMinimumMet = CartManager.getSupplierCart(supplierName)?.isMinimumMet == true
        if (delta > 0) {
            CartManager.addItem(
                CartItem(
                    id = product.id,
                    name = product.name,
                    supplier = supplierName,
                    price = product.price,
                    quantity = 1,
                    imageUrl = product.imageUrl,
                    offerPrice = product.offerPrice,
                    maximumOfferQuantity = product.maximumOfferQuantity
                )
            )
        } else {
            CartManager.decrementItem(product.id, supplierName)
        }

        updateOrderProgress()
        val selectedCart = CartManager.getSupplierCart(supplierName)
        if (delta > 0 && !wasMinimumMet && selectedCart?.isMinimumMet == true) {
            Toast.makeText(this, R.string.order_minimum_ready_opening_cart, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, CartActivity::class.java).apply {
                putExtra(CartActivity.EXTRA_SUPPLIER_NAME, supplierName)
            })
        } else if (delta > 0) {
            Toast.makeText(this, R.string.added_to_cart, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateOrderProgress() {
        val supplierCart = CartManager.getSupplierCart(supplierName)
        renderProgress(supplierCart)
    }

    private fun renderProgress(selectedCart: SupplierCart?) {
        if (selectedCart == null) {
            binding.tvOrderProgressSubtitle.text = getString(R.string.complete_minimum_order_requirements)
            binding.tvAmountProgress.text = getString(
                R.string.cart_progress_amount_format,
                CurrencyFormatter.format(0.0),
                CurrencyFormatter.format(0.0)
            )
            binding.tvQuantityProgress.text = getString(R.string.cart_quantity_progress_format, 0, 0)
            binding.tvAmountRemaining.text = getString(R.string.cart_empty_message)
            binding.tvQuantityRemaining.text = getString(R.string.cart_empty_message)
            binding.pbAmount.progress = 0
            binding.pbQuantity.progress = 0
            return
        }

        binding.tvOrderProgressSubtitle.text = selectedCart.supplierName
        binding.tvAmountProgress.text = getString(
            R.string.cart_progress_amount_format,
            CurrencyFormatter.format(selectedCart.subtotal),
            CurrencyFormatter.format(selectedCart.minimumAmount)
        )
        binding.tvQuantityProgress.text = getString(
            R.string.cart_quantity_progress_format,
            selectedCart.totalQuantity,
            selectedCart.minimumQuantity
        )
        binding.tvAmountRemaining.text = if (selectedCart.remainingAmount > 0.0) {
            getString(R.string.cart_add_more_amount_format, CurrencyFormatter.format(selectedCart.remainingAmount))
        } else {
            getString(R.string.minimum_amount_reached)
        }
        binding.tvQuantityRemaining.text = if (selectedCart.remainingQuantity > 0) {
            getString(R.string.cart_add_more_quantity_format, selectedCart.remainingQuantity)
        } else {
            getString(R.string.minimum_quantity_reached)
        }
        binding.pbAmount.progress = calculateProgress(selectedCart.subtotal, selectedCart.minimumAmount)
        binding.pbQuantity.progress = calculateProgress(
            selectedCart.totalQuantity.toDouble(),
            selectedCart.minimumQuantity.toDouble()
        )
    }

    private fun calculateProgress(currentValue: Double, minimumValue: Double): Int {
        if (minimumValue <= 0.0) return if (currentValue > 0.0) 100 else 0
        val progress = (currentValue / minimumValue) * 100
        return if (progress.isFinite()) progress.toInt().coerceIn(0, 100) else 0
    }

    private fun showInlineLoading(@StringRes messageRes: Int) {
        activeLoadingCount += 1
        binding.layoutLoadingOverlay.visibility = android.view.View.VISIBLE
        binding.tvInlineLoadingMessage.setText(messageRes)
    }

    private fun hideInlineLoading() {
        activeLoadingCount = (activeLoadingCount - 1).coerceAtLeast(0)
        if (activeLoadingCount == 0) {
            binding.layoutLoadingOverlay.visibility = android.view.View.GONE
        }
    }

    override fun onDestroy() {
        activeLoadingCount = 0
        super.onDestroy()
    }
}
