package com.hiskytechs.muhallinewuserapp.supplier.Data

import com.hiskytechs.muhallinewuserapp.R
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object SupplierData {

    private val introPages = listOf(
        SupplierIntroPage(R.drawable.ic_storefront_24, "List Your Products Easily", "Add your wholesale products and reach hundreds of retailers instantly."),
        SupplierIntroPage(R.drawable.ic_grid_view_24, "Receive Bulk Orders Daily", "Retailers place orders directly. You confirm and prepare."),
        SupplierIntroPage(R.drawable.ic_attach_money_24, "Track Earnings in Real Time", "Monitor revenue and performance all in one place.")
    )

    private val categories = listOf(
        SupplierCategory("snacks", "Snacks", 12, R.color.supplier_orange),
        SupplierCategory("chips", "Chips", 8, R.color.supplier_yellow),
        SupplierCategory("drinks", "Drinks", 16, R.color.supplier_pink),
        SupplierCategory("groceries", "Groceries", 22, R.color.supplier_blue),
        SupplierCategory("mineral_water", "Mineral Water", 7, R.color.supplier_light_blue),
        SupplierCategory("dairy", "Dairy", 9, R.color.supplier_soft_blue),
        SupplierCategory("bakery", "Bakery", 6, R.color.supplier_amber),
        SupplierCategory("frozen_food", "Frozen Food", 10, R.color.supplier_ice),
        SupplierCategory("cleaning", "Cleaning Supplies", 11, R.color.supplier_teal),
        SupplierCategory("personal_care", "Personal Care", 13, R.color.supplier_blush)
    )

    private val catalogProducts = listOf(
        SupplierCatalogProduct("lays_classic", "chips", "Lays Classic", "Carton", "Box of 24 packs", R.color.supplier_orange),
        SupplierCatalogProduct("kurkure_masala", "snacks", "Kurkure Masala Munch", "Carton", "Box of 30 packs", R.color.supplier_orange),
        SupplierCatalogProduct("coca_cola_250", "drinks", "Coca Cola 250ml", "Carton", "Tray of 24 cans", R.color.supplier_red),
        SupplierCatalogProduct("nestle_500", "mineral_water", "Nestle Pure Life 500ml", "Carton", "Pack of 12 bottles", R.color.supplier_light_blue),
        SupplierCatalogProduct("aquafina_15", "mineral_water", "Aquafina 1.5L", "Carton", "Pack of 6 bottles", R.color.supplier_blue),
        SupplierCatalogProduct("rice_basmati", "groceries", "Rice Basmati 5kg", "Bag", "Premium long grain", R.color.supplier_amber),
        SupplierCatalogProduct("milk_uht", "dairy", "UHT Milk 1L", "Carton", "Pack of 12 boxes", R.color.supplier_soft_blue),
        SupplierCatalogProduct("bread_butter", "bakery", "Bread Butter Rusks", "Carton", "Pack of 20", R.color.supplier_amber),
        SupplierCatalogProduct("frozen_fries", "frozen_food", "Frozen Fries 2.5kg", "Carton", "Pack of 6", R.color.supplier_ice),
        SupplierCatalogProduct("dishwash", "cleaning", "Dishwash Liquid 500ml", "Carton", "Pack of 24 bottles", R.color.supplier_teal),
        SupplierCatalogProduct("soap_bar", "personal_care", "Bath Soap 100g", "Carton", "Pack of 48 bars", R.color.supplier_blush)
    )

    private val supplierProducts = mutableListOf(
        SupplierProduct("product_1", "lays_classic", "Lays Classic", "Chips", "Carton", 1200, 150, "2-3 days", true, R.color.supplier_orange),
        SupplierProduct("product_2", "kurkure_masala", "Kurkure Masala Munch", "Snacks", "Carton", 950, 8, "1-2 days", true, R.color.supplier_orange),
        SupplierProduct("product_3", "coca_cola_250", "Coca Cola 250ml", "Drinks", "Carton", 850, 200, "2-3 days", true, R.color.supplier_red),
        SupplierProduct("product_4", "nestle_500", "Nestle Pure Life 500ml", "Mineral Water", "Carton", 450, 0, "3-4 days", false, R.color.supplier_light_blue),
        SupplierProduct("product_5", "rice_basmati", "Rice Basmati 5kg", "Groceries", "Bag", 1450, 85, "2-3 days", true, R.color.supplier_amber)
    )

    private val orders = mutableListOf(
        SupplierOrder("WM-10234", "Super Store Karachi", "18/03/2026", "21/03/2026", 3, 45600, SupplierOrderStatus.PENDING),
        SupplierOrder("WM-10233", "ABC Retail Store", "17/03/2026", "20/03/2026", 2, 32500, SupplierOrderStatus.CONFIRMED),
        SupplierOrder("WM-10232", "Metro Wholesale", "15/03/2026", "19/03/2026", 4, 68500, SupplierOrderStatus.SHIPPED),
        SupplierOrder("WM-10231", "Quick Mart", "14/03/2026", "17/03/2026", 1, 24000, SupplierOrderStatus.DELIVERED)
    )

    private val transactions = listOf(
        SupplierTransaction("Quick Mart", "WM-10231", "17/03/2026", 24500, SupplierEarningsPeriod.THIS_MONTH),
        SupplierTransaction("City Grocers", "WM-10228", "15/03/2026", 18900, SupplierEarningsPeriod.THIS_MONTH),
        SupplierTransaction("Super Store Karachi", "WM-10225", "12/03/2026", 35600, SupplierEarningsPeriod.THIS_MONTH),
        SupplierTransaction("Metro Wholesale", "WM-10220", "08/03/2026", 52300, SupplierEarningsPeriod.THIS_MONTH),
        SupplierTransaction("ABC Retail Store", "WM-10215", "05/03/2026", 29800, SupplierEarningsPeriod.THIS_MONTH)
    )

    private val quickActions = listOf(
        SupplierQuickAction("Add Product", "Choose from catalog", R.drawable.ic_add_24, SupplierHomeAction.ADD_PRODUCT),
        SupplierQuickAction("View Orders", "Track retailer orders", R.drawable.ic_shopping_cart_24, SupplierHomeAction.VIEW_ORDERS),
        SupplierQuickAction("Messages", "Reply to retailers", R.drawable.ic_chat_bubble_24, SupplierHomeAction.OPEN_MESSAGES),
        SupplierQuickAction("Inventory", "Update stock levels", R.drawable.ic_sync_24, SupplierHomeAction.OPEN_INVENTORY)
    )

    private var profile = SupplierProfile(
        businessName = "Fresh Foods Wholesale",
        ownerName = "Ahmed Khan",
        phoneNumber = "+92 300 1234567",
        emailAddress = "ahmed@freshfoods.pk",
        city = "Karachi",
        businessAddress = "Plot 123, Industrial Area, Karachi",
        minimumOrderQuantity = 5,
        minimumOrderAmountPkr = 10000
    )

    private val conversations = mutableListOf(
        SupplierConversation("thread_super_store", "Super Store Karachi", "When will the order be delivered?", "10:45 AM", 2, R.color.primary),
        SupplierConversation("thread_abc", "ABC Retail Store", "Thank you for confirming!", "Yesterday", 0, R.color.supplier_blue)
    )

    private val messages = mutableMapOf(
        "thread_super_store" to mutableListOf(
            SupplierChatMessage("m1", "thread_super_store", "Hi, I placed an order yesterday.", "10:30 AM", false),
            SupplierChatMessage("m2", "thread_super_store", "Yes, order #WM-10234. It will be shipped today.", "10:35 AM", true),
            SupplierChatMessage("m3", "thread_super_store", "When will the order be delivered?", "10:45 AM", false)
        ),
        "thread_abc" to mutableListOf(
            SupplierChatMessage("m4", "thread_abc", "Thank you for confirming!", "Yesterday", false)
        )
    )

    fun getIntroPages(): List<SupplierIntroPage> = introPages
    fun getCategories(): List<SupplierCategory> = categories
    fun getQuickActions(): List<SupplierQuickAction> = quickActions
    fun getRecentOrders(): List<SupplierOrder> = orders.take(3)
    fun getProfile(): SupplierProfile = profile.copy()

    fun getDashboardStats(): SupplierDashboardStats {
        return SupplierDashboardStats(
            todayOrders = 1,
            pendingOrders = orders.count { it.status == SupplierOrderStatus.PENDING },
            thisMonthRevenuePkr = 24000,
            totalProducts = supplierProducts.size
        )
    }

    fun getLowStockAlert(): String {
        return if (supplierProducts.any { it.stockState == SupplierStockState.LOW_STOCK }) {
            "1 product running low on stock"
        } else {
            "All products have healthy stock levels."
        }
    }

    fun getOrders(status: SupplierOrderStatus? = null): List<SupplierOrder> {
        return if (status == null) orders.toList() else orders.filter { it.status == status }
    }

    fun findOrder(orderId: String): SupplierOrder? = orders.find { it.id == orderId }?.copy()

    fun updateOrderStatus(orderId: String, status: SupplierOrderStatus) {
        val index = orders.indexOfFirst { it.id == orderId }
        if (index >= 0) {
            orders[index] = orders[index].copy(status = status)
        }
    }

    fun getProducts(filter: SupplierProductFilter = SupplierProductFilter.ALL, query: String = ""): List<SupplierProduct> {
        val normalizedQuery = query.trim().lowercase(Locale.getDefault())
        return supplierProducts.filter { product ->
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
        return catalogProducts.filter { product ->
            product.categoryId == categoryId &&
                (normalizedQuery.isBlank() || product.name.lowercase(Locale.getDefault()).contains(normalizedQuery))
        }
    }

    fun findCategory(categoryId: String): SupplierCategory? = categories.find { it.id == categoryId }
    fun findCatalogProduct(productId: String): SupplierCatalogProduct? = catalogProducts.find { it.id == productId }
    fun findProduct(productId: String): SupplierProduct? = supplierProducts.find { it.id == productId }
    fun findConversation(conversationId: String): SupplierConversation? = conversations.find { it.id == conversationId }
    fun getMessages(conversationId: String): List<SupplierChatMessage> = messages[conversationId]?.toList().orEmpty()

    fun getConversations(query: String = ""): List<SupplierConversation> {
        val normalizedQuery = query.trim().lowercase(Locale.getDefault())
        return conversations.filter { conversation ->
            normalizedQuery.isBlank() ||
                conversation.retailerName.lowercase(Locale.getDefault()).contains(normalizedQuery) ||
                conversation.lastMessage.lowercase(Locale.getDefault()).contains(normalizedQuery)
        }
    }

    fun sendMessage(conversationId: String, message: String) {
        val trimmed = message.trim()
        if (trimmed.isEmpty()) return
        val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val chatMessage = SupplierChatMessage(UUID.randomUUID().toString(), conversationId, trimmed, time, true)
        messages.getOrPut(conversationId) { mutableListOf() }.add(chatMessage)

        val index = conversations.indexOfFirst { it.id == conversationId }
        if (index >= 0) {
            conversations[index] = conversations[index].copy(lastMessage = trimmed, timeLabel = time, unreadCount = 0)
        }
    }

    fun getTransactions(period: SupplierEarningsPeriod): List<SupplierTransaction> {
        return when (period) {
            SupplierEarningsPeriod.ALL -> transactions
            SupplierEarningsPeriod.THIS_MONTH -> transactions.filter { it.period == SupplierEarningsPeriod.THIS_MONTH }
            SupplierEarningsPeriod.LAST_MONTH -> emptyList()
        }
    }

    fun getProfileOptions(): List<SupplierProfileOption> {
        return listOf(
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
    }

    fun updateProfile(updatedProfile: SupplierProfile) {
        profile = updatedProfile
    }

    fun setProductAvailability(productId: String, isActive: Boolean) {
        supplierProducts.find { it.id == productId }?.isActive = isActive
    }

    fun adjustStock(productId: String, delta: Int) {
        supplierProducts.find { it.id == productId }?.let { product ->
            product.stock = (product.stock + delta).coerceAtLeast(0)
            if (product.stock == 0) product.isActive = false
        }
    }

    fun addProduct(catalogProductId: String, pricePkr: Int, stock: Int, deliveryDays: String, isActive: Boolean) {
        val catalogProduct = findCatalogProduct(catalogProductId) ?: return
        val existing = supplierProducts.find { it.catalogProductId == catalogProductId }
        if (existing != null) {
            existing.pricePkr = pricePkr
            existing.stock = stock
            existing.deliveryDays = deliveryDays
            existing.isActive = isActive
            return
        }

        supplierProducts.add(
            0,
            SupplierProduct(
                id = "product_${supplierProducts.size + 1}",
                catalogProductId = catalogProduct.id,
                name = catalogProduct.name,
                categoryName = findCategory(catalogProduct.categoryId)?.name.orEmpty(),
                unitLabel = catalogProduct.unitLabel,
                pricePkr = pricePkr,
                stock = stock,
                deliveryDays = deliveryDays,
                isActive = isActive,
                accentColorRes = catalogProduct.accentColorRes
            )
        )
    }
}
