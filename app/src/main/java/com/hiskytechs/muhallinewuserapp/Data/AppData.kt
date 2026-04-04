package com.hiskytechs.muhallinewuserapp.Data

import androidx.annotation.StringRes
import com.hiskytechs.muhallinewuserapp.MuhalliApplication
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.Models.BuyerProfile
import com.hiskytechs.muhallinewuserapp.Models.CartItem
import com.hiskytechs.muhallinewuserapp.Models.Category
import com.hiskytechs.muhallinewuserapp.Models.ChatMessage
import com.hiskytechs.muhallinewuserapp.Models.ChatMessageType
import com.hiskytechs.muhallinewuserapp.Models.ChatParticipant
import com.hiskytechs.muhallinewuserapp.Models.ChatThread
import com.hiskytechs.muhallinewuserapp.Models.Order
import com.hiskytechs.muhallinewuserapp.Models.Supplier
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object AppData {

    private const val STATUS_DELIVERED = "Delivered"
    private const val STATUS_IN_TRANSIT = "In Transit"
    private const val STATUS_PROCESSING = "Processing"
    private const val STATUS_CANCELLED = "Cancelled"

    private val context
        get() = MuhalliApplication.instance

    val buyerProfile: BuyerProfile by lazy {
        BuyerProfile(
            storeName = string(R.string.data_store_name),
            buyerName = string(R.string.data_buyer_name),
            email = string(R.string.mystore_email_com),
            phoneNumber = string(R.string.value_971_50_123_4567),
            city = string(R.string.data_buyer_city),
            memberSince = string(R.string.data_buyer_member_since)
        )
    }

    val categories: List<Category>
        get() = listOf(
            Category(string(R.string.data_category_snacks), products(245), R.drawable.ic_grid_view_24, "#FFF8E1"),
            Category(string(R.string.data_category_chips), products(189), R.drawable.ic_grid_view_24, "#E3F2FD"),
            Category(string(R.string.data_category_mineral_water), products(158), R.drawable.ic_grid_view_24, "#E0F2F1"),
            Category(string(R.string.data_category_groceries), products(567), R.drawable.ic_grid_view_24, "#FCE4EC"),
            Category(string(R.string.data_category_drinks), products(234), R.drawable.ic_grid_view_24, "#E8EAF6"),
            Category(string(R.string.data_category_fresh_fruits), products(145), R.drawable.ic_grid_view_24, "#FFEBEE"),
            Category(string(R.string.data_category_meat), products(98), R.drawable.ic_grid_view_24, "#FFF3E0"),
            Category(string(R.string.data_category_coffee_tea), products(123), R.drawable.ic_grid_view_24, "#F3E5F5")
        )

    val suppliers: List<Supplier>
        get() = listOf(
            Supplier(
                name = string(R.string.data_supplier_al_hamd_wholes),
                location = string(R.string.data_location_jebel_ali),
                productCount = products(250),
                deliveryTime = deliveryRange(1, 2),
                minAmount = currency(500.0),
                minQty = "20",
                categories = listOf(
                    string(R.string.data_category_snacks),
                    string(R.string.data_category_groceries)
                ),
                headerColor = "#EEF4FF"
            ),
            Supplier(
                name = string(R.string.data_supplier_al_sadiqa_company),
                location = string(R.string.data_location_sharjah),
                productCount = products(180),
                deliveryTime = string(R.string.delivery_same_day),
                minAmount = currency(300.0),
                minQty = "15",
                categories = listOf(
                    string(R.string.data_category_drinks),
                    string(R.string.data_category_mineral_water)
                ),
                headerColor = "#EFFBF3"
            ),
            Supplier(
                name = string(R.string.data_supplier_al_far_imports),
                location = string(R.string.data_location_dubai),
                productCount = products(320),
                deliveryTime = deliveryRange(2, 3),
                minAmount = currency(1000.0),
                minQty = "50",
                categories = listOf(
                    string(R.string.data_category_chips),
                    string(R.string.data_category_snacks)
                ),
                headerColor = "#FFF6EC"
            ),
            Supplier(
                name = string(R.string.data_supplier_premium_foods),
                location = string(R.string.data_location_dubai),
                productCount = products(215),
                deliveryTime = deliveryRange(1, 2),
                minAmount = currency(400.0),
                minQty = "25",
                categories = listOf(
                    string(R.string.data_category_groceries),
                    string(R.string.data_category_coffee_tea)
                ),
                headerColor = "#F5F2FF"
            ),
            Supplier(
                name = string(R.string.data_supplier_global_trade),
                location = string(R.string.data_location_abu_dhabi),
                productCount = products(298),
                deliveryTime = string(R.string.delivery_same_day),
                minAmount = currency(600.0),
                minQty = "30",
                categories = listOf(
                    string(R.string.data_category_fresh_fruits),
                    string(R.string.data_category_drinks)
                ),
                headerColor = "#FFF1F6"
            ),
            Supplier(
                name = string(R.string.data_supplier_pure_spring),
                location = string(R.string.data_location_ajman),
                productCount = products(164),
                deliveryTime = string(R.string.delivery_one_day),
                minAmount = currency(350.0),
                minQty = "18",
                categories = listOf(
                    string(R.string.data_category_mineral_water),
                    string(R.string.data_category_drinks)
                ),
                headerColor = "#EAF9F8"
            ),
            Supplier(
                name = string(R.string.data_supplier_prime_cuts),
                location = string(R.string.data_location_lahore),
                productCount = products(104),
                deliveryTime = string(R.string.delivery_next_day),
                minAmount = currency(850.0),
                minQty = "12",
                categories = listOf(
                    string(R.string.data_category_meat),
                    string(R.string.data_category_groceries)
                ),
                headerColor = "#FFF3EE"
            ),
            Supplier(
                name = string(R.string.data_supplier_desert_brew),
                location = string(R.string.data_location_karachi),
                productCount = products(142),
                deliveryTime = string(R.string.delivery_two_days),
                minAmount = currency(450.0),
                minQty = "22",
                categories = listOf(
                    string(R.string.data_category_coffee_tea),
                    string(R.string.data_category_snacks)
                ),
                headerColor = "#F4EEFF"
            )
        )

    private val orderHistory by lazy {
        mutableListOf(
            Order(
                orderId = "ORD-12345",
                date = string(R.string.data_date_march_14_2026),
                status = STATUS_DELIVERED,
                supplier = string(R.string.data_supplier_al_hamd_wholes),
                itemsCount = 3,
                totalAmount = 231.97
            ),
            Order(
                orderId = "ORD-12344",
                date = string(R.string.data_date_march_12_2026),
                status = STATUS_IN_TRANSIT,
                supplier = string(R.string.data_supplier_al_sadiqa_company),
                itemsCount = 2,
                totalAmount = 269.98,
                deliveryDate = string(R.string.data_date_march_17_2026)
            ),
            Order(
                orderId = "ORD-12343",
                date = string(R.string.data_date_march_10_2026),
                status = STATUS_PROCESSING,
                supplier = string(R.string.data_supplier_al_far_imports),
                itemsCount = 5,
                totalAmount = 445.50
            ),
            Order(
                orderId = "ORD-12342",
                date = string(R.string.data_date_march_8_2026),
                status = STATUS_DELIVERED,
                supplier = string(R.string.data_supplier_premium_foods),
                itemsCount = 4,
                totalAmount = 389.99
            ),
            Order(
                orderId = "ORD-12341",
                date = string(R.string.data_date_march_5_2026),
                status = STATUS_CANCELLED,
                supplier = string(R.string.data_supplier_global_trade),
                itemsCount = 2,
                totalAmount = 178.00
            )
        )
    }

    val chatThreads: List<ChatThread>
        get() = listOf(
            ChatThread(
                supplierName = string(R.string.data_supplier_al_hamd_wholes),
                supplierLocation = string(R.string.data_location_jebel_ali),
                lastMessage = string(R.string.data_chat_thread_message_1),
                lastSeen = string(R.string.data_last_seen_2_min_ago),
                unreadCount = 2
            ),
            ChatThread(
                supplierName = string(R.string.data_supplier_premium_foods),
                supplierLocation = string(R.string.data_location_dubai),
                lastMessage = string(R.string.data_chat_thread_message_2),
                lastSeen = string(R.string.data_last_seen_15_min_ago)
            ),
            ChatThread(
                supplierName = string(R.string.data_supplier_global_trade),
                supplierLocation = string(R.string.data_location_abu_dhabi),
                lastMessage = string(R.string.data_chat_thread_message_3),
                lastSeen = string(R.string.data_last_seen_1_hr_ago),
                unreadCount = 1
            ),
            ChatThread(
                supplierName = string(R.string.data_supplier_pure_spring),
                supplierLocation = string(R.string.data_location_ajman),
                lastMessage = string(R.string.data_chat_thread_message_4),
                lastSeen = string(R.string.data_last_seen_yesterday)
            )
        )

    fun suppliersForCategory(categoryName: String): List<Supplier> {
        return suppliers.filter { supplier ->
            supplier.categories.any { it.equals(categoryName, ignoreCase = true) }
        }
    }

    fun getOrders(): List<Order> = orderHistory.toList()

    fun findOrder(orderId: String): Order? = orderHistory.find { it.orderId == orderId }

    fun findSupplierByName(name: String): Supplier? {
        return suppliers.find { supplier ->
            supplier.name.equals(name, ignoreCase = true)
        }
    }

    fun conversationForSupplier(supplierName: String): List<ChatMessage> {
        return when (normalizedSupplierKey(supplierName)) {
            "al_hamd" -> alHamdConversation()
            "premium_foods" -> premiumFoodsConversation()
            else -> defaultConversation(supplierName)
        }
    }

    fun addOrderFromCart(items: List<CartItem>, totalAmount: Double): Order {
        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val supplierName = items.map { it.supplier }.distinct().let { names ->
            if (names.size == 1) names.first() else string(R.string.multiple_suppliers)
        }
        val order = Order(
            orderId = "ORD-${13000 + orderHistory.size}",
            date = dateFormat.format(Date()),
            status = STATUS_PROCESSING,
            supplier = supplierName,
            itemsCount = items.sumOf { it.quantity },
            totalAmount = totalAmount
        )
        orderHistory.add(0, order)
        return order
    }

    private fun alHamdConversation(): List<ChatMessage> {
        return listOf(
            ChatMessage(
                id = "alhamd-1",
                participant = ChatParticipant.SUPPLIER,
                type = ChatMessageType.TEXT,
                body = string(R.string.data_chat_alhamd_text_1),
                timeLabel = timeLabel(9, 10)
            ),
            ChatMessage(
                id = "alhamd-2",
                participant = ChatParticipant.BUYER,
                type = ChatMessageType.MEDIA,
                body = string(R.string.data_chat_alhamd_media_body),
                timeLabel = timeLabel(9, 12),
                mediaLabel = string(R.string.photo),
                mediaTitle = string(R.string.data_chat_alhamd_media_title),
                mediaSubtitle = string(R.string.data_chat_alhamd_media_subtitle)
            ),
            ChatMessage(
                id = "alhamd-3",
                participant = ChatParticipant.SUPPLIER,
                type = ChatMessageType.VOICE,
                body = string(R.string.data_chat_alhamd_voice_body),
                timeLabel = timeLabel(9, 15),
                voiceDuration = string(R.string.value_0_18),
                voiceStatus = string(R.string.data_chat_alhamd_voice_status),
                voiceProgress = 48
            ),
            ChatMessage(
                id = "alhamd-4",
                participant = ChatParticipant.SUPPLIER,
                type = ChatMessageType.MEDIA,
                body = string(R.string.data_chat_alhamd_invoice_body),
                timeLabel = timeLabel(9, 18),
                mediaLabel = string(R.string.data_chat_alhamd_invoice_label),
                mediaTitle = string(R.string.data_chat_alhamd_invoice_title),
                mediaSubtitle = string(R.string.data_chat_alhamd_invoice_subtitle)
            ),
            ChatMessage(
                id = "alhamd-5",
                participant = ChatParticipant.BUYER,
                type = ChatMessageType.TEXT,
                body = string(R.string.data_chat_alhamd_buyer_reply),
                timeLabel = timeLabel(9, 20)
            )
        )
    }

    private fun premiumFoodsConversation(): List<ChatMessage> {
        return listOf(
            ChatMessage(
                id = "premium-1",
                participant = ChatParticipant.SUPPLIER,
                type = ChatMessageType.TEXT,
                body = string(R.string.data_chat_premium_text_1),
                timeLabel = timeLabel(11, 5)
            ),
            ChatMessage(
                id = "premium-2",
                participant = ChatParticipant.BUYER,
                type = ChatMessageType.VOICE,
                body = string(R.string.data_chat_premium_voice_body),
                timeLabel = timeLabel(11, 9),
                voiceDuration = "0:22",
                voiceStatus = string(R.string.buyer_voice_note_status),
                voiceProgress = 70
            ),
            ChatMessage(
                id = "premium-3",
                participant = ChatParticipant.SUPPLIER,
                type = ChatMessageType.MEDIA,
                body = string(R.string.data_chat_premium_media_body),
                timeLabel = timeLabel(11, 14),
                mediaLabel = string(R.string.data_chat_premium_media_label),
                mediaTitle = string(R.string.data_chat_premium_media_title),
                mediaSubtitle = string(R.string.data_chat_premium_media_subtitle)
            )
        )
    }

    private fun defaultConversation(supplierName: String): List<ChatMessage> {
        return listOf(
            ChatMessage(
                id = "${supplierName}-1",
                participant = ChatParticipant.SUPPLIER,
                type = ChatMessageType.TEXT,
                body = string(R.string.data_chat_default_text_1),
                timeLabel = timeLabel(8, 45)
            ),
            ChatMessage(
                id = "${supplierName}-2",
                participant = ChatParticipant.BUYER,
                type = ChatMessageType.MEDIA,
                body = string(R.string.data_chat_default_media_body),
                timeLabel = timeLabel(8, 49),
                mediaLabel = string(R.string.data_chat_default_media_label),
                mediaTitle = string(R.string.data_chat_default_media_title),
                mediaSubtitle = string(R.string.data_chat_default_media_subtitle)
            ),
            ChatMessage(
                id = "${supplierName}-3",
                participant = ChatParticipant.SUPPLIER,
                type = ChatMessageType.VOICE,
                body = string(R.string.data_chat_default_voice_body),
                timeLabel = timeLabel(8, 52),
                voiceDuration = "0:16",
                voiceStatus = string(R.string.data_chat_default_voice_status),
                voiceProgress = 42
            )
        )
    }

    private fun normalizedSupplierKey(name: String): String {
        return when (name.trim().lowercase(Locale.ROOT)) {
            string(R.string.data_supplier_al_hamd_wholes).lowercase(Locale.ROOT) -> "al_hamd"
            string(R.string.data_supplier_premium_foods).lowercase(Locale.ROOT) -> "premium_foods"
            else -> "default"
        }
    }

    private fun products(count: Int): String = string(R.string.products_count_format, count)

    private fun deliveryRange(start: Int, end: Int): String {
        return string(R.string.delivery_days_range_format, start, end)
    }

    private fun currency(amount: Double): String {
        return String.format(Locale.getDefault(), string(R.string.currency_amount_format), amount)
    }

    private fun timeLabel(hour24: Int, minute: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour24)
            set(Calendar.MINUTE, minute)
        }
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
    }

    private fun string(@StringRes resId: Int, vararg args: Any): String {
        return if (args.isEmpty()) {
            context.getString(resId)
        } else {
            context.getString(resId, *args)
        }
    }
}
