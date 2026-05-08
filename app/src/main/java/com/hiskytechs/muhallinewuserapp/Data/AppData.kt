package com.hiskytechs.muhallinewuserapp.Data

import androidx.annotation.StringRes
import com.hiskytechs.muhallinewuserapp.MuhalliApplication
import com.hiskytechs.muhallinewuserapp.Models.BuyerNotificationItem
import com.hiskytechs.muhallinewuserapp.Models.BuyerProfile
import com.hiskytechs.muhallinewuserapp.Models.CartItem
import com.hiskytechs.muhallinewuserapp.Models.Category
import com.hiskytechs.muhallinewuserapp.Models.ChatMessage
import com.hiskytechs.muhallinewuserapp.Models.ChatMessageType
import com.hiskytechs.muhallinewuserapp.Models.ChatParticipant
import com.hiskytechs.muhallinewuserapp.Models.ChatThread
import com.hiskytechs.muhallinewuserapp.Models.MarketplaceOffer
import com.hiskytechs.muhallinewuserapp.Models.Order
import com.hiskytechs.muhallinewuserapp.Models.Product
import com.hiskytechs.muhallinewuserapp.Models.PublicAppSettings
import com.hiskytechs.muhallinewuserapp.Models.ReferralClaim
import com.hiskytechs.muhallinewuserapp.Models.ReferralSummary
import com.hiskytechs.muhallinewuserapp.Models.Supplier
import com.hiskytechs.muhallinewuserapp.R
import android.content.Context
import com.hiskytechs.muhallinewuserapp.Utill.AddressManager
import com.hiskytechs.muhallinewuserapp.network.ApiClient
import com.hiskytechs.muhallinewuserapp.network.ApiException
import com.hiskytechs.muhallinewuserapp.network.ApiFormatting
import com.hiskytechs.muhallinewuserapp.network.AppSession
import com.hiskytechs.muhallinewuserapp.network.BackgroundWork
import com.hiskytechs.muhallinewuserapp.network.ApiConfig
import com.hiskytechs.muhallinewuserapp.network.CurrencyFormatter
import com.hiskytechs.muhallinewuserapp.notifications.AppNotificationHelper
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

object AppData {

    private val context
        get() = MuhalliApplication.instance

    var buyerProfile: BuyerProfile = BuyerProfile("", "", "", "", "", "")
        private set

    private var categoriesCache: List<Category> = emptyList()
    val categories: List<Category>
        get() = categoriesCache

    private var suppliersCache: List<Supplier> = emptyList()
    val suppliers: List<Supplier>
        get() = suppliersCache

    private var productsCache: List<Product> = emptyList()
    private var ordersCache: MutableList<Order> = mutableListOf()
    private var chatThreadsCache: List<ChatThread> = emptyList()
    private var offersCache: List<MarketplaceOffer> = emptyList()
    private var notificationsCache: List<BuyerNotificationItem> = emptyList()
    private var publicSettingsCache = PublicAppSettings("", "", "Karachi", "PKR")
    private var referralSummaryCache = ReferralSummary(false, "", 0.0, 0.0, 0, 0.0, emptyList())

    val chatThreads: List<ChatThread>
        get() = chatThreadsCache

    private val supplierByName = linkedMapOf<String, Supplier>()
    private val supplierIdToCategories = linkedMapOf<Int, List<String>>()
    private val threadMessagesCache = linkedMapOf<Int, MutableList<ChatMessage>>()
    private const val CACHE_PREFS = "muhalli_device_cache"
    private const val CACHE_PRODUCTS = "buyer_products"
    private const val CACHE_SUPPLIERS = "buyer_suppliers"
    private const val CACHE_OFFERS = "buyer_offers"
    private const val CACHE_NOTIFICATIONS = "buyer_notifications"
    private const val CACHE_PROFILE = "buyer_profile"
    private const val CACHE_PUBLIC_SETTINGS = "public_settings"
    private const val CACHE_REFERRAL = "buyer_referral"
    private const val CACHE_LAST_POPPED_NOTIFICATION_ID = "last_popped_notification_id"

