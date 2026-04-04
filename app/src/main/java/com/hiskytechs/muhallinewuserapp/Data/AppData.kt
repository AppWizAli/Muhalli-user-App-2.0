package com.hiskytechs.muhallinewuserapp.Data

import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.Models.BuyerProfile
import com.hiskytechs.muhallinewuserapp.Models.Category
import com.hiskytechs.muhallinewuserapp.Models.ChatMessage
import com.hiskytechs.muhallinewuserapp.Models.ChatMessageType
import com.hiskytechs.muhallinewuserapp.Models.ChatParticipant
import com.hiskytechs.muhallinewuserapp.Models.ChatThread
import com.hiskytechs.muhallinewuserapp.Models.Order
import com.hiskytechs.muhallinewuserapp.Models.Supplier
import com.hiskytechs.muhallinewuserapp.Models.CartItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppData {

    val buyerProfile = BuyerProfile(
        storeName = "My Retail Store",
        buyerName = "Sana Ahmed",
        email = "mystore@email.com",
        phoneNumber = "+971 50 123 4567",
        city = "Dubai, UAE",
        memberSince = "Joined March 2024"
    )

    val categories = listOf(
        Category("Snacks", "245 products", R.drawable.ic_grid_view_24, "#FFF8E1"),
        Category("Chips", "189 products", R.drawable.ic_grid_view_24, "#E3F2FD"),
        Category("Mineral Water", "158 products", R.drawable.ic_grid_view_24, "#E0F2F1"),
        Category("Groceries", "567 products", R.drawable.ic_grid_view_24, "#FCE4EC"),
        Category("Drinks", "234 products", R.drawable.ic_grid_view_24, "#E8EAF6"),
        Category("Fresh Fruits", "145 products", R.drawable.ic_grid_view_24, "#FFEBEE"),
        Category("Meat", "98 products", R.drawable.ic_grid_view_24, "#FFF3E0"),
        Category("Coffee & Tea", "123 products", R.drawable.ic_grid_view_24, "#F3E5F5")
    )

    val suppliers = listOf(
        Supplier(
            name = "Al-Hamd Wholes",
            location = "Jebel Ali",
            productCount = "250 products",
            deliveryTime = "1-2 days",
            minAmount = "$500",
            minQty = "20",
            categories = listOf("Snacks", "Groceries"),
            headerColor = "#EEF4FF"
        ),
        Supplier(
            name = "Al-Sadiqa Company",
            location = "Sharjah",
            productCount = "180 products",
            deliveryTime = "Same day",
            minAmount = "$300",
            minQty = "15",
            categories = listOf("Drinks", "Mineral Water"),
            headerColor = "#EFFBF3"
        ),
        Supplier(
            name = "Al-Far Imports",
            location = "Dubai",
            productCount = "320 products",
            deliveryTime = "2-3 days",
            minAmount = "$1000",
            minQty = "50",
            categories = listOf("Chips", "Snacks"),
            headerColor = "#FFF6EC"
        ),
        Supplier(
            name = "Premium Foods LLC",
            location = "Dubai",
            productCount = "215 products",
            deliveryTime = "1-2 days",
            minAmount = "$400",
            minQty = "25",
            categories = listOf("Groceries", "Coffee & Tea"),
            headerColor = "#F5F2FF"
        ),
        Supplier(
            name = "Global Trade Supplies",
            location = "Abu Dhabi",
            productCount = "298 products",
            deliveryTime = "Same day",
            minAmount = "$600",
            minQty = "30",
            categories = listOf("Fresh Fruits", "Drinks"),
            headerColor = "#FFF1F6"
        ),
        Supplier(
            name = "Pure Spring Distributors",
            location = "Ajman",
            productCount = "164 products",
            deliveryTime = "1 day",
            minAmount = "$350",
            minQty = "18",
            categories = listOf("Mineral Water", "Drinks"),
            headerColor = "#EAF9F8"
        ),
        Supplier(
            name = "Prime Cuts Market",
            location = "Lahore",
            productCount = "104 products",
            deliveryTime = "Next day",
            minAmount = "$850",
            minQty = "12",
            categories = listOf("Meat", "Groceries"),
            headerColor = "#FFF3EE"
        ),
        Supplier(
            name = "Desert Brew Traders",
            location = "Karachi",
            productCount = "142 products",
            deliveryTime = "2 days",
            minAmount = "$450",
            minQty = "22",
            categories = listOf("Coffee & Tea", "Snacks"),
            headerColor = "#F4EEFF"
        )
    )

    private val orderHistory = mutableListOf(
        Order("ORD-12345", "March 14, 2026", "Delivered", "Al-Hamd Wholes", 3, 231.97),
        Order("ORD-12344", "March 12, 2026", "In Transit", "Al-Sadiqa Company", 2, 269.98, "March 17, 2026"),
        Order("ORD-12343", "March 10, 2026", "Processing", "Al-Far Imports", 5, 445.50),
        Order("ORD-12342", "March 8, 2026", "Delivered", "Premium Foods LLC", 4, 389.99),
        Order("ORD-12341", "March 5, 2026", "Cancelled", "Global Trade Supplies", 2, 178.00)
    )

    val chatThreads = listOf(
        ChatThread(
            supplierName = "Al-Hamd Wholes",
            supplierLocation = "Jebel Ali",
            lastMessage = "Your snacks order can be delivered tomorrow morning.",
            lastSeen = "2 min ago",
            unreadCount = 2
        ),
        ChatThread(
            supplierName = "Premium Foods LLC",
            supplierLocation = "Dubai",
            lastMessage = "Fresh stock for coffee and tea is now available.",
            lastSeen = "15 min ago"
        ),
        ChatThread(
            supplierName = "Global Trade Supplies",
            supplierLocation = "Abu Dhabi",
            lastMessage = "We can arrange same-day dispatch for your next order.",
            lastSeen = "1 hr ago",
            unreadCount = 1
        ),
        ChatThread(
            supplierName = "Pure Spring Distributors",
            supplierLocation = "Ajman",
            lastMessage = "Mineral water cartons are packed and ready.",
            lastSeen = "Yesterday"
        )
    )

    private val supplierConversations = mapOf(
        "al-hamd wholes" to listOf(
            ChatMessage(
                id = "alhamd-1",
                participant = ChatParticipant.SUPPLIER,
                type = ChatMessageType.TEXT,
                body = "Hello! We packed your snacks order and can dispatch tomorrow morning.",
                timeLabel = "09:10 AM"
            ),
            ChatMessage(
                id = "alhamd-2",
                participant = ChatParticipant.BUYER,
                type = ChatMessageType.MEDIA,
                body = "Sharing the shelf display image so you can match the carton labeling.",
                timeLabel = "09:12 AM",
                mediaLabel = "Photo",
                mediaTitle = "Store shelf update",
                mediaSubtitle = "PNG image - 2.4 MB"
            ),
            ChatMessage(
                id = "alhamd-3",
                participant = ChatParticipant.SUPPLIER,
                type = ChatMessageType.VOICE,
                body = "Please review the dispatch timing in this voice note.",
                timeLabel = "09:15 AM",
                voiceDuration = "0:18",
                voiceStatus = "Voice message from supplier",
                voiceProgress = 48
            ),
            ChatMessage(
                id = "alhamd-4",
                participant = ChatParticipant.SUPPLIER,
                type = ChatMessageType.MEDIA,
                body = "Invoice copy is attached for approval.",
                timeLabel = "09:18 AM",
                mediaLabel = "Invoice",
                mediaTitle = "March invoice copy",
                mediaSubtitle = "PDF document - 1 page"
            ),
            ChatMessage(
                id = "alhamd-5",
                participant = ChatParticipant.BUYER,
                type = ChatMessageType.TEXT,
                body = "Looks good. Please keep the same rates and deliver before noon.",
                timeLabel = "09:20 AM"
            )
        ),
        "premium foods llc" to listOf(
            ChatMessage(
                id = "premium-1",
                participant = ChatParticipant.SUPPLIER,
                type = ChatMessageType.TEXT,
                body = "Fresh coffee stock is available now. Do you want us to reserve 20 boxes?",
                timeLabel = "11:05 AM"
            ),
            ChatMessage(
                id = "premium-2",
                participant = ChatParticipant.BUYER,
                type = ChatMessageType.VOICE,
                body = "Sending a quick voice note with the revised quantity.",
                timeLabel = "11:09 AM",
                voiceDuration = "0:22",
                voiceStatus = "Buyer voice note",
                voiceProgress = 70
            ),
            ChatMessage(
                id = "premium-3",
                participant = ChatParticipant.SUPPLIER,
                type = ChatMessageType.MEDIA,
                body = "Here is the latest product shot from today's batch.",
                timeLabel = "11:14 AM",
                mediaLabel = "Product Image",
                mediaTitle = "Coffee cartons preview",
                mediaSubtitle = "JPG image - 1.7 MB"
            )
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
        return supplierConversations[supplierName.lowercase()]
            ?: defaultConversation(supplierName)
    }

    private fun defaultConversation(supplierName: String): List<ChatMessage> {
        return listOf(
            ChatMessage(
                id = "${supplierName}-1",
                participant = ChatParticipant.SUPPLIER,
                type = ChatMessageType.TEXT,
                body = "Welcome back. We are ready to share pricing, photos, and delivery updates for your next order.",
                timeLabel = "08:45 AM"
            ),
            ChatMessage(
                id = "${supplierName}-2",
                participant = ChatParticipant.BUYER,
                type = ChatMessageType.MEDIA,
                body = "Please review this product reference before you confirm the carton mix.",
                timeLabel = "08:49 AM",
                mediaLabel = "Reference",
                mediaTitle = "Buyer attachment",
                mediaSubtitle = "Image file - 2.0 MB"
            ),
            ChatMessage(
                id = "${supplierName}-3",
                participant = ChatParticipant.SUPPLIER,
                type = ChatMessageType.VOICE,
                body = "I also recorded a voice note with dispatch timing.",
                timeLabel = "08:52 AM",
                voiceDuration = "0:16",
                voiceStatus = "Supplier voice note",
                voiceProgress = 42
            )
        )
    }

    fun addOrderFromCart(items: List<CartItem>, totalAmount: Double): Order {
        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val supplierName = items.map { it.supplier }.distinct().let { names ->
            if (names.size == 1) names.first() else "Multiple Suppliers"
        }
        val order = Order(
            orderId = "ORD-${13000 + orderHistory.size}",
            date = dateFormat.format(Date()),
            status = "Processing",
            supplier = supplierName,
            itemsCount = items.sumOf { it.quantity },
            totalAmount = totalAmount
        )
        orderHistory.add(0, order)
        return order
    }
}
