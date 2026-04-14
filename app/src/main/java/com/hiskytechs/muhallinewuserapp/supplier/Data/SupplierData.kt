package com.hiskytechs.muhallinewuserapp.supplier.Data

import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.network.ApiClient
import com.hiskytechs.muhallinewuserapp.network.ApiException
import com.hiskytechs.muhallinewuserapp.network.ApiFormatting
import com.hiskytechs.muhallinewuserapp.network.AppSession
import com.hiskytechs.muhallinewuserapp.network.BackgroundWork
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierCatalogProduct
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierCategory
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierChatMessage
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierConversation
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierDashboardStats
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierEarningsPeriod
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierHomeAction
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierIntroPage
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierOrder
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierOrderStatus
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierProduct
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierProductFilter
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierProfile
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierProfileAction
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierProfileOption
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierQuickAction
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierStockState
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierTransaction
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

object SupplierData {

    private val introPages = listOf(
        SupplierIntroPage(
            R.drawable.ic_storefront_24,
            "List Your Products Easily",
            "Add your wholesale products and reach hundreds of retailers instantly."
        ),
        SupplierIntroPage(
            R.drawable.ic_grid_view_24,
            "Receive Bulk Orders Daily",
            "Retailers place orders directly. You confirm and prepare."
        ),
        SupplierIntroPage(
            R.drawable.ic_attach_money_24,
            "Track Earnings in Real Time",
            "Monitor revenue and performance all in one place."
        )
    )

    private val quickActions = listOf(
        SupplierQuickAction("Add Product", "Choose from catalog", R.drawable.ic_add_24, SupplierHomeAction.ADD_PRODUCT),
        SupplierQuickAction("View Orders", "Track retailer orders", R.drawable.ic_shopping_cart_24, SupplierHomeAction.VIEW_ORDERS),
        SupplierQuickAction("Messages", "Reply to retailers", R.drawable.ic_chat_bubble_24, SupplierHomeAction.OPEN_MESSAGES),
        SupplierQuickAction("Inventory", "Update stock levels", R.drawable.ic_sync_24, SupplierHomeAction.OPEN_INVENTORY)
    )

    private val profileOptions = listOf(
        SupplierProfileOption("My Products", R.drawable.ic_grid_view_24, SupplierProfileAction.PRODUCTS),
        SupplierProfileOption("My Orders", R.drawable.ic_shopping_cart_24, SupplierProfileAction.ORDERS),
        SupplierProfileOption("Earnings", R.drawable.ic_attach_money_24, SupplierProfileAction.EARNINGS),
        SupplierProfileOption("Business Address", R.drawable.ic_location_on_24, SupplierProfileAction.BUSINESS_ADDRESS),
        SupplierProfileOption("Notification Settings", R.drawable.ic_notifications_24, SupplierProfileAction.NOTIFICATIONS),
        SupplierProfileOption("Change Password", R.drawable.ic_lock_24, SupplierProfileAction.CHANGE_PASSWORD),
        SupplierProfileOption("Help & Support", R.drawable.ic_info_24, SupplierProfileAction.HELP_SUPPORT),
        SupplierProfileOption("About App", R.drawable.ic_info_24, SupplierProfileAction.ABOUT),
        SupplierProfileOption("Logout", R.drawable.ic_arrow_back_24, SupplierProfileAction.LOGOUT, true)
    )

    private var categoriesCache: List<SupplierCategory> = emptyList()
    private var catalogProductsCache: List<SupplierCatalogProduct> = emptyList()
    private var supplierProductsCache: MutableList<SupplierProduct> = mutableListOf()
    private var ordersCache: MutableList<SupplierOrder> = mutableListOf()
    private var transactionsCache: List<SupplierTransaction> = emptyList()
    private var conversationsCache: MutableList<SupplierConversation> = mutableListOf()
    private val messagesCache = linkedMapOf<String, MutableList<SupplierChatMessage>>()

