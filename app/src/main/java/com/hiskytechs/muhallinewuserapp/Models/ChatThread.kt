package com.hiskytechs.muhallinewuserapp.Models

data class ChatThread(
    val supplierName: String,
    val supplierLocation: String,
    val lastMessage: String,
    val lastSeen: String,
    val unreadCount: Int = 0
)
