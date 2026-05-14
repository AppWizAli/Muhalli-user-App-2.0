package com.hiskytechs.muhallinewuserapp.Fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.hiskytechs.muhallinewuserapp.Adapters.OfferAdapter
import com.hiskytechs.muhallinewuserapp.Adapters.ProductSearchAdapter
import com.hiskytechs.muhallinewuserapp.Adapters.SupplierAdapter
import com.hiskytechs.muhallinewuserapp.Data.AppData
import com.hiskytechs.muhallinewuserapp.Models.CartItem
import com.hiskytechs.muhallinewuserapp.Models.MarketplaceOffer
import com.hiskytechs.muhallinewuserapp.Models.Product
import com.hiskytechs.muhallinewuserapp.Models.Supplier
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.Ui.CartActivity
import com.hiskytechs.muhallinewuserapp.Ui.MapActivity
import com.hiskytechs.muhallinewuserapp.Ui.NotificationsActivity
import com.hiskytechs.muhallinewuserapp.Ui.ReferralActivity
import com.hiskytechs.muhallinewuserapp.Ui.SupplierDetailsActivity
import com.hiskytechs.muhallinewuserapp.Utill.CartManager
import com.hiskytechs.muhallinewuserapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var supplierAdapter: SupplierAdapter
    private lateinit var offerAdapter: OfferAdapter
    private lateinit var productSearchAdapter: ProductSearchAdapter
    private var activeLoadingCount = 0
    private var currentSort = SORT_DEFAULT
    private var currentSearch = ""
    private var nextSupplierLoadForceRefresh = false
    private val searchHandler = Handler(Looper.getMainLooper())
    private var pendingSearchLoad: Runnable? = null
    private var skippedInitialResume = false

    private val mapPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
            val selectedCity = result.data?.getStringExtra(MapActivity.EXTRA_SELECTED_CITY).orEmpty()
            if (selectedCity.isBlank()) return@registerForActivityResult
            val selectedAddress = result.data?.getStringExtra(MapActivity.EXTRA_SELECTED_ADDRESS).orEmpty()
            val selectedLatitude = if (result.data?.hasExtra(MapActivity.EXTRA_SELECTED_LATITUDE) == true) {
                result.data?.getDoubleExtra(MapActivity.EXTRA_SELECTED_LATITUDE, 0.0)
            } else {
                null
            }
            val selectedLongitude = if (result.data?.hasExtra(MapActivity.EXTRA_SELECTED_LONGITUDE) == true) {
                result.data?.getDoubleExtra(MapActivity.EXTRA_SELECTED_LONGITUDE, 0.0)
            } else {
                null
            }
            binding.tvActiveCity.text = selectedCity
            val updatedProfile = AppData.buyerProfile.copy(
                city = selectedCity,
                address = selectedAddress.ifBlank { selectedCity },
                latitude = selectedLatitude,
                longitude = selectedLongitude
            )
            showInlineLoading(R.string.loading_location_update)
            AppData.updateBuyerProfile(
                updatedProfile = updatedProfile,
                onSuccess = {
                    hideInlineLoading()
                    loadOffers()
                    loadSuppliers()
                },
                onError = { message ->
                    hideInlineLoading()
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            )
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        setupInteractions()
        loadProfileAndContent()
    }

    override fun onResume() {
        super.onResume()
        if (!skippedInitialResume) {
            skippedInitialResume = true
            return
        }
        if (_binding != null) {
            loadOffers()
            loadSuppliers()
            loadReferralSummary()
        }
    }

    private fun setupRecyclerViews() {
        supplierAdapter = SupplierAdapter(emptyList())
        binding.rvSuppliers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSuppliers.setHasFixedSize(true)
        binding.rvSuppliers.adapter = supplierAdapter

        productSearchAdapter = ProductSearchAdapter(
            emptyList(),
            onAddClick = ::addProductToCart,
            onOpenSupplier = { product ->
                startActivity(Intent(requireContext(), SupplierDetailsActivity::class.java).apply {
                    putExtra("supplier_name", product.supplierName)
                    putExtra("location", binding.tvActiveCity.text?.toString().orEmpty())
                })
            }
        )
        binding.rvSearchProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSearchProducts.setHasFixedSize(true)
        binding.rvSearchProducts.adapter = productSearchAdapter

        offerAdapter = OfferAdapter(emptyList(), ::onOfferClicked)
        binding.rvOffers.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvOffers.setHasFixedSize(true)
        binding.rvOffers.adapter = offerAdapter
    }

    private fun setupInteractions() {
        binding.etSearch.addTextChangedListener {
            currentSearch = it?.toString().orEmpty()
            scheduleSupplierLoad()
        }
        binding.swipeRefresh.setOnRefreshListener {
            loadProfileAndContent()
        }
        binding.ivHomeNotifications.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationsActivity::class.java))
        }
        binding.btnOpenReferral.setOnClickListener {
            startActivity(Intent(requireContext(), ReferralActivity::class.java))
        }
        binding.cardReferral.setOnClickListener {
            startActivity(Intent(requireContext(), ReferralActivity::class.java))
        }
        binding.btnPickCity.setOnClickListener {
            mapPickerLauncher.launch(
                Intent(requireContext(), MapActivity::class.java).apply {
                    putExtra(MapActivity.EXTRA_MODE, MapActivity.MODE_PICK)
                    putExtra(MapActivity.EXTRA_CITY, binding.tvActiveCity.text?.toString().orEmpty())
                }
            )
        }
        binding.btnDefaultSort.setOnClickListener {
            currentSort = SORT_DEFAULT
            updateSortButtons()
            loadSuppliers()
        }
        binding.btnCheapest.setOnClickListener {
            currentSort = SORT_CHEAPEST
            updateSortButtons()
            loadSuppliers()
        }
        binding.btnMinOrder.setOnClickListener {
            currentSort = SORT_MIN_ORDER
            updateSortButtons()
            loadSuppliers()
        }
        updateSortButtons()
    }

    private fun loadProfileAndContent() {
        showInlineLoading(R.string.loading_profile_details)
        AppData.loadBuyerProfile(
            onSuccess = { profile ->
                if (_binding == null) return@loadBuyerProfile
                hideInlineLoading()
                binding.tvActiveCity.text = profile.city.ifBlank {
                    getString(R.string.default_city_name)
                }
                nextSupplierLoadForceRefresh = true
                loadOffers()
                loadSuppliers()
                loadReferralSummary()
            },
            onError = {
                if (_binding == null) return@loadBuyerProfile
                hideInlineLoading()
                binding.tvActiveCity.text = getString(R.string.default_city_name)
                nextSupplierLoadForceRefresh = true
                loadOffers()
                loadSuppliers()
                loadReferralSummary()
            }
        )
    }

    private fun loadOffers() {
        showInlineLoading()
        AppData.loadOffers(
            cityFilter = binding.tvActiveCity.text?.toString().orEmpty(),
            onSuccess = { offers ->
                if (_binding == null) return@loadOffers
                hideInlineLoading()
                binding.swipeRefresh.isRefreshing = false
                offerAdapter.updateItems(offers)
            },
            onError = { message ->
                if (_binding == null) return@loadOffers
                hideInlineLoading()
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun loadSuppliers() {
        if (currentSearch.isNotBlank()) {
            loadProductResults()
            return
        }

        binding.tvSearchResultsTitle.visibility = View.GONE
        binding.rvSearchProducts.visibility = View.GONE
        binding.rvSuppliers.visibility = View.VISIBLE
        val requestSearch = currentSearch
        val forceRefresh = nextSupplierLoadForceRefresh
        nextSupplierLoadForceRefresh = false
        showInlineLoading()
        AppData.loadHomeSuppliers(
            searchQuery = requestSearch,
            cityFilter = binding.tvActiveCity.text?.toString().orEmpty(),
            sort = currentSort,
            forceRefresh = forceRefresh,
            onSuccess = { suppliers ->
                if (_binding == null) return@loadHomeSuppliers
                if (requestSearch != currentSearch) {
                    hideInlineLoading()
                    return@loadHomeSuppliers
                }
                hideInlineLoading()
                binding.swipeRefresh.isRefreshing = false
                renderSuppliers(suppliers)
            },
            onError = { message ->
                if (_binding == null) return@loadHomeSuppliers
                hideInlineLoading()
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun loadProductResults() {
        val requestSearch = currentSearch
        showInlineLoading(R.string.loading_search_results)
        AppData.loadProductSearchResults(
            searchQuery = requestSearch,
            cityFilter = binding.tvActiveCity.text?.toString().orEmpty(),
            forceRefresh = nextSupplierLoadForceRefresh.also { nextSupplierLoadForceRefresh = false },
            onSuccess = { products ->
                if (_binding == null) return@loadProductSearchResults
                if (requestSearch != currentSearch) {
                    hideInlineLoading()
                    return@loadProductSearchResults
                }
                hideInlineLoading()
                binding.swipeRefresh.isRefreshing = false
                binding.tvSearchResultsTitle.visibility = View.VISIBLE
                binding.rvSearchProducts.visibility = View.VISIBLE
                binding.rvSuppliers.visibility = View.GONE
                binding.tvSearchResultsTitle.text = getString(
                    R.string.search_results_title_format,
                    requestSearch,
                    products.size
                )
                productSearchAdapter.updateItems(products)
                binding.tvSupplierCount.text = getString(
                    R.string.search_sellers_count_format,
                    products.size
                )
            },
            onError = { message ->
                if (_binding == null) return@loadProductSearchResults
                hideInlineLoading()
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun onOfferClicked(offer: MarketplaceOffer) {
        AppData.resolveOfferProduct(
            offer = offer,
            onSuccess = { product ->
                if (_binding == null) return@resolveOfferProduct
                val wasMinimumMet = CartManager.getSupplierCart(product.supplierName)?.isMinimumMet == true
                CartManager.addItem(
                    CartItem(
                        id = product.id,
                        name = product.name,
                        supplier = product.supplierName,
                        price = product.price,
                        quantity = 1,
                        imageUrl = product.imageUrl,
                        offerPrice = offer.offerPrice.takeIf { it > 0.0 } ?: product.offerPrice,
                        maximumOfferQuantity = offer.maximumQuantity.takeIf { it > 0 }
                            ?: product.maximumOfferQuantity
                    )
                )
                Toast.makeText(requireContext(), R.string.offer_added_to_cart, Toast.LENGTH_SHORT).show()

                val supplierCart = CartManager.getSupplierCart(product.supplierName)
                if (!wasMinimumMet && supplierCart?.isMinimumMet == true) {
                    startActivity(Intent(requireContext(), CartActivity::class.java).apply {
                        putExtra(CartActivity.EXTRA_SUPPLIER_NAME, product.supplierName)
                    })
                }
            },
            onError = { message ->
                if (_binding == null) return@resolveOfferProduct
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                if (offer.supplierName.isNotBlank()) {
                    startActivity(Intent(requireContext(), SupplierDetailsActivity::class.java).apply {
                        putExtra("supplier_name", offer.supplierName)
                        putExtra("location", offer.city)
                    })
                }
            }
        )
    }

    private fun addProductToCart(product: Product) {
        CartManager.addItem(
            CartItem(
                id = product.id,
                name = product.name,
                supplier = product.supplierName,
                price = product.price,
                quantity = 1,
                imageUrl = product.imageUrl,
                offerPrice = product.offerPrice,
                maximumOfferQuantity = product.maximumOfferQuantity
            )
        )
        Toast.makeText(requireContext(), R.string.added_to_cart, Toast.LENGTH_SHORT).show()
    }

    private fun renderSuppliers(suppliers: List<Supplier>) {
        supplierAdapter.updateItems(suppliers)
        binding.tvSupplierCount.text = getString(
            R.string.home_supplier_count_format,
            suppliers.size,
            binding.tvActiveCity.text?.toString().orEmpty()
        )
    }

    private fun loadReferralSummary() {
        AppData.loadReferralSummary(
            onSuccess = { summary ->
                if (_binding == null) return@loadReferralSummary
                binding.tvReferralHeadline.text = if (summary.referralCode.isBlank()) {
                    getString(R.string.referral_home_title)
                } else {
                    getString(R.string.referral_code_home_format, summary.referralCode)
                }
                binding.tvReferralSubline.text = getString(
                    R.string.referral_home_reward_format,
                    summary.totalClaims,
                    summary.rewardAmount
                )
            },
            onError = {
                if (_binding == null) return@loadReferralSummary
                binding.tvReferralHeadline.text = getString(R.string.referral_home_title)
                binding.tvReferralSubline.text = getString(R.string.referral_home_subtitle)
            }
        )
    }

    private fun showInlineLoading(@StringRes messageRes: Int = R.string.loading_message) {
        if (_binding == null) return
        activeLoadingCount += 1
        binding.layoutLoadingOverlay.visibility = View.VISIBLE
        binding.tvInlineLoadingMessage.setText(messageRes)
    }

    private fun hideInlineLoading() {
        if (_binding == null) return
        activeLoadingCount = (activeLoadingCount - 1).coerceAtLeast(0)
        if (activeLoadingCount == 0) {
            binding.layoutLoadingOverlay.visibility = View.GONE
        }
    }

    private fun updateSortButtons() {
        setSortState(binding.btnDefaultSort, currentSort == SORT_DEFAULT)
        setSortState(binding.btnCheapest, currentSort == SORT_CHEAPEST)
        setSortState(binding.btnMinOrder, currentSort == SORT_MIN_ORDER)
    }

    private fun scheduleSupplierLoad() {
        pendingSearchLoad?.let(searchHandler::removeCallbacks)
        pendingSearchLoad = Runnable {
            loadSuppliers()
        }.also { searchHandler.postDelayed(it, SEARCH_DEBOUNCE_MS) }
    }

    private fun setSortState(button: MaterialButton, isActive: Boolean) {
        button.alpha = if (isActive) 1f else 0.7f
        button.strokeWidth = if (isActive) 0 else 2
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pendingSearchLoad?.let(searchHandler::removeCallbacks)
        pendingSearchLoad = null
        activeLoadingCount = 0
        _binding = null
    }

    companion object {
        private const val SORT_DEFAULT = "default"
        private const val SORT_CHEAPEST = "cheapest"
        private const val SORT_MIN_ORDER = "low_min_order"
        private const val SEARCH_DEBOUNCE_MS = 350L
    }
}
