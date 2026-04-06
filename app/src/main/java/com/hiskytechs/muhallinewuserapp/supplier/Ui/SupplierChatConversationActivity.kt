package com.hiskytechs.muhallinewuserapp.supplier.Ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiskytechs.muhallinewuserapp.databinding.ActivitySupplierChatConversationBinding
import com.hiskytechs.muhallinewuserapp.supplier.Adapters.SupplierMessageAdapter
import com.hiskytechs.muhallinewuserapp.supplier.Data.SupplierData
import com.hiskytechs.muhallinewuserapp.supplier.Utill.initials

class SupplierChatConversationActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierChatConversationBinding
    private lateinit var messageAdapter: SupplierMessageAdapter
    private lateinit var conversationId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierChatConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        conversationId = intent.getStringExtra(EXTRA_CONVERSATION_ID).orEmpty()
        val conversation = SupplierData.findConversation(conversationId)
        if (conversation == null) {
            finish()
            return
        }

        binding.tvAvatar.text = initials(conversation.retailerName)
        binding.tvRetailerName.text = conversation.retailerName
        binding.ivBack.setOnClickListener { finish() }

        messageAdapter = SupplierMessageAdapter(emptyList())
        binding.rvMessages.layoutManager = LinearLayoutManager(this)
        binding.rvMessages.adapter = messageAdapter

        binding.ivSend.setOnClickListener {
            val message = binding.etMessage.text?.toString().orEmpty()
            if (message.isBlank()) return@setOnClickListener
            SupplierData.sendMessage(conversationId, message)
            binding.etMessage.text?.clear()
            loadMessages()
        }

        loadMessages()
    }

    private fun loadMessages() {
        val messages = SupplierData.getMessages(conversationId)
        messageAdapter.updateItems(messages)
        if (messages.isNotEmpty()) {
            binding.rvMessages.scrollToPosition(messages.lastIndex)
        }
    }

    companion object {
        const val EXTRA_CONVERSATION_ID = "extra_conversation_id"
    }
}