    private var dashboardStats = SupplierDashboardStats(0, 0, 0, 0)
    private var profile = SupplierProfile("", "", "", "", "", "", 0, 0)

    fun getIntroPages(): List<SupplierIntroPage> = introPages
    fun getQuickActions(): List<SupplierQuickAction> = quickActions
    fun getProfileOptions(): List<SupplierProfileOption> = profileOptions
    fun getCategories(): List<SupplierCategory> = categoriesCache
    fun getRecentOrders(): List<SupplierOrder> = ordersCache.take(3)
    fun getProfile(): SupplierProfile = profile.copy()
    fun getDashboardStats(): SupplierDashboardStats = dashboardStats

    fun getLowStockAlert(): String {
        val lowStockCount = supplierProductsCache.count { it.stockState == SupplierStockState.LOW_STOCK }
        return if (lowStockCount > 0) {
            "$lowStockCount product running low on stock"
        } else {
            "All products have healthy stock levels."
        }
    }

    fun refreshDashboard(onSuccess: () -> Unit, onError: (String) -> Unit) {
        BackgroundWork.run(
            task = {
                refreshProfileSync()
                refreshProductsSync()
                refreshOrdersSync()
                refreshMessagesSync()
                dashboardStats = SupplierDashboardStats(
                    todayOrders = ordersCache.count { isToday(it.orderDate) },
                    pendingOrders = ordersCache.count { it.status == SupplierOrderStatus.PENDING },
                    thisMonthRevenuePkr = ordersCache
                        .filter { isCurrentMonth(it.orderDate) }
                        .sumOf { it.amountPkr },
                    totalProducts = supplierProductsCache.size
                )
            },
            onSuccess = { onSuccess() },
            onError = onError
        )
    }

    fun refreshProducts(onSuccess: () -> Unit, onError: (String) -> Unit) {
        BackgroundWork.run(
            task = { refreshProductsSync() },
            onSuccess = { onSuccess() },
            onError = onError
        )
    }

    fun refreshOrders(onSuccess: () -> Unit, onError: (String) -> Unit) {
        BackgroundWork.run(
            task = { refreshOrdersSync() },
            onSuccess = { onSuccess() },
            onError = onError
        )
    }

    fun refreshEarnings(onSuccess: () -> Unit, onError: (String) -> Unit) {
        BackgroundWork.run(
            task = { refreshEarningsSync() },
            onSuccess = { onSuccess() },
            onError = onError
        )
    }

    fun refreshMessages(onSuccess: () -> Unit, onError: (String) -> Unit) {
        BackgroundWork.run(
            task = { refreshMessagesSync() },
            onSuccess = { onSuccess() },
            onError = onError
        )
    }

    fun refreshProfile(onSuccess: () -> Unit, onError: (String) -> Unit) {
        BackgroundWork.run(
            task = { refreshProfileSync() },
            onSuccess = { onSuccess() },
            onError = onError
        )
    }

    fun getOrders(status: SupplierOrderStatus? = null): List<SupplierOrder> {
        return if (status == null) ordersCache.toList() else ordersCache.filter { it.status == status }
    }

    fun findOrder(orderId: String): SupplierOrder? {
        return ordersCache.find { order ->
            order.id.equals(orderId, ignoreCase = true) ||
                order.backendId.toString() == orderId
        }?.copy()
    }

