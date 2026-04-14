package com.hiskytechs.muhallinewuserapp.supplier.Ui

import android.os.Bundle
import android.widget.Toast
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
        binding.ivBack.setOnClickListener { finish() }

        messageAdapter = SupplierMessageAdapter(emptyList())
        binding.rvMessages.layoutManager = LinearLayoutManager(this)
        binding.rvMessages.adapter = messageAdapter

        binding.ivSend.setOnClickListener {
            val message = binding.etMessage.text?.toString().orEmpty()
            if (message.isBlank()) return@setOnClickListener
            SupplierData.sendMessage(
                conversationId = conversationId,
                message = message,
                onSuccess = {
                    binding.etMessage.text?.clear()
                    loadMessages()
                },
                onError = { error ->
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            )
        }

        bindConversation()
    }

    private fun bindConversation() {
        val conversation = SupplierData.findConversation(conversationId)
        if (conversation != null) {
            binding.tvAvatar.text = initials(conversation.retailerName)
            binding.tvRetailerName.text = conversation.retailerName
            loadMessages()
            return
        }

        SupplierData.refreshMessages(
            onSuccess = {
                val refreshedConversation = SupplierData.findConversation(conversationId)
                if (refreshedConversation == null) {
                    finish()
                } else {
                    binding.tvAvatar.text = initials(refreshedConversation.retailerName)
                    binding.tvRetailerName.text = refreshedConversation.retailerName
                    loadMessages()
                }
            },
            onError = { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                finish()
            }
        )
    }

    private fun loadMessages() {
        SupplierData.loadConversation(
            conversationId = conversationId,
            onSuccess = { messages ->
                messageAdapter.updateItems(messages)
                if (messages.isNotEmpty()) {
                    binding.rvMessages.scrollToPosition(messages.lastIndex)
                }
            },
            onError = { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    companion object {
        const val EXTRA_CONVERSATION_ID = "extra_conversation_id"
    }
}
