package com.hiskytechs.muhallinewuserapp.supplier.Ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.Ui.AppLoadingDialog
import com.hiskytechs.muhallinewuserapp.Ui.loadMarketplaceImage
import com.hiskytechs.muhallinewuserapp.databinding.ActivitySupplierAddProductBinding
import com.hiskytechs.muhallinewuserapp.network.BackgroundWork
import com.hiskytechs.muhallinewuserapp.supplier.Adapters.SupplierCatalogProductAdapter
import com.hiskytechs.muhallinewuserapp.supplier.Adapters.SupplierCategoryAdapter
import com.hiskytechs.muhallinewuserapp.supplier.Data.SupplierData
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierCatalogProduct
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierCategory
import com.hiskytechs.muhallinewuserapp.supplier.Utill.initials

class SupplierAddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierAddProductBinding
    private lateinit var loadingDialog: AppLoadingDialog
    private lateinit var categoryAdapter: SupplierCategoryAdapter
    private lateinit var catalogProductAdapter: SupplierCatalogProductAdapter
    private var selectedCategory: SupplierCategory? = null
    private var selectedProduct: SupplierCatalogProduct? = null
    private var currentStep = 1
    private var selectedImageDataUrl: String? = null

    private val editProductId: String?
        get() = intent.getStringExtra(EXTRA_EDIT_PRODUCT_ID)
    private val isEditMode: Boolean
        get() = !editProductId.isNullOrBlank()

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@registerForActivityResult
        binding.ivSelectedProductImage.loadMarketplaceImage(uri.toString())
        binding.tvSelectedProductThumb.alpha = 0f
        binding.tvSelectedImageStatus.text = getString(R.string.supplier_upload_product_image)
        binding.btnSaveProduct.isEnabled = false
        val resolver = contentResolver
        val mimeType = resolver.getType(uri).orEmpty().ifBlank { "image/jpeg" }
        BackgroundWork.run(
            task = { uri.toDataUrl(mimeType) },
            onSuccess = { dataUrl ->
                selectedImageDataUrl = dataUrl
                binding.btnSaveProduct.isEnabled = true
            },
            onError = {
                selectedImageDataUrl = null
                binding.btnSaveProduct.isEnabled = true
                Toast.makeText(this, R.string.supplier_bulk_file_read_failed, Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = AppLoadingDialog(this)
        if (isEditMode) {
            binding.tvAddProductTitle.text = getString(R.string.supplier_edit_product)
            binding.btnSaveProduct.text = getString(R.string.supplier_update)
        }

        setupCategoryStep()
        setupProductStep()
        setupPricingStep()
        loadCatalogData()
        binding.btnStickyNext.setOnClickListener {
            when (currentStep) {
                1 -> goToProductStep()
                2 -> goToPricingStep()
            }
        }

        binding.ivBack.setOnClickListener {
            when (currentStep) {
                3 -> {
                    if (isEditMode) {
                        finish()
                        return@setOnClickListener
                    }
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
            goToProductStep()
        }
    }

    private fun setupProductStep() {
        catalogProductAdapter = SupplierCatalogProductAdapter(emptyList()) { product ->
            selectedProduct = product
            bindSelectedProduct(product)
            selectedImageDataUrl = null
            binding.tvSelectedImageStatus.text = getString(R.string.supplier_product_image_helper)
        }
        binding.rvCatalogProducts.layoutManager = LinearLayoutManager(this)
        binding.rvCatalogProducts.adapter = catalogProductAdapter
        binding.etSearchCatalog.addTextChangedListener { loadCatalogProducts() }
        binding.tvChangeCategory.setOnClickListener {
            currentStep = 1
            renderStep()
        }
        binding.btnNextProduct.setOnClickListener {
            goToPricingStep()
        }
    }

    private fun setupPricingStep() {
        binding.btnChooseProductImage.setOnClickListener {
            imagePicker.launch("image/*")
        }

        binding.btnSaveProduct.setOnClickListener {
            val product = selectedProduct ?: run {
                Toast.makeText(this, getString(R.string.supplier_select_product_first), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val price = binding.etPrice.text?.toString()?.trim().orEmpty()
            val stock = binding.etStock.text?.toString()?.trim().orEmpty()
            val deliveryDays = binding.etDeliveryDays.text?.toString()?.trim().orEmpty()
            val offerPrice = binding.etOfferPrice.text?.toString()?.trim().orEmpty()
            val offerMaxQuantity = binding.etOfferMaxQuantity.text?.toString()?.trim().orEmpty()
            clearPricingErrors()

            val parsedPrice = price.toIntOrNull()
            val parsedStock = stock.toIntOrNull()
            val parsedOfferPrice = offerPrice.toIntOrNull()
            val parsedOfferMaxQuantity = offerMaxQuantity.toIntOrNull()
            var hasError = false
            if (parsedPrice == null || parsedPrice <= 0) {
                binding.etPrice.error = getString(R.string.supplier_price_required)
                hasError = true
            }
            if (parsedStock == null || parsedStock < 0) {
                binding.etStock.error = getString(R.string.supplier_stock_required)
                hasError = true
            }
            if (deliveryDays.isEmpty()) {
                binding.etDeliveryDays.error = getString(R.string.required_field)
                hasError = true
            }
            if (offerPrice.isNotEmpty()) {
                if (parsedOfferPrice == null || parsedPrice == null || parsedOfferPrice <= 0 || parsedOfferPrice >= parsedPrice) {
                    binding.etOfferPrice.error = getString(R.string.supplier_offer_price_error)
                    hasError = true
                }
                if (parsedOfferMaxQuantity == null || parsedOfferMaxQuantity <= 0) {
                    binding.etOfferMaxQuantity.error = getString(R.string.supplier_offer_quantity_error)
                    hasError = true
                }
            } else if (offerMaxQuantity.isNotEmpty()) {
                binding.etOfferPrice.error = getString(R.string.supplier_offer_price_needed)
                hasError = true
            }
            if (hasError) {
                return@setOnClickListener
            }

            binding.btnSaveProduct.isEnabled = false
            loadingDialog.show(R.string.loading_saving_product)
            val onSuccess = {
                loadingDialog.dismiss()
                Toast.makeText(
                    this,
                    getString(if (isEditMode) R.string.supplier_product_updated else R.string.supplier_product_saved),
                    Toast.LENGTH_SHORT
                ).show()
                SupplierMainActivity.open(this, R.id.nav_supplier_products)
                finish()
            }
            val onError = { message: String ->
                binding.btnSaveProduct.isEnabled = true
                loadingDialog.dismiss()
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }

            val productId = editProductId
            if (isEditMode && !productId.isNullOrBlank()) {
                SupplierData.updateProductDetails(
                    productId = productId,
                    catalogProductId = product.id,
                    pricePkr = requireNotNull(parsedPrice),
                    stock = requireNotNull(parsedStock),
                    deliveryDays = deliveryDays,
                    imageDataUrl = selectedImageDataUrl,
                    offerPricePkr = parsedOfferPrice,
                    maximumOfferQuantity = parsedOfferMaxQuantity,
                    isActive = binding.switchAvailable.isChecked,
                    onSuccess = onSuccess,
                    onError = onError
                )
            } else {
                SupplierData.addProduct(
                    catalogProductId = product.id,
                    pricePkr = requireNotNull(parsedPrice),
                    stock = requireNotNull(parsedStock),
                    deliveryDays = deliveryDays,
                    imageDataUrl = selectedImageDataUrl,
                    offerPricePkr = parsedOfferPrice,
                    maximumOfferQuantity = parsedOfferMaxQuantity,
                    isActive = binding.switchAvailable.isChecked,
                    onSuccess = onSuccess,
                    onError = onError
                )
            }
        }
    }

    private fun clearPricingErrors() {
        binding.etPrice.error = null
        binding.etStock.error = null
        binding.etDeliveryDays.error = null
        binding.etOfferPrice.error = null
        binding.etOfferMaxQuantity.error = null
    }

    private fun loadCatalogProducts() {
        val categoryId = selectedCategory?.id ?: return
        catalogProductAdapter.updateItems(
            SupplierData.getCatalogProducts(categoryId, binding.etSearchCatalog.text?.toString().orEmpty())
        )
        binding.rvCatalogProducts.scrollToPosition(0)
    }

    private fun renderStep() {
        binding.tvStepLabel.text = getString(R.string.supplier_step_format, currentStep)
        binding.layoutStepCategory.visibility = if (currentStep == 1) View.VISIBLE else View.GONE
        binding.layoutStepProduct.visibility = if (currentStep == 2) View.VISIBLE else View.GONE
        binding.layoutStepPricing.visibility = if (currentStep == 3) View.VISIBLE else View.GONE
        binding.btnStickyNext.visibility = if (currentStep == 3) View.GONE else View.VISIBLE
        binding.btnStickyNext.text = getString(
            if (currentStep == 1) {
                R.string.supplier_next_choose_product
            } else {
                R.string.supplier_next_set_pricing
            }
        )
        binding.scrollAddProduct.post { binding.scrollAddProduct.smoothScrollTo(0, 0) }
    }

    private fun loadCatalogData() {
        loadingDialog.show(R.string.loading_supplier_catalog)
        val onSuccess = {
            loadingDialog.dismiss()
            categoryAdapter.updateItems(SupplierData.getCategories())
            if (isEditMode) {
                prefillEditProduct()
            } else {
                renderStep()
            }
        }
        val onError = { message: String ->
            loadingDialog.dismiss()
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            renderStep()
        }
        if (isEditMode) {
            SupplierData.refreshProducts(onSuccess = onSuccess, onError = onError)
        } else {
            SupplierData.refreshCatalog(onSuccess = onSuccess, onError = onError)
        }
    }

    private fun goToProductStep() {
        if (selectedCategory == null) {
            Toast.makeText(this, getString(R.string.supplier_select_category_first), Toast.LENGTH_SHORT).show()
            return
        }
        currentStep = 2
        renderStep()
    }

    private fun goToPricingStep() {
        if (selectedProduct == null) {
            Toast.makeText(this, getString(R.string.supplier_select_product_first), Toast.LENGTH_SHORT).show()
            return
        }
        currentStep = 3
        renderStep()
    }

    private fun prefillEditProduct() {
        val product = editProductId?.let(SupplierData::findProduct)
        if (product == null) {
            Toast.makeText(this, getString(R.string.supplier_product_not_found), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        selectedProduct = SupplierData.findCatalogProduct(product.catalogProductId)
        selectedCategory = selectedProduct?.let { SupplierData.findCategory(it.categoryId) }
            ?: SupplierData.getCategories().firstOrNull { it.name == product.categoryName }

        selectedCategory?.let { category ->
            categoryAdapter.selectedCategoryId = category.id
            categoryAdapter.updateItems(SupplierData.getCategories())
            binding.tvSelectedCategory.text = category.name
        }

        selectedProduct?.let { catalogProduct ->
            catalogProductAdapter.selectedProductId = catalogProduct.id
            bindSelectedProduct(catalogProduct, product.imageUrl)
        } ?: run {
            selectedProduct = SupplierCatalogProduct(
                id = product.catalogProductId,
                categoryId = selectedCategory?.id.orEmpty(),
                name = product.name,
                unitLabel = product.unitLabel,
                packaging = "",
                imageUrl = product.imageUrl,
                accentColorRes = product.accentColorRes
            )
            bindSelectedProduct(requireNotNull(selectedProduct), product.imageUrl)
        }

        binding.etPrice.setText(product.pricePkr.toString())
        binding.etStock.setText(product.stock.toString())
        binding.etDeliveryDays.setText(product.deliveryDays)
        binding.etOfferPrice.setText(if (product.offerPricePkr > 0) product.offerPricePkr.toString() else "")
        binding.etOfferMaxQuantity.setText(
            if (product.maximumOfferQuantity > 0) product.maximumOfferQuantity.toString() else ""
        )
        binding.switchAvailable.isChecked = product.isActive
        binding.tvSelectedImageStatus.text = getString(R.string.supplier_product_image_helper)
        currentStep = 3
        renderStep()
    }

    private fun bindSelectedProduct(product: SupplierCatalogProduct, fallbackImageUrl: String = product.imageUrl) {
        val imageUrl = fallbackImageUrl.ifBlank { product.imageUrl }
        binding.tvSelectedProductThumb.text = initials(product.name)
        binding.tvSelectedProductName.text = product.name
        binding.tvSelectedProductMeta.text = "${selectedCategory?.name.orEmpty()}   ${product.unitLabel}"
        binding.ivSelectedProductImage.loadMarketplaceImage(imageUrl)
        binding.tvSelectedProductThumb.alpha = if (imageUrl.isBlank()) 1f else 0f
    }

    private fun Uri.toDataUrl(mimeType: String): String {
        val bytes = contentResolver.openInputStream(this)?.use { it.readBytes() } ?: ByteArray(0)
        if (bytes.isEmpty()) throw IllegalStateException("Image file is empty.")
        return "data:$mimeType;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    override fun onDestroy() {
        loadingDialog.dismiss()
        super.onDestroy()
    }

    companion object {
        private const val EXTRA_EDIT_PRODUCT_ID = "extra_edit_product_id"

        fun openEdit(context: Context, productId: String) {
            context.startActivity(
                Intent(context, SupplierAddProductActivity::class.java)
                    .putExtra(EXTRA_EDIT_PRODUCT_ID, productId)
            )
        }
    }
}