    fun loadHomeSuppliers(
        searchQuery: String = "",
        cityFilter: String = buyerProfile.city,
        sort: String = "default",
        onSuccess: (List<Supplier>) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                ensureMarketplaceData()
                filterSuppliers(
                    categoryName = "",
                    searchQuery = searchQuery,
                    cityFilter = cityFilter,
                    sort = sort
                ).take(8)
            },
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun loadCategories(
        onSuccess: (List<Category>) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                categoriesCache = parseCategories(ApiClient.getDataArray("buyer/categories"))
                categoriesCache
            },
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun loadSuppliers(
        categoryName: String = "",
        searchQuery: String = "",
        cityFilter: String = buyerProfile.city,
        sort: String = "default",
        onSuccess: (List<Supplier>) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                ensureMarketplaceData()
                filterSuppliers(
                    categoryName = categoryName,
                    searchQuery = searchQuery,
                    cityFilter = cityFilter,
                    sort = sort
                )
            },
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun loadProductSearchResults(
        searchQuery: String,
        cityFilter: String = buyerProfile.city,
        onSuccess: (List<Product>) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                val normalizedSearch = searchQuery.trim()
                if (normalizedSearch.isBlank()) {
                    return@run emptyList()
                }

                ensureMarketplaceData()
                val normalizedCity = cityFilter.trim()
                productsCache.filter { product ->
                    val supplier = supplierByName[product.supplierName.lowercase(Locale.getDefault())]
                    product.stockQuantity > 0 &&
                        (normalizedCity.isBlank() || supplier?.location?.equals(normalizedCity, ignoreCase = true) == true) &&
                        (
                            product.name.contains(normalizedSearch, ignoreCase = true) ||
                                product.categoryName.contains(normalizedSearch, ignoreCase = true) ||
                                product.packaging.contains(normalizedSearch, ignoreCase = true) ||
                                product.supplierName.contains(normalizedSearch, ignoreCase = true)
                            )
                }
                    .sortedWith(
                        compareBy<Product> { it.displayPrice }
                            .thenBy { it.supplierName.lowercase(Locale.getDefault()) }
                            .thenBy { it.name.lowercase(Locale.getDefault()) }
                    )
                    .take(40)
            },
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun loadSupplierProducts(
        supplierName: String,
        query: String = "",
        categoryName: String = "",
        onSuccess: (List<Product>) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                ensureMarketplaceData()
                val supplier = supplierByName.values.firstOrNull {
                    it.name.equals(supplierName, ignoreCase = true)
                } ?: throw ApiException("Supplier not found.")

                val normalizedQuery = query.trim().lowercase(Locale.getDefault())
                val normalizedCategory = categoryName.trim().lowercase(Locale.getDefault())
                productsCache.filter { product ->
                    product.supplierId == supplier.id &&
                        (normalizedCategory.isBlank() ||
                            product.categoryName.lowercase(Locale.getDefault()) == normalizedCategory) &&
                        (normalizedQuery.isBlank() ||
                            product.name.lowercase(Locale.getDefault()).contains(normalizedQuery) ||
                            product.categoryName.lowercase(Locale.getDefault()).contains(normalizedQuery) ||
                            product.packaging.lowercase(Locale.getDefault()).contains(normalizedQuery))
                }
            },
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun resolveOfferProduct(
        offer: MarketplaceOffer,
        onSuccess: (Product) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                ensureMarketplaceData()
                productsCache.firstOrNull { product ->
                    offer.catalogProductId > 0 &&
                        product.catalogProductId == offer.catalogProductId &&
                        (offer.supplierId == 0 || product.supplierId == offer.supplierId)
                } ?: productsCache.firstOrNull { product ->
                    product.name.equals(offer.productName, ignoreCase = true) &&
                        (offer.supplierName.isBlank() ||
                            product.supplierName.equals(offer.supplierName, ignoreCase = true))
                } ?: throw ApiException(string(R.string.offer_product_not_available))
            },
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun loadOffers(
        cityFilter: String = buyerProfile.city,
        onSuccess: (List<MarketplaceOffer>) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                offersCache = runCatching {
                    ApiClient.getDataArray(
                        endpoint = "buyer/offers",
                        queryParams = mapOf("city" to cityFilter)
                    ).also { saveCacheArray(CACHE_OFFERS, it) }
                }.getOrElse { error ->
                    cachedArray(CACHE_OFFERS) ?: throw error
                }.let(::parseOffers)
                offersCache
            },
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun loadNotifications(
        onSuccess: (List<BuyerNotificationItem>) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                notificationsCache = runCatching {
                    ApiClient.getDataArray("buyer/notifications")
                        .also { saveCacheArray(CACHE_NOTIFICATIONS, it) }
                }.getOrElse { error ->
                    cachedArray(CACHE_NOTIFICATIONS) ?: throw error
                }.let(::parseNotifications)
                maybePopLatestNotification(notificationsCache)
                notificationsCache
            },
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun registerNotificationToken(firebaseToken: String, onError: (String) -> Unit = {}) {
        if (firebaseToken.isBlank() || (!AppSession.hasBuyerSession() && !AppSession.hasSupplierSession())) return
        BackgroundWork.run(
            task = {
                if (AppSession.hasBuyerSession()) {
                    ApiClient.postDataObject(
                        endpoint = "buyer/notifications/register-device",
                        bodyParams = mapOf(
                            "buyer_id" to AppSession.buyerId,
                            "firebase_token" to firebaseToken,
                            "platform" to "android"
                        )
                    )
                }
                if (AppSession.hasSupplierSession()) {
                    ApiClient.postDataObject(
                        endpoint = "supplier/notifications/register-device",
                        bodyParams = mapOf(
                            "supplier_id" to AppSession.supplierId,
                            "firebase_token" to firebaseToken,
                            "platform" to "android"
                        )
                    )
                }
            },
            onSuccess = {},
            onError = onError
        )
    }

