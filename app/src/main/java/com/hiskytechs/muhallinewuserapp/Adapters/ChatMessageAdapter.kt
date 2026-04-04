package com.hiskytechs.muhallinewuserapp.Adapters

import android.content.res.ColorStateList
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hiskytechs.muhallinewuserapp.Models.ChatMessage
import com.hiskytechs.muhallinewuserapp.Models.ChatMessageType
import com.hiskytechs.muhallinewuserapp.Models.ChatParticipant
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.databinding.ItemChatMessageBinding

class ChatMessageAdapter(
    private val supplierName: String,
    private val messages: MutableList<ChatMessage>
) : RecyclerView.Adapter<ChatMessageAdapter.ChatMessageViewHolder>() {

    inner class ChatMessageViewHolder(val binding: ItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val binding = ItemChatMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatMessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        val message = messages[position]
        val isBuyer = message.participant == ChatParticipant.BUYER
        val context = holder.binding.root.context

        holder.binding.apply {
            (layoutBubbleContainer.layoutParams as FrameLayout.LayoutParams).gravity =
                if (isBuyer) Gravity.END else Gravity.START

            tvSenderRole.text = if (isBuyer) context.getString(R.string.you_label) else supplierName
            tvSenderRole.textAlignment = if (isBuyer) {
                View.TEXT_ALIGNMENT_TEXT_END
            } else {
                View.TEXT_ALIGNMENT_TEXT_START
            }
            tvSenderRole.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (isBuyer) R.color.primary else R.color.status_delivered_text
                )
            )

            layoutBubble.setBackgroundResource(
                if (isBuyer) R.drawable.bg_chat_bubble_me else R.drawable.bg_chat_bubble_other
            )
            layoutMediaCard.setBackgroundResource(
                if (isBuyer) R.drawable.bg_chat_media_card_me else R.drawable.bg_chat_media_card_other
            )
            layoutMediaThumb.setBackgroundResource(
                if (isBuyer) R.drawable.bg_chat_media_thumb_me else R.drawable.bg_chat_media_thumb_other
            )
            layoutVoiceCard.setBackgroundResource(
                if (isBuyer) R.drawable.bg_chat_voice_card_me else R.drawable.bg_chat_voice_card_other
            )
            ivVoiceIcon.setBackgroundResource(
                if (isBuyer) R.drawable.bg_chat_media_thumb_me else R.drawable.bg_chat_media_thumb_other
            )

            ivMediaIcon.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    if (isBuyer) R.color.white else R.color.primary
                )
            )
            ivVoiceIcon.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    if (isBuyer) R.color.white else R.color.primary
                )
            )
            pbVoiceWave.progressTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    if (isBuyer) R.color.white else R.color.primary
                )
            )
            pbVoiceWave.progressBackgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    if (isBuyer) R.color.white else R.color.status_transit_bg
                )
            )

            val bodyColor = ContextCompat.getColor(
                context,
                if (isBuyer) R.color.white else R.color.text_dark
            )
            val secondaryColor = ContextCompat.getColor(
                context,
                if (isBuyer) R.color.white else R.color.text_grey
            )

            tvMessageBody.setTextColor(bodyColor)
            tvMediaLabel.setTextColor(bodyColor)
            tvMediaTitle.setTextColor(bodyColor)
            tvMediaSubtitle.setTextColor(secondaryColor)
            tvVoiceStatus.setTextColor(bodyColor)
            tvVoiceDuration.setTextColor(secondaryColor)
            tvTime.setTextColor(secondaryColor)

            tvMessageBody.visibility = if (message.body.isBlank()) View.GONE else View.VISIBLE
            tvMessageBody.text = message.body

            layoutMediaCard.visibility =
                if (message.type == ChatMessageType.MEDIA) View.VISIBLE else View.GONE
            if (message.type == ChatMessageType.MEDIA) {
                tvMediaLabel.text = message.mediaLabel
                tvMediaTitle.text = message.mediaTitle
                tvMediaSubtitle.text = message.mediaSubtitle
            }

            layoutVoiceCard.visibility =
                if (message.type == ChatMessageType.VOICE) View.VISIBLE else View.GONE
            if (message.type == ChatMessageType.VOICE) {
                tvVoiceStatus.text = message.voiceStatus
                tvVoiceDuration.text = message.voiceDuration
                pbVoiceWave.progress = message.voiceProgress.coerceIn(8, 100)
            }

            tvTime.text = message.timeLabel
        }
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.lastIndex)
    }
}
