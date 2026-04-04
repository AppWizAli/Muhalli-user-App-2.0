package com.hiskytechs.muhallinewuserapp.Ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiskytechs.muhallinewuserapp.Adapters.ChatMessageAdapter
import com.hiskytechs.muhallinewuserapp.Data.AppData
import com.hiskytechs.muhallinewuserapp.Models.ChatMessage
import com.hiskytechs.muhallinewuserapp.Models.ChatMessageType
import com.hiskytechs.muhallinewuserapp.Models.ChatParticipant
import com.hiskytechs.muhallinewuserapp.databinding.ActivityChatConversationBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatConversationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatConversationBinding
    private lateinit var messageAdapter: ChatMessageAdapter
    private val messages = mutableListOf<ChatMessage>()
    private var supplierName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supplierName = intent.getStringExtra(EXTRA_SUPPLIER_NAME).orEmpty()
        val supplierLocation = intent.getStringExtra(EXTRA_SUPPLIER_LOCATION).orEmpty()

        binding.toolbar.title = supplierName
        binding.tvSupplierLocation.text = supplierLocation
        binding.tvSupplierStatus.text = "Online for media and voice updates"
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupMessages()
        setupComposer()
    }

    private fun setupMessages() {
        messages.clear()
        messages.addAll(AppData.conversationForSupplier(supplierName))
        messageAdapter = ChatMessageAdapter(
            supplierName = supplierName.ifBlank { "Supplier" },
            messages = messages
        )
        binding.rvMessages.layoutManager = LinearLayoutManager(this)
        binding.rvMessages.adapter = messageAdapter
        binding.rvMessages.post {
            binding.rvMessages.scrollToPosition((messageAdapter.itemCount - 1).coerceAtLeast(0))
        }
    }

    private fun setupComposer() {
        binding.etMessage.doAfterTextChanged { text ->
            binding.layoutSendAction.alpha = if (text.isNullOrBlank()) 0.65f else 1f
        }

        binding.layoutSendAction.setOnClickListener {
            val text = binding.etMessage.text?.toString()?.trim().orEmpty()
            if (text.isBlank()) return@setOnClickListener

            appendMessage(
                ChatMessage(
                    id = "buyer-text-${System.currentTimeMillis()}",
                    participant = ChatParticipant.BUYER,
                    type = ChatMessageType.TEXT,
                    body = text,
                    timeLabel = currentTimeLabel()
                )
            )
            binding.etMessage.setText("")
        }

        binding.layoutMediaAction.setOnClickListener {
            appendMessage(
                ChatMessage(
                    id = "buyer-media-${System.currentTimeMillis()}",
                    participant = ChatParticipant.BUYER,
                    type = ChatMessageType.MEDIA,
                    body = "Attached a product photo for confirmation.",
                    timeLabel = currentTimeLabel(),
                    mediaLabel = "Photo",
                    mediaTitle = "Buyer media attachment",
                    mediaSubtitle = "JPG image - 2.1 MB"
                )
            )
        }

        binding.layoutVoiceAction.setOnClickListener {
            appendMessage(
                ChatMessage(
                    id = "buyer-voice-${System.currentTimeMillis()}",
                    participant = ChatParticipant.BUYER,
                    type = ChatMessageType.VOICE,
                    body = "Recorded a quick voice note for quantity changes.",
                    timeLabel = currentTimeLabel(),
                    voiceDuration = "0:24",
                    voiceStatus = "Buyer voice note",
                    voiceProgress = 76
                )
            )
        }
    }

    private fun appendMessage(message: ChatMessage) {
        messageAdapter.addMessage(message)
        binding.rvMessages.post {
            binding.rvMessages.scrollToPosition(messageAdapter.itemCount - 1)
        }
    }

    private fun currentTimeLabel(): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }

    companion object {
        const val EXTRA_SUPPLIER_NAME = "supplier_name"
        const val EXTRA_SUPPLIER_LOCATION = "supplier_location"
    }
}