    fun loadReferralSummary(
        onSuccess: (ReferralSummary) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                val payload = runCatching {
                    ApiClient.getDataObject("buyer/referrals")
                        .also { saveCacheObject(CACHE_REFERRAL, it) }
                }.getOrElse { error ->
                    cachedObject(CACHE_REFERRAL) ?: throw error
                }
                referralSummaryCache = parseReferralSummary(payload)
                referralSummaryCache
            },
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun applyReferralCode(
        referralCode: String,
        onSuccess: (ReferralSummary) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                ApiClient.postDataObject(
                    endpoint = "buyer/referrals/apply",
                    bodyParams = mapOf("referral_code" to referralCode.trim())
                )
                val updated = ApiClient.getDataObject("buyer/referrals")
                saveCacheObject(CACHE_REFERRAL, updated)
                referralSummaryCache = parseReferralSummary(updated)
                referralSummaryCache
            },
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun loadPublicSettings(
        onSuccess: (PublicAppSettings) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                val payload = runCatching {
                    ApiClient.getDataObject("settings/public")
                        .also { saveCacheObject(CACHE_PUBLIC_SETTINGS, it) }
                }.getOrElse { error ->
                    cachedObject(CACHE_PUBLIC_SETTINGS) ?: throw error
                }
                publicSettingsCache = parsePublicSettings(payload)
                publicSettingsCache
            },
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun loadOrders(
        onSuccess: (List<Order>) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                val orders = parseOrders(
                    ApiClient.getDataArray(
                        endpoint = "buyer/orders",
                        queryParams = mapOf("buyer_id" to AppSession.buyerId)
                    )
                ).toMutableList()
                ordersCache = orders
                ordersCache.toList()
            },
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun loadChats(
        onSuccess: (List<ChatThread>) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                ensureMarketplaceData()
                chatThreadsCache = parseThreads(
                    ApiClient.getDataArray(
                        endpoint = "buyer/chats",
                        queryParams = mapOf("buyer_id" to AppSession.buyerId)
                    )
                )
                chatThreadsCache
            },
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun loadConversation(
        threadId: Int,
        onSuccess: (List<ChatMessage>) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                val payload = ApiClient.getDataObject(
                    endpoint = "buyer/chats/thread",
                    queryParams = mapOf(
                        "buyer_id" to AppSession.buyerId,
                        "thread_id" to threadId
                    )
                )
                val messages = parseMessages(payload.optJSONArray("messages"))
                threadMessagesCache[threadId] = messages.toMutableList()
                messages
            },
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun startChatWithSupplier(
        supplierId: Int,
        onSuccess: (ChatThread) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                ensureMarketplaceData()
                val payload = ApiClient.postDataObject(
                    endpoint = "buyer/chats/start",
                    bodyParams = mapOf(
                        "buyer_id" to AppSession.buyerId,
                        "supplier_id" to supplierId
                    )
                )
                val thread = parseThread(payload)
                chatThreadsCache = (chatThreadsCache.filterNot { it.threadId == thread.threadId } + thread)
                    .sortedByDescending { it.lastSeen }
                threadMessagesCache[thread.threadId] = parseMessages(payload.optJSONArray("messages")).toMutableList()
                thread
            },
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun sendMessage(
        threadId: Int,
        message: String,
        onSuccess: (List<ChatMessage>) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                val payload = ApiClient.postDataObject(
                    endpoint = "buyer/chats/send",
                    bodyParams = mapOf(
                        "buyer_id" to AppSession.buyerId,
                        "thread_id" to threadId,
                        "message_body" to message,
                        "message_type" to "text"
                    )
                )
                val messages = parseMessages(payload.optJSONArray("messages"))
                threadMessagesCache[threadId] = messages.toMutableList()
                chatThreadsCache = chatThreadsCache.map { thread ->
                    if (thread.threadId == threadId) {
                        thread.copy(
                            lastMessage = message,
                            lastSeen = string(R.string.just_now),
                            unreadCount = 0
                        )
                    } else {
                        thread
                    }
                }
                messages
            },
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun loadBuyerProfile(
        onSuccess: (BuyerProfile) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                val payload = runCatching {
                    ApiClient.getDataObject(
                        endpoint = "buyer/profile",
                        queryParams = mapOf("buyer_id" to AppSession.buyerId)
                    ).also { saveCacheObject(CACHE_PROFILE, it) }
                }.getOrElse { error ->
                    cachedObject(CACHE_PROFILE) ?: throw error
                }
                buyerProfile = parseBuyerProfile(payload)
                buyerProfile
            },
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun updateBuyerProfile(
        updatedProfile: BuyerProfile,
        onSuccess: (BuyerProfile) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                ApiClient.postDataObject(
                    endpoint = "buyer/profile/update",
                    bodyParams = mapOf(
                        "buyer_id" to AppSession.buyerId,
                        "store_name" to updatedProfile.storeName.ifBlank { updatedProfile.buyerName },
                        "buyer_name" to updatedProfile.buyerName,
                        "email" to updatedProfile.email,
                        "phone" to updatedProfile.phoneNumber,
                        "city" to updatedProfile.city,
                        "address" to updatedProfile.address,
                        "latitude" to updatedProfile.latitude,
                        "longitude" to updatedProfile.longitude
                    )
                )
                buyerProfile = updatedProfile
                saveCacheObject(
                    CACHE_PROFILE,
                    JSONObject().apply {
                        put("store_name", updatedProfile.storeName.ifBlank { updatedProfile.buyerName })
                        put("buyer_name", updatedProfile.buyerName)
                        put("email", updatedProfile.email)
                        put("phone", updatedProfile.phoneNumber)
                        put("city", updatedProfile.city)
                        put("address", updatedProfile.address)
                        put("latitude", updatedProfile.latitude ?: JSONObject.NULL)
                        put("longitude", updatedProfile.longitude ?: JSONObject.NULL)
                        put("member_since", buyerProfile.memberSince)
                    }
                )
                buyerProfile
            },
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun createOrder(
        items: List<CartItem>,
        totalAmount: Double,
        onSuccess: (Order) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                ensureMarketplaceData()
                val firstSupplierName = items.firstOrNull()?.supplier ?: throw ApiException("Cart is empty.")
                val supplier = supplierByName.values.firstOrNull {
                    it.name.equals(firstSupplierName, ignoreCase = true)
                } ?: throw ApiException("Supplier not found for this cart.")

                val subtotal = items.sumOf { it.subtotal }
                val deliveryFee = (totalAmount - subtotal).coerceAtLeast(0.0)
                val address = AddressManager.getAddress() ?: throw ApiException("Delivery address is required.")
                val createdOrder = parseOrder(
                    ApiClient.postDataObject(
                        endpoint = "buyer/orders/create",
                        bodyParams = mapOf(
                            "buyer_id" to AppSession.buyerId,
                            "supplier_id" to supplier.id,
                            "delivery_fee" to deliveryFee,
                            "delivery_name" to address.fullName,
                            "delivery_phone" to address.phoneNumber,
                            "delivery_address" to address.formattedAddress,
                            "notes" to address.note,
                            "items" to items.map { item ->
                                mapOf(
                                    "supplier_product_id" to (item.id.toIntOrNull() ?: 0),
                                    "quantity" to item.quantity
                                )
                            }
                        )
                    )
                )
                ordersCache.removeAll { it.orderId.equals(createdOrder.orderId, ignoreCase = true) }
                ordersCache.add(0, createdOrder)
                createdOrder
            },
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun suppliersForCategory(categoryName: String): List<Supplier> {
        return suppliersCache.filter { supplier ->
            supplier.categories.any { it.equals(categoryName, ignoreCase = true) }
        }
    }

