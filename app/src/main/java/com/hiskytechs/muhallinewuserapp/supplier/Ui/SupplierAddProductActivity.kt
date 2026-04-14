package com.hiskytechs.muhallinewuserapp.supplier.Ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.databinding.ActivitySupplierAddProductBinding
import com.hiskytechs.muhallinewuserapp.supplier.Adapters.SupplierCatalogProductAdapter
import com.hiskytechs.muhallinewuserapp.supplier.Adapters.SupplierCategoryAdapter
import com.hiskytechs.muhallinewuserapp.supplier.Data.SupplierData
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierCatalogProduct
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierCategory
import com.hiskytechs.muhallinewuserapp.supplier.Utill.initials

class SupplierAddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierAddProductBinding
    private lateinit var categoryAdapter: SupplierCategoryAdapter
    private lateinit var catalogProductAdapter: SupplierCatalogProductAdapter
    private var selectedCategory: SupplierCategory? = null
    private var selectedProduct: SupplierCatalogProduct? = null
    private var currentStep = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCategoryStep()
        setupProductStep()
        setupPricingStep()
        loadCatalogData()

        binding.ivBack.setOnClickListener {
            when (currentStep) {
                3 -> {
                    currentStep = 2
                    renderStep()
                }
                2 -> {
                    currentStep = 1
                    renderStep()
                }
                else -> finish()
            }
        }
    }

    private fun setupCategoryStep() {
        categoryAdapter = SupplierCategoryAdapter(SupplierData.getCategories()) { category ->
            selectedCategory = category
            binding.tvSelectedCategory.text = category.name
            selectedProduct = null
            catalogProductAdapter.selectedProductId = null
            loadCatalogProducts()
        }
        binding.rvCategories.layoutManager = GridLayoutManager(this, 2)
        binding.rvCategories.adapter = categoryAdapter
        binding.btnNextCategory.setOnClickListener {
            if (selectedCategory == null) {
                Toast.makeText(this, getString(R.string.supplier_select_category_first), Toast.LENGTH_SHORT).show()
            } else {
                currentStep = 2
                renderStep()
            }
        }
    }

    private fun setupProductStep() {
        catalogProductAdapter = SupplierCatalogProductAdapter(emptyList()) { product ->
            selectedProduct = product
            binding.tvSelectedProductThumb.text = initials(product.name)
            binding.tvSelectedProductName.text = product.name
            binding.tvSelectedProductMeta.text = "${selectedCategory?.name.orEmpty()}   ${product.unitLabel}"
        }
        binding.rvCatalogProducts.layoutManager = LinearLayoutManager(this)
        binding.rvCatalogProducts.adapter = catalogProductAdapter
        binding.etSearchCatalog.addTextChangedListener { loadCatalogProducts() }
        binding.tvChangeCategory.setOnClickListener {
            currentStep = 1
            renderStep()
        }
        binding.btnNextProduct.setOnClickListener {
            if (selectedProduct == null) {
                Toast.makeText(this, getString(R.string.supplier_select_product_first), Toast.LENGTH_SHORT).show()
            } else {
                currentStep = 3
                renderStep()
            }
        }
    }

    private fun setupPricingStep() {
        binding.btnSaveProduct.setOnClickListener {
            val product = selectedProduct ?: run {
                Toast.makeText(this, getString(R.string.supplier_select_product_first), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val price = binding.etPrice.text?.toString()?.trim().orEmpty()
            val stock = binding.etStock.text?.toString()?.trim().orEmpty()
            val deliveryDays = binding.etDeliveryDays.text?.toString()?.trim().orEmpty()
            if (price.isEmpty() || stock.isEmpty() || deliveryDays.isEmpty()) {
                Toast.makeText(this, getString(R.string.supplier_fill_all_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            SupplierData.addProduct(
                catalogProductId = product.id,
                pricePkr = price.toInt(),
                stock = stock.toInt(),
                deliveryDays = deliveryDays,
                isActive = binding.switchAvailable.isChecked,
                onSuccess = {
                    Toast.makeText(this, getString(R.string.supplier_product_saved), Toast.LENGTH_SHORT).show()
                    SupplierMainActivity.open(this, R.id.nav_supplier_products)
                    finish()
                },
                onError = { message ->
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun loadCatalogProducts() {
        val categoryId = selectedCategory?.id ?: return
        catalogProductAdapter.updateItems(
            SupplierData.getCatalogProducts(categoryId, binding.etSearchCatalog.text?.toString().orEmpty())
        )
    }

    private fun renderStep() {
        binding.tvStepLabel.text = getString(R.string.supplier_step_format, currentStep)
        binding.layoutStepCategory.visibility = if (currentStep == 1) android.view.View.VISIBLE else android.view.View.GONE
        binding.layoutStepProduct.visibility = if (currentStep == 2) android.view.View.VISIBLE else android.view.View.GONE
        binding.layoutStepPricing.visibility = if (currentStep == 3) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun loadCatalogData() {
        SupplierData.refreshProducts(
            onSuccess = {
                categoryAdapter.updateItems(SupplierData.getCategories())
                renderStep()
            },
            onError = { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                renderStep()
            }
        )
    }
}
