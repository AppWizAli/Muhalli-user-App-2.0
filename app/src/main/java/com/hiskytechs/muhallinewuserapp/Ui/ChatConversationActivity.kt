package com.hiskytechs.muhallinewuserapp.Ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiskytechs.muhallinewuserapp.Adapters.ChatMessageAdapter
import com.hiskytechs.muhallinewuserapp.Data.AppData
import com.hiskytechs.muhallinewuserapp.Models.ChatMessage
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.databinding.ActivityChatConversationBinding

class ChatConversationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatConversationBinding
    private lateinit var messageAdapter: ChatMessageAdapter
    private val messages = mutableListOf<ChatMessage>()
    private var supplierName: String = ""
    private var threadId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        threadId = intent.getIntExtra(EXTRA_THREAD_ID, 0)
        supplierName = intent.getStringExtra(EXTRA_SUPPLIER_NAME).orEmpty()
        val supplierLocation = intent.getStringExtra(EXTRA_SUPPLIER_LOCATION).orEmpty()

        binding.toolbar.title = supplierName
        binding.tvSupplierLocation.text = supplierLocation
        binding.tvSupplierStatus.text = getString(R.string.online_for_media_and_voice_updates)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupMessagesRecycler()
        setupComposer()
        resolveConversation()
    }

    private fun setupMessagesRecycler() {
        messageAdapter = ChatMessageAdapter(
            supplierName = supplierName.ifBlank { getString(R.string.supplier_fallback) },
            messages = messages
        )
        binding.rvMessages.layoutManager = LinearLayoutManager(this)
        binding.rvMessages.adapter = messageAdapter
    }

    private fun resolveConversation() {
        if (threadId > 0) {
            loadConversation()
            return
        }

        val cachedThread = AppData.findThreadBySupplierName(supplierName)
        if (cachedThread != null) {
            threadId = cachedThread.threadId
            loadConversation()
            return
        }

        AppData.loadChats(
            onSuccess = { threads ->
                val match = threads.firstOrNull {
                    it.supplierName.equals(supplierName, ignoreCase = true)
                }
                if (match == null) {
                    Toast.makeText(this, getString(R.string.no_messages_yet), Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    threadId = match.threadId
                    loadConversation()
                }
            },
            onError = { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                finish()
            }
        )
    }

    private fun loadConversation() {
        AppData.loadConversation(
            threadId = threadId,
            onSuccess = { conversation ->
                messages.clear()
                messages.addAll(conversation)
                messageAdapter.notifyDataSetChanged()
                binding.rvMessages.post {
                    binding.rvMessages.scrollToPosition((messageAdapter.itemCount - 1).coerceAtLeast(0))
                }
            },
            onError = { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun scrollToBottom() {
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
            if (threadId <= 0) return@setOnClickListener

            AppData.sendMessage(
                threadId = threadId,
                message = text,
                onSuccess = { updatedMessages ->
                    messages.clear()
                    messages.addAll(updatedMessages)
                    messageAdapter.notifyDataSetChanged()
                    binding.etMessage.setText("")
                    scrollToBottom()
                },
                onError = { message ->
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.layoutMediaAction.setOnClickListener {
            Toast.makeText(this, getString(R.string.media_coming_soon), Toast.LENGTH_SHORT).show()
        }

        binding.layoutVoiceAction.setOnClickListener {
            Toast.makeText(this, getString(R.string.voice_notes_coming_soon), Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_THREAD_ID = "thread_id"
        const val EXTRA_SUPPLIER_NAME = "supplier_name"
        const val EXTRA_SUPPLIER_LOCATION = "supplier_location"
    }
}