    fun getOrders(): List<Order> = ordersCache.toList()

    fun findOrder(orderId: String): Order? {
        return ordersCache.firstOrNull { it.orderId.equals(orderId, ignoreCase = true) }
    }

    fun findSupplierByName(name: String): Supplier? {
        return supplierByName.values.firstOrNull { supplier ->
            supplier.name.equals(name, ignoreCase = true)
        }
    }

    fun findThreadBySupplierName(name: String): ChatThread? {
        return chatThreadsCache.firstOrNull { thread ->
            thread.supplierName.equals(name, ignoreCase = true)
        }
    }

    fun cachedConversation(threadId: Int): List<ChatMessage> {
        return threadMessagesCache[threadId].orEmpty()
    }

    fun clearCachedState() {
        buyerProfile = BuyerProfile("", "", "", "", "", "")
        categoriesCache = emptyList()
        suppliersCache = emptyList()
        productsCache = emptyList()
        ordersCache = mutableListOf()
        chatThreadsCache = emptyList()
        offersCache = emptyList()
        notificationsCache = emptyList()
        publicSettingsCache = PublicAppSettings("", "", "Karachi", "PKR")
        referralSummaryCache = ReferralSummary(false, "", 0.0, 0.0, 0, 0.0, emptyList())
        supplierByName.clear()
        supplierIdToCategories.clear()
        threadMessagesCache.clear()
        cachePrefs().edit().clear().apply()
    }

