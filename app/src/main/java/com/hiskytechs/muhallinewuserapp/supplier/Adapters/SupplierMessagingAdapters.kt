package com.hiskytechs.muhallinewuserapp.supplier.Adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.databinding.ItemSupplierConversationBinding
import com.hiskytechs.muhallinewuserapp.databinding.ItemSupplierMessageBinding
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierChatMessage
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierConversation
import com.hiskytechs.muhallinewuserapp.supplier.Utill.initials

class SupplierConversationAdapter(
    private var items: List<SupplierConversation>,
    private val onClick: (SupplierConversation) -> Unit
) : RecyclerView.Adapter<SupplierConversationAdapter.SupplierConversationViewHolder>() {

    inner class SupplierConversationViewHolder(
        private val binding: ItemSupplierConversationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SupplierConversation) {
            val context = binding.root.context
            binding.tvAvatar.text = initials(item.retailerName)
            binding.tvAvatar.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(context, item.accentColorRes)
            )
            binding.tvRetailerName.text = item.retailerName
            binding.tvLastMessage.text = item.lastMessage
            binding.tvTime.text = item.timeLabel
            binding.tvUnread.visibility = if (item.unreadCount > 0) android.view.View.VISIBLE else android.view.View.GONE
            binding.tvUnread.text = item.unreadCount.toString()
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplierConversationViewHolder {
        val binding = ItemSupplierConversationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SupplierConversationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SupplierConversationViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<SupplierConversation>) {
        items = newItems
        notifyDataSetChanged()
    }
}

class SupplierMessageAdapter(
    private var items: List<SupplierChatMessage>
) : RecyclerView.Adapter<SupplierMessageAdapter.SupplierMessageViewHolder>() {

    inner class SupplierMessageViewHolder(
        private val binding: ItemSupplierMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SupplierChatMessage) {
            val context = binding.root.context
            binding.tvMessage.text = item.message
            binding.tvTime.text = item.timeLabel
            binding.layoutRoot.gravity = if (item.isMine) Gravity.END else Gravity.START
            binding.layoutBubble.setBackgroundResource(
                if (item.isMine) {
                    R.drawable.bg_supplier_message_me
                } else {
                    R.drawable.bg_supplier_message_other
                }
            )
            val messageColor = if (item.isMine) Color.WHITE else ContextCompat.getColor(context, R.color.text_dark)
            val timeColor = if (item.isMine) {
                ContextCompat.getColor(context, R.color.white)
            } else {
                ContextCompat.getColor(context, R.color.supplier_text_secondary)
            }
            binding.tvMessage.setTextColor(messageColor)
            binding.tvTime.setTextColor(timeColor)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplierMessageViewHolder {
        val binding = ItemSupplierMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SupplierMessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SupplierMessageViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<SupplierChatMessage>) {
        items = newItems
        notifyDataSetChanged()
    }
}
