package com.hiskytechs.muhallinewuserapp.Data

import androidx.annotation.StringRes
import com.hiskytechs.muhallinewuserapp.MuhalliApplication
import com.hiskytechs.muhallinewuserapp.Models.BuyerProfile
import com.hiskytechs.muhallinewuserapp.Models.CartItem
import com.hiskytechs.muhallinewuserapp.Models.Category
import com.hiskytechs.muhallinewuserapp.Models.ChatMessage
import com.hiskytechs.muhallinewuserapp.Models.ChatMessageType
import com.hiskytechs.muhallinewuserapp.Models.ChatParticipant
import com.hiskytechs.muhallinewuserapp.Models.ChatThread
import com.hiskytechs.muhallinewuserapp.Models.Order
import com.hiskytechs.muhallinewuserapp.Models.Product
import com.hiskytechs.muhallinewuserapp.Models.Supplier
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.network.ApiClient
import com.hiskytechs.muhallinewuserapp.network.ApiException
import com.hiskytechs.muhallinewuserapp.network.ApiFormatting
import com.hiskytechs.muhallinewuserapp.network.AppSession
import com.hiskytechs.muhallinewuserapp.network.BackgroundWork
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

    val chatThreads: List<ChatThread>
        get() = chatThreadsCache

    private val supplierByName = linkedMapOf<String, Supplier>()
    private val supplierIdToCategories = linkedMapOf<Int, List<String>>()
    private val threadMessagesCache = linkedMapOf<Int, MutableList<ChatMessage>>()

    fun loadHomeSuppliers(
        onSuccess: (List<Supplier>) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                ensureMarketplaceData(forceRefresh = true)
                suppliersCache.take(5)
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
        onSuccess: (List<Supplier>) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                ensureMarketplaceData(forceRefresh = true)
                if (categoryName.isBlank()) {
                    suppliersCache
                } else {
                    suppliersCache.filter { supplier ->
                        supplier.categories.any { it.equals(categoryName, ignoreCase = true) }
                    }
                }
            },
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun loadSupplierProducts(
        supplierName: String,
        onSuccess: (List<Product>) -> Unit,
        onError: (String) -> Unit
    ) {
        BackgroundWork.run(
            task = {
                ensureMarketplaceData()
                val supplier = supplierByName.values.firstOrNull {
                    it.name.equals(supplierName, ignoreCase = true)
                } ?: throw ApiException("Supplier not found.")

                val productArray = ApiClient.getDataArray(
                    endpoint = "buyer/products",
                    queryParams = mapOf("supplier_id" to supplier.id)
                )
                parseProducts(productArray, supplier.name)
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
                val payload = ApiClient.getDataObject(
                    endpoint = "buyer/profile",
                    queryParams = mapOf("buyer_id" to AppSession.buyerId)
                )
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
                        "store_name" to updatedProfile.storeName,
                        "buyer_name" to updatedProfile.buyerName,
                        "email" to updatedProfile.email,
                        "phone" to updatedProfile.phoneNumber,
                        "city" to updatedProfile.city
                    )
                )
                buyerProfile = updatedProfile
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
                val createdOrder = parseOrder(
                    ApiClient.postDataObject(
                        endpoint = "buyer/orders/create",
                        bodyParams = mapOf(
                            "buyer_id" to AppSession.buyerId,
                            "supplier_id" to supplier.id,
                            "delivery_fee" to deliveryFee,
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

    private fun ensureMarketplaceData(forceRefresh: Boolean = false) {
        if (!forceRefresh && suppliersCache.isNotEmpty() && productsCache.isNotEmpty()) {
            return
        }

        val productArray = ApiClient.getDataArray("buyer/products")
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
            supplierArray = ApiClient.getDataArray("buyer/suppliers"),
            categoriesBySupplier = supplierIdToCategories
        )
        supplierByName.clear()
        suppliersCache.forEach { supplier ->
            supplierByName[supplier.name.lowercase(Locale.getDefault())] = supplier
        }
    }

    private fun parseBuyerProfile(payload: JSONObject): BuyerProfile {
        return BuyerProfile(
            storeName = payload.optString("store_name"),
            buyerName = payload.optString("buyer_name"),
            email = payload.optString("email"),
            phoneNumber = payload.optString("phone"),
            city = payload.optString("city"),
            memberSince = ApiFormatting.displayDate(payload.optString("member_since"))
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
                        name = item.optString("catalog_name", item.optString("name")),
                        price = item.optDouble("price"),
                        unit = item.optString("unit_type"),
                        imageResId = 0,
                        supplierName = item.optString("supplier_name").ifBlank { fallbackSupplierName },
                        supplierId = item.optInt("supplier_id"),
                        packaging = item.optString("packaging"),
                        stockQuantity = item.optInt("stock_quantity"),
                        deliveryTime = item.optString("delivery_time"),
                        categoryName = item.optString("category_name")
                    )
                )
            }
        }
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
                val supplier = suppliersCache.firstOrNull { it.id == item.optInt("supplier_id") }
                add(
                    ChatThread(
                        threadId = item.optInt("id"),
                        supplierName = item.optString("business_name"),
                        supplierLocation = supplier?.location.orEmpty(),
                        lastMessage = item.optString("last_message"),
                        lastSeen = ApiFormatting.displayDateTime(item.optString("last_message_at")),
                        unreadCount = item.optInt("buyer_unread_count")
                    )
                )
            }
        }
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

    private fun string(@StringRes resId: Int, vararg args: Any): String {
        return if (args.isEmpty()) {
            context.getString(resId)
        } else {
            context.getString(resId, *args)
        }
    }
}