    private fun ensureMarketplaceData(forceRefresh: Boolean = false) {
        if (!forceRefresh && suppliersCache.isNotEmpty() && productsCache.isNotEmpty()) {
            return
        }
        if (!forceRefresh && restoreMarketplaceDataFromCache()) {
            return
        }

        val productArray = runCatching {
            ApiClient.getDataArray("buyer/products")
                .also { saveCacheArray(CACHE_PRODUCTS, it) }
        }.getOrElse { error ->
            cachedArray(CACHE_PRODUCTS) ?: throw error
        }
        val categoriesBySupplier = linkedMapOf<Int, LinkedHashSet<String>>()
        repeat(productArray.length()) { index ->
            val item = productArray.optJSONObject(index) ?: return@repeat
            val supplierId = item.optInt("supplier_id")
            val categoryName = item.optString("category_name")
            if (supplierId > 0 && categoryName.isNotBlank()) {
                categoriesBySupplier.getOrPut(supplierId) { linkedSetOf() }.add(categoryName)
            }
        }

        productsCache = parseProducts(productArray)
        supplierIdToCategories.clear()
        categoriesBySupplier.forEach { (supplierId, names) ->
            supplierIdToCategories[supplierId] = names.toList()
        }

        val supplierArray = runCatching {
            ApiClient.getDataArray("buyer/suppliers")
                .also { saveCacheArray(CACHE_SUPPLIERS, it) }
        }.getOrElse { error ->
            cachedArray(CACHE_SUPPLIERS) ?: throw error
        }
        suppliersCache = parseSuppliers(
            supplierArray = supplierArray,
            categoriesBySupplier = supplierIdToCategories
        )
        supplierByName.clear()
        suppliersCache.forEach { supplier ->
            supplierByName[supplier.name.lowercase(Locale.getDefault())] = supplier
        }
    }

    private fun restoreMarketplaceDataFromCache(): Boolean {
        val productArray = cachedArray(CACHE_PRODUCTS) ?: return false
        val supplierArray = cachedArray(CACHE_SUPPLIERS) ?: return false

        val categoriesBySupplier = linkedMapOf<Int, LinkedHashSet<String>>()
        repeat(productArray.length()) { index ->
            val item = productArray.optJSONObject(index) ?: return@repeat
            val supplierId = item.optInt("supplier_id")
            val categoryName = item.optString("category_name")
            if (supplierId > 0 && categoryName.isNotBlank()) {
                categoriesBySupplier.getOrPut(supplierId) { linkedSetOf() }.add(categoryName)
            }
        }

        productsCache = parseProducts(productArray)
        supplierIdToCategories.clear()
        categoriesBySupplier.forEach { (supplierId, names) ->
            supplierIdToCategories[supplierId] = names.toList()
        }
        suppliersCache = parseSuppliers(
            supplierArray = supplierArray,
            categoriesBySupplier = supplierIdToCategories
        )
        supplierByName.clear()
        suppliersCache.forEach { supplier ->
            supplierByName[supplier.name.lowercase(Locale.getDefault())] = supplier
        }
        return suppliersCache.isNotEmpty() || productsCache.isNotEmpty()
    }

