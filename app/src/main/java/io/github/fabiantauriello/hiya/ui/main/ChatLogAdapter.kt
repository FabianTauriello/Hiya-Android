package io.github.fabiantauriello.hiya.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.databinding.ChatLogItemBinding
import io.github.fabiantauriello.hiya.domain.Message
import io.github.fabiantauriello.hiya.util.Utils

class ChatLogAdapter(
    private val messages: ArrayList<Message>
) : RecyclerView.Adapter<ChatLogAdapter.ChatLogItemViewHolder>() {

    class ChatLogItemViewHolder(
        val binding: ChatLogItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatLogItemViewHolder {
        val binding = DataBindingUtil.inflate<io.github.fabiantauriello.hiya.databinding.ChatLogItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.chat_log_item,
            parent,
            false
        )
        return ChatLogItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatLogItemViewHolder, position: Int) {
        // change location of message view depending on who the sender is
        val paramsToChange = (holder.binding.layoutMessage.layoutParams as ConstraintLayout.LayoutParams)
        if (messages[position].sender != Hiya.userId) {
            holder.binding.layoutMessage.setBackgroundResource(R.drawable.bg_receiver_msg)
            paramsToChange.horizontalBias = 0f
        } else {
            holder.binding.layoutMessage.setBackgroundResource(R.drawable.bg_sender_msg)
            paramsToChange.horizontalBias = 1f
        }
        holder.binding.layoutMessage.layoutParams = paramsToChange
        holder.binding.tvText.text = messages[position].text
        holder.binding.tvTimestamp.text = messages[position].timestamp
    }

    override fun getItemCount() = messages.size

    fun update(newMessage: Message) {
        messages.add(newMessage)
        notifyDataSetChanged()
    }



}