    fun updateOrderStatus(
        orderId: String,
        status: SupplierOrderStatus,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                val order = findOrder(orderId) ?: throw ApiException("Order not found.")
                ApiClient.postDataObject(
                    endpoint = "supplier/orders/status",
                    bodyParams = mapOf(
                        "supplier_id" to AppSession.supplierId,
                        "order_id" to order.backendId,
                        "status" to status.toApiValue()
                    )
                )
                refreshOrdersSync()
            },
            onSuccess = { onSuccess() },
            onError = onError
        )
    }

    fun getProducts(
        filter: SupplierProductFilter = SupplierProductFilter.ALL,
        query: String = ""
    ): List<SupplierProduct> {
        val normalizedQuery = query.trim().lowercase(Locale.getDefault())
        return supplierProductsCache.filter { product ->
            val matchesFilter = when (filter) {
                SupplierProductFilter.ALL -> true
                SupplierProductFilter.ACTIVE -> product.isActive
                SupplierProductFilter.INACTIVE -> !product.isActive
                SupplierProductFilter.LOW_STOCK -> product.stockState == SupplierStockState.LOW_STOCK
            }
            val matchesQuery = normalizedQuery.isBlank() ||
                product.name.lowercase(Locale.getDefault()).contains(normalizedQuery) ||
                product.categoryName.lowercase(Locale.getDefault()).contains(normalizedQuery)
            matchesFilter && matchesQuery
        }
    }

    fun getCatalogProducts(categoryId: String, query: String = ""): List<SupplierCatalogProduct> {
        val normalizedQuery = query.trim().lowercase(Locale.getDefault())
        return catalogProductsCache.filter { product ->
            product.categoryId == categoryId &&
                (normalizedQuery.isBlank() || product.name.lowercase(Locale.getDefault()).contains(normalizedQuery))
        }
    }

    fun findCategory(categoryId: String): SupplierCategory? = categoriesCache.find { it.id == categoryId }
    fun findCatalogProduct(productId: String): SupplierCatalogProduct? = catalogProductsCache.find { it.id == productId }
    fun findProduct(productId: String): SupplierProduct? = supplierProductsCache.find { it.id == productId }
    fun findConversation(conversationId: String): SupplierConversation? = conversationsCache.find { it.id == conversationId }
    fun getMessages(conversationId: String): List<SupplierChatMessage> = messagesCache[conversationId]?.toList().orEmpty()

    fun getConversations(query: String = ""): List<SupplierConversation> {
        val normalizedQuery = query.trim().lowercase(Locale.getDefault())
        return conversationsCache.filter { conversation ->
            normalizedQuery.isBlank() ||
                conversation.retailerName.lowercase(Locale.getDefault()).contains(normalizedQuery) ||
                conversation.lastMessage.lowercase(Locale.getDefault()).contains(normalizedQuery)
        }
    }

    fun loadConversation(
        conversationId: String,
        onSuccess: (List<SupplierChatMessage>) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                val payload = ApiClient.getDataObject(
                    endpoint = "supplier/messages/thread",
                    queryParams = mapOf(
                        "supplier_id" to AppSession.supplierId,
                        "thread_id" to conversationId
                    )
                )
                val messages = parseMessages(payload.optJSONArray("messages"), conversationId)
                messagesCache[conversationId] = messages.toMutableList()
                messages
            },
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun sendMessage(
        conversationId: String,
        message: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                val payload = ApiClient.postDataObject(
                    endpoint = "supplier/messages/send",
                    bodyParams = mapOf(
                        "supplier_id" to AppSession.supplierId,
                        "thread_id" to conversationId.toIntOrNull(),
                        "message_body" to message,
                        "message_type" to "text"
                    )
                )
                val messages = parseMessages(payload.optJSONArray("messages"), conversationId)
                messagesCache[conversationId] = messages.toMutableList()
                conversationsCache = conversationsCache.map { conversation ->
                    if (conversation.id == conversationId) {
                        conversation.copy(
                            lastMessage = message,
                            timeLabel = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()),
                            unreadCount = 0
                        )
                    } else {
                        conversation
                    }
                }.toMutableList()
            },
            onSuccess = { onSuccess() },
            onError = onError
        )
    }

    fun getTransactions(period: SupplierEarningsPeriod): List<SupplierTransaction> {
        return when (period) {
            SupplierEarningsPeriod.ALL -> transactionsCache
            SupplierEarningsPeriod.THIS_MONTH -> transactionsCache.filter { it.period == SupplierEarningsPeriod.THIS_MONTH }
            SupplierEarningsPeriod.LAST_MONTH -> transactionsCache.filter { it.period == SupplierEarningsPeriod.LAST_MONTH }
        }
    }

    fun updateProfile(
        updatedProfile: SupplierProfile,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                ApiClient.postDataObject(
                    endpoint = "supplier/profile/update",
                    bodyParams = mapOf(
                        "supplier_id" to AppSession.supplierId,
                        "business_name" to updatedProfile.businessName,
                        "owner_name" to updatedProfile.ownerName,
                        "phone" to updatedProfile.phoneNumber,
                        "email" to updatedProfile.emailAddress,
                        "city" to updatedProfile.city,
                        "address" to updatedProfile.businessAddress,
                        "minimum_order_quantity" to updatedProfile.minimumOrderQuantity,
                        "minimum_order_amount" to updatedProfile.minimumOrderAmountPkr,
                        "delivery_time" to updatedProfile.deliveryTime,
                        "payment_terms" to updatedProfile.paymentTerms,
                        "description" to updatedProfile.description
                    )
                )
                profile = updatedProfile
            },
            onSuccess = { onSuccess() },
            onError = onError
        )
    }

    fun setProductAvailability(
        productId: String,
        isActive: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        updateProduct(productId, onSuccess, onError) { product ->
            mapOf("status" to if (isActive) "active" else "draft")
        }
    }

    fun adjustStock(
        productId: String,
        delta: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        updateProduct(productId, onSuccess, onError) { product ->
            mapOf("stock_quantity" to (product.stock + delta).coerceAtLeast(0))
        }
    }

    fun addProduct(
        catalogProductId: String,
        pricePkr: Int,
        stock: Int,
        deliveryDays: String,
        isActive: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                ApiClient.postDataObject(
                    endpoint = "supplier/products/create",
                    bodyParams = mapOf(
                        "supplier_id" to AppSession.supplierId,
                        "catalog_product_id" to catalogProductId.toIntOrNull(),
                        "price" to pricePkr,
                        "stock_quantity" to stock,
                        "delivery_time" to deliveryDays,
                        "status" to if (isActive) "active" else "draft",
                        "min_order_qty" to profile.minimumOrderQuantity,
                        "min_order_amount" to profile.minimumOrderAmountPkr
                    )
                )
                refreshProductsSync()
            },
            onSuccess = { onSuccess() },
            onError = onError
        )
    }

    private fun updateProduct(
        productId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        extras: (SupplierProduct) -> Map<String, Any>
    ) {
        BackgroundWork.run(
            task = {
                val product = findProduct(productId) ?: throw ApiException("Product not found.")
                val body = linkedMapOf<String, Any?>(
                    "supplier_id" to AppSession.supplierId,
                    "listing_id" to product.id.toIntOrNull(),
                    "catalog_product_id" to product.catalogProductId.toIntOrNull(),
                    "price" to product.pricePkr,
                    "stock_quantity" to product.stock,
                    "delivery_time" to product.deliveryDays,
                    "status" to if (product.isActive) "active" else "draft",
                    "min_order_qty" to profile.minimumOrderQuantity,
                    "min_order_amount" to profile.minimumOrderAmountPkr
                )
                extras(product).forEach { (key, value) -> body[key] = value }

                ApiClient.postDataObject(
                    endpoint = "supplier/products/update",
                    bodyParams = body
                )
                refreshProductsSync()
            },
            onSuccess = { onSuccess() },
            onError = onError
        )
    }

    private fun refreshProfileSync() {
        val payload = ApiClient.getDataObject(
            endpoint = "supplier/profile",
            queryParams = mapOf("supplier_id" to AppSession.supplierId)
        )
        profile = SupplierProfile(
            businessName = payload.optString("business_name"),
            ownerName = payload.optString("owner_name"),
            phoneNumber = payload.optString("phone"),
            emailAddress = payload.optString("email"),
            city = payload.optString("city"),
            businessAddress = payload.optString("address"),
            minimumOrderQuantity = payload.optInt("minimum_order_quantity"),
            minimumOrderAmountPkr = payload.optDouble("minimum_order_amount").roundToInt(),
            deliveryTime = payload.optString("delivery_time"),
            paymentTerms = payload.optString("payment_terms"),
            description = payload.optString("description"),
            businessLicenseNumber = payload.optString("business_license_number"),
            status = payload.optString("status")
        )
    }

    private fun refreshProductsSync() {
        val catalogArray = ApiClient.getDataArray("supplier/catalog")
        catalogProductsCache = parseCatalogProducts(catalogArray)
        categoriesCache = parseCategories(catalogArray)

        supplierProductsCache = parseSupplierProducts(
            ApiClient.getDataArray(
                endpoint = "supplier/products",
                queryParams = mapOf("supplier_id" to AppSession.supplierId)
            )
        ).toMutableList()
    }

    private fun refreshOrdersSync() {
        ordersCache = parseOrders(
            ApiClient.getDataArray(
                endpoint = "supplier/orders",
                queryParams = mapOf("supplier_id" to AppSession.supplierId)
            )
        ).toMutableList()
    }

    private fun refreshEarningsSync() {
        val payload = ApiClient.getDataObject(
            endpoint = "supplier/earnings",
            queryParams = mapOf("supplier_id" to AppSession.supplierId)
        )
        transactionsCache = parseTransactions(payload.optJSONArray("transactions"))
    }

    private fun refreshMessagesSync() {
        if (categoriesCache.isEmpty() || supplierProductsCache.isEmpty()) {
            refreshProductsSync()
        }
        conversationsCache = parseConversations(
            ApiClient.getDataArray(
                endpoint = "supplier/messages",
                queryParams = mapOf("supplier_id" to AppSession.supplierId)
            )
        ).toMutableList()
    }

    private fun parseCategories(catalogArray: JSONArray): List<SupplierCategory> {
        val grouped = linkedMapOf<String, MutableList<JSONObject>>()
        repeat(catalogArray.length()) { index ->
            val item = catalogArray.optJSONObject(index) ?: return@repeat
            val categoryName = item.optString("category_name")
            grouped.getOrPut(categoryName) { mutableListOf() }.add(item)
        }

        return grouped.entries.mapIndexed { index, entry ->
            SupplierCategory(
                id = entry.value.firstOrNull()?.optInt("category_id").toString(),
                name = entry.key,
                productCount = entry.value.size,
                accentColorRes = ApiFormatting.accentColorRes("${entry.key}-$index")
            )
        }
    }

    private fun parseCatalogProducts(array: JSONArray): List<SupplierCatalogProduct> {
        return buildList {
            repeat(array.length()) { index ->
                val item = array.optJSONObject(index) ?: return@repeat
                add(
                    SupplierCatalogProduct(
                        id = item.optInt("id").toString(),
                        categoryId = item.optInt("category_id").toString(),
                        name = item.optString("name"),
                        unitLabel = item.optString("unit_type"),
                        packaging = item.optString("packaging"),
                        accentColorRes = ApiFormatting.accentColorRes("${item.optString("name")}-$index")
                    )
                )
            }
        }
    }

    private fun parseSupplierProducts(array: JSONArray): List<SupplierProduct> {
        return buildList {
            repeat(array.length()) { index ->
                val item = array.optJSONObject(index) ?: return@repeat
                add(
                    SupplierProduct(
                        id = item.optInt("id").toString(),
                        catalogProductId = item.optInt("catalog_product_id").toString(),
                        name = item.optString("name"),
                        categoryName = item.optString("category_name"),
                        unitLabel = item.optString("unit_type"),
                        pricePkr = item.optDouble("price").roundToInt(),
                        stock = item.optInt("stock_quantity"),
                        deliveryDays = item.optString("delivery_time"),
                        isActive = item.optString("status").equals("active", true),
                        accentColorRes = ApiFormatting.accentColorRes("${item.optString("name")}-$index")
                    )
                )
            }
        }
    }

    private fun parseOrders(array: JSONArray): List<SupplierOrder> {
        return buildList {
            repeat(array.length()) { index ->
                val item = array.optJSONObject(index) ?: return@repeat
                add(
                    SupplierOrder(
                        backendId = item.optInt("id"),
                        id = item.optString("order_number"),
                        retailerName = item.optString("store_name"),
                        orderDate = ApiFormatting.displayDate(item.optString("order_date")),
                        expectedDeliveryDate = ApiFormatting.displayDate(item.optString("delivery_date")),
                        itemsCount = item.optInt("item_count"),
                        amountPkr = item.optDouble("total_amount").roundToInt(),
                        status = item.optString("status").toSupplierStatus()
                    )
                )
            }
        }
    }

    private fun parseTransactions(array: JSONArray?): List<SupplierTransaction> {
        if (array == null) return emptyList()
        return buildList {
            repeat(array.length()) { index ->
                val item = array.optJSONObject(index) ?: return@repeat
                val displayDate = ApiFormatting.displayDate(item.optString("order_date"))
                add(
                    SupplierTransaction(
                        retailerName = item.optString("order_number"),
                        orderId = item.optString("order_number"),
                        date = displayDate,
                        amountPkr = item.optDouble("total_amount").roundToInt(),
                        period = when {
                            isCurrentMonth(displayDate) -> SupplierEarningsPeriod.THIS_MONTH
                            isLastMonth(displayDate) -> SupplierEarningsPeriod.LAST_MONTH
                            else -> SupplierEarningsPeriod.ALL
                        }
                    )
                )
            }
        }
    }

    private fun parseConversations(array: JSONArray): List<SupplierConversation> {
        return buildList {
            repeat(array.length()) { index ->
                val item = array.optJSONObject(index) ?: return@repeat
                add(
                    SupplierConversation(
                        id = item.optInt("id").toString(),
                        retailerName = item.optString("store_name"),
                        lastMessage = item.optString("last_message"),
                        timeLabel = ApiFormatting.displayDateTime(item.optString("last_message_at")),
                        unreadCount = item.optInt("supplier_unread_count"),
                        accentColorRes = ApiFormatting.accentColorRes("${item.optString("store_name")}-$index")
                    )
                )
            }
        }
    }

    private fun parseMessages(array: JSONArray?, conversationId: String): List<SupplierChatMessage> {
        if (array == null) return emptyList()
        return buildList {
            repeat(array.length()) { index ->
                val item = array.optJSONObject(index) ?: return@repeat
                add(
                    SupplierChatMessage(
                        id = item.optInt("id").toString(),
                        conversationId = conversationId,
                        message = item.optString("message_body"),
                        timeLabel = ApiFormatting.displayTime(item.optString("created_at")),
                        isMine = item.optString("sender_type").equals("supplier", true)
                    )
                )
            }
        }
    }

    private fun String.toSupplierStatus(): SupplierOrderStatus {
        return when (lowercase(Locale.getDefault())) {
            "pending" -> SupplierOrderStatus.PENDING
            "shipped" -> SupplierOrderStatus.SHIPPED
            "delivered" -> SupplierOrderStatus.DELIVERED
            else -> SupplierOrderStatus.CONFIRMED
        }
    }

    private fun SupplierOrderStatus.toApiValue(): String {
        return when (this) {
            SupplierOrderStatus.PENDING -> "pending"
            SupplierOrderStatus.CONFIRMED -> "processing"
            SupplierOrderStatus.SHIPPED -> "shipped"
            SupplierOrderStatus.DELIVERED -> "delivered"
        }
    }

    private fun isToday(displayDate: String): Boolean {
        return displayDate == ApiFormatting.displayDate(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()))
    }

    private fun isCurrentMonth(displayDate: String): Boolean {
        return monthMatch(displayDate, 0)
    }

    private fun isLastMonth(displayDate: String): Boolean {
        return monthMatch(displayDate, -1)
    }

    private fun monthMatch(displayDate: String, offset: Int): Boolean {
        return runCatching {
            val parsed = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).parse(displayDate) ?: return false
            val target = Calendar.getInstance().apply { add(Calendar.MONTH, offset) }
            val calendar = Calendar.getInstance().apply { time = parsed }
            calendar.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == target.get(Calendar.MONTH)
        }.getOrDefault(false)
    }
}