    private fun filterSuppliers(
        categoryName: String,
        searchQuery: String,
        cityFilter: String,
        sort: String
    ): List<Supplier> {
        val normalizedCategory = categoryName.trim()
        val normalizedQuery = searchQuery.trim().lowercase(Locale.getDefault())
        val normalizedCity = cityFilter.trim().lowercase(Locale.getDefault())

        val filtered = suppliersCache.filter { supplier ->
            val matchesCategory = normalizedCategory.isBlank() ||
                supplier.categories.any { it.equals(normalizedCategory, ignoreCase = true) }
            val matchesCity = normalizedCity.isBlank() ||
                supplier.location.lowercase(Locale.getDefault()) == normalizedCity
            val matchesQuery = normalizedQuery.isBlank() ||
                supplier.name.lowercase(Locale.getDefault()).contains(normalizedQuery) ||
                supplier.location.lowercase(Locale.getDefault()).contains(normalizedQuery) ||
                supplier.categories.any { it.lowercase(Locale.getDefault()).contains(normalizedQuery) } ||
                productsCache.any { product ->
                    product.supplierId == supplier.id &&
                        (product.name.lowercase(Locale.getDefault()).contains(normalizedQuery) ||
                            product.categoryName.lowercase(Locale.getDefault()).contains(normalizedQuery) ||
                            product.packaging.lowercase(Locale.getDefault()).contains(normalizedQuery))
                }
            matchesCategory && matchesCity && matchesQuery
        }

        return when (sort.lowercase(Locale.getDefault())) {
            "cheapest" -> filtered.sortedWith(
                compareBy<Supplier> { if (it.lowestPrice > 0.0) it.lowestPrice else Double.MAX_VALUE }
                    .thenBy { it.minimumAmount }
                    .thenBy { it.name.lowercase(Locale.getDefault()) }
            )
            "low_min_order" -> filtered.sortedWith(
                compareBy<Supplier> { it.minimumAmount }
                    .thenBy { if (it.lowestPrice > 0.0) it.lowestPrice else Double.MAX_VALUE }
                    .thenBy { it.name.lowercase(Locale.getDefault()) }
            )
            else -> filtered.sortedWith(
                compareByDescending<Supplier> { it.isVerified }
                    .thenBy { it.name.lowercase(Locale.getDefault()) }
            )
        }
    }

    private fun parseBuyerProfile(payload: JSONObject): BuyerProfile {
        return BuyerProfile(
            storeName = payload.optString("store_name").ifBlank { payload.optString("buyer_name") },
            buyerName = payload.optString("buyer_name"),
            email = payload.optString("email"),
            phoneNumber = payload.optString("phone"),
            city = payload.optString("city"),
            memberSince = ApiFormatting.displayDate(payload.optString("member_since")),
            address = payload.optString("address"),
            latitude = if (payload.isNull("latitude")) null else payload.optDouble("latitude"),
            longitude = if (payload.isNull("longitude")) null else payload.optDouble("longitude")
        )
    }

    private fun parseCategories(array: JSONArray): List<Category> {
        return buildList {
            repeat(array.length()) { index ->
                val item = array.optJSONObject(index) ?: return@repeat
                add(
                    Category(
                        id = item.optInt("id"),
                        name = item.optString("name"),
                        productCount = string(
                            R.string.products_count_format,
                            item.optInt("listing_count", item.optInt("catalog_count"))
                        ),
                        iconResId = R.drawable.ic_grid_view_24,
                        backgroundColor = item.optString("accent_color").ifBlank {
                            ApiFormatting.supplierHeaderColor(index)
                        },
                        description = item.optString("description")
                    )
                )
            }
        }
    }

    private fun parseSuppliers(
        supplierArray: JSONArray,
        categoriesBySupplier: Map<Int, List<String>>
    ): List<Supplier> {
        return buildList {
            repeat(supplierArray.length()) { index ->
                val item = supplierArray.optJSONObject(index) ?: return@repeat
                val supplierId = item.optInt("id")
                add(
                    Supplier(
                        id = supplierId,
                        name = item.optString("business_name"),
                        location = item.optString("city"),
                        productCount = string(
                            R.string.products_count_format,
                            item.optInt("product_count")
                        ),
                        deliveryTime = item.optString("delivery_time"),
                        minimumAmount = item.optDouble("minimum_order_amount"),
                        minimumQuantity = item.optInt("minimum_order_quantity"),
                        categories = categoriesBySupplier[supplierId].orEmpty(),
                        lowestPrice = item.optDouble("lowest_price"),
                        isVerified = item.optInt("is_verified", 0) == 1,
                        headerColor = ApiFormatting.supplierHeaderColor(index),
                        email = item.optString("email"),
                        phoneNumber = item.optString("phone"),
                        address = item.optString("address"),
                        description = item.optString("description"),
                        paymentTerms = item.optString("payment_terms"),
                        ownerName = item.optString("owner_name"),
                        status = item.optString("status")
                    )
                )
            }
        }
    }

    private fun parseProducts(array: JSONArray, fallbackSupplierName: String = ""): List<Product> {
        return buildList {
            repeat(array.length()) { index ->
                val item = array.optJSONObject(index) ?: return@repeat
                add(
                    Product(
                        id = item.optInt("id").toString(),
                        catalogProductId = item.optInt("catalog_product_id"),
                        name = item.optString("catalog_name", item.optString("name")),
                        price = item.optDouble("price"),
                        unit = item.optString("unit_type"),
                        imageResId = 0,
                        imageUrl = ApiConfig.resolveMediaUrl(item.optString("image_url")),
                        supplierName = item.optString("supplier_name").ifBlank { fallbackSupplierName },
                        supplierId = item.optInt("supplier_id"),
                        packaging = item.optString("packaging"),
                        stockQuantity = item.optInt("stock_quantity"),
                        deliveryTime = item.optString("delivery_time"),
                        categoryName = item.optString("category_name"),
                        offerPrice = item.optDouble("offer_price"),
                        maximumOfferQuantity = item.optInt("maximum_quantity"),
                        effectivePrice = item.optDouble("effective_price")
                    )
                )
            }
        }
    }

    private fun parseOffers(array: JSONArray): List<MarketplaceOffer> {
        return buildList {
            repeat(array.length()) { index ->
                val item = array.optJSONObject(index) ?: return@repeat
                add(
                    MarketplaceOffer(
                        id = item.optInt("id"),
                        supplierId = item.optInt("supplier_id"),
                        catalogProductId = item.optInt("catalog_product_id"),
                        title = item.optString("title"),
                        description = item.optString("description"),
                        badgeLabel = item.optString("badge_label"),
                        discountLabel = item.optString("discount_label"),
                        city = item.optString("city"),
                        imageUrl = ApiConfig.resolveMediaUrl(item.optString("image_url")),
                        supplierName = item.optString("supplier_name"),
                        productName = item.optString("product_name"),
                        offerPrice = item.optDouble("offer_price"),
                        maximumQuantity = item.optInt("maximum_quantity"),
                        originalPrice = item.optDouble("original_price"),
                        stockQuantity = item.optInt("stock_quantity")
                    )
                )
            }
        }
    }

    private fun parseNotifications(array: JSONArray): List<BuyerNotificationItem> {
        return buildList {
            repeat(array.length()) { index ->
                val item = array.optJSONObject(index) ?: return@repeat
                add(
                    BuyerNotificationItem(
                        id = item.optInt("id"),
                        title = item.optString("title"),
                        message = item.optString("message"),
                        createdAtLabel = ApiFormatting.displayDateTime(item.optString("created_at")),
                        linkType = item.optString("link_type"),
                        linkValue = item.optString("link_value")
                    )
                )
            }
        }
    }

    private fun parsePublicSettings(payload: JSONObject): PublicAppSettings {
        val settings = PublicAppSettings(
            supportWhatsapp = payload.optString("support_whatsapp"),
            supportWhatsappMessage = payload.optString("support_whatsapp_message"),
            defaultMapCity = payload.optString("map_default_city").ifBlank { "Karachi" },
            defaultCurrency = payload.optString("default_currency").ifBlank { "PKR" }
        )
        CurrencyFormatter.update(context, settings.defaultCurrency)
        return settings
    }

    private fun parseReferralSummary(payload: JSONObject): ReferralSummary {
        val claims = buildList {
            val array = payload.optJSONArray("recent_claims")
            if (array != null) {
                repeat(array.length()) { index ->
                    val item = array.optJSONObject(index) ?: return@repeat
                    add(
                        ReferralClaim(
                            referredStoreName = item.optString("referred_store_name"),
                            referredCity = item.optString("referred_city"),
                            rewardAmount = item.optDouble("reward_amount"),
                            createdAtLabel = ApiFormatting.displayDate(item.optString("created_at"))
                        )
                    )
                }
            }
        }

        return ReferralSummary(
            enabled = payload.optBoolean("enabled", false),
            referralCode = payload.optString("referral_code"),
            rewardAmount = payload.optDouble("reward_amount"),
            refereeRewardAmount = payload.optDouble("referee_reward_amount"),
            totalClaims = payload.optInt("total_claims"),
            earnedAmount = payload.optDouble("earned_amount"),
            recentClaims = claims
        )
    }

    private fun parseOrders(array: JSONArray): List<Order> {
        return buildList {
            repeat(array.length()) { index ->
                val item = array.optJSONObject(index) ?: return@repeat
                add(parseOrder(item))
            }
        }
    }

    private fun parseOrder(item: JSONObject): Order {
        return Order(
            internalId = item.optInt("id"),
            orderId = item.optString("order_number"),
            date = ApiFormatting.displayDate(item.optString("order_date")),
            status = item.optString("status"),
            supplier = item.optString("business_name"),
            itemsCount = item.optInt("item_count", item.optJSONArray("items")?.length() ?: 0),
            totalAmount = item.optDouble("total_amount"),
            deliveryDate = ApiFormatting.displayDate(item.optString("delivery_date")).ifBlank { null }
        )
    }

    private fun parseThreads(array: JSONArray): List<ChatThread> {
        return buildList {
            repeat(array.length()) { index ->
                val item = array.optJSONObject(index) ?: return@repeat
                add(parseThread(item))
            }
        }
    }

    private fun parseThread(item: JSONObject): ChatThread {
        val supplier = suppliersCache.firstOrNull { it.id == item.optInt("supplier_id") }
        return ChatThread(
            threadId = item.optInt("id"),
            supplierName = item.optString("business_name"),
            supplierLocation = supplier?.location.orEmpty(),
            lastMessage = item.optString("last_message"),
            lastSeen = ApiFormatting.displayDateTime(item.optString("last_message_at")),
            unreadCount = item.optInt("buyer_unread_count")
        )
    }

    private fun parseMessages(array: JSONArray?): List<ChatMessage> {
        if (array == null) return emptyList()
        return buildList {
            repeat(array.length()) { index ->
                val item = array.optJSONObject(index) ?: return@repeat
                add(
                    ChatMessage(
                        id = item.optInt("id").toString(),
                        participant = if (item.optString("sender_type").equals("buyer", true)) {
                            ChatParticipant.BUYER
                        } else {
                            ChatParticipant.SUPPLIER
                        },
                        type = when (item.optString("message_type").lowercase(Locale.getDefault())) {
                            "media" -> ChatMessageType.MEDIA
                            "voice" -> ChatMessageType.VOICE
                            else -> ChatMessageType.TEXT
                        },
                        body = item.optString("message_body"),
                        timeLabel = ApiFormatting.displayTime(item.optString("created_at"))
                    )
                )
            }
        }
    }

    private fun saveCacheArray(key: String, value: JSONArray) {
        cachePrefs().edit().putString(key, value.toString()).apply()
    }

    private fun saveCacheObject(key: String, value: JSONObject) {
        cachePrefs().edit().putString(key, value.toString()).apply()
    }

    private fun cachedArray(key: String): JSONArray? {
        val raw = cachePrefs().getString(key, null)?.takeIf { it.isNotBlank() } ?: return null
        return runCatching { JSONArray(raw) }.getOrNull()
    }

    private fun cachedObject(key: String): JSONObject? {
        val raw = cachePrefs().getString(key, null)?.takeIf { it.isNotBlank() } ?: return null
        return runCatching { JSONObject(raw) }.getOrNull()
    }

    private fun maybePopLatestNotification(items: List<BuyerNotificationItem>) {
        val latest = items.maxByOrNull { it.id } ?: return
        val prefs = cachePrefs()
        val lastPoppedId = prefs.getInt(CACHE_LAST_POPPED_NOTIFICATION_ID, 0)
        if (latest.id <= lastPoppedId) return

        prefs.edit().putInt(CACHE_LAST_POPPED_NOTIFICATION_ID, latest.id).apply()
        AppNotificationHelper.showBuyerNotification(
            context = context,
            title = latest.title.ifBlank { string(R.string.notifications) },
            message = latest.message.ifBlank { string(R.string.new_notification_received) },
            navigateTo = latest.linkType.toNotificationTarget(),
            notificationId = latest.id
        )
    }

    private fun String.toNotificationTarget(): String {
        return when (lowercase(Locale.getDefault())) {
            "order", "orders" -> "orders"
            "chat", "chats" -> "chats"
            "cart" -> "cart"
            "categories", "category" -> "categories"
            else -> "home"
        }
    }

    private fun cachePrefs() = context.getSharedPreferences(CACHE_PREFS, Context.MODE_PRIVATE)

    private fun string(@StringRes resId: Int, vararg args: Any): String {
        return if (args.isEmpty()) {
            context.getString(resId)
        } else {
            context.getString(resId, *args)
        }
    }
}
