package io.github.fabiantauriello.hiya.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.databinding.ChatLogItemBinding
import io.github.fabiantauriello.hiya.domain.Message

class ChatLogAdapter(
    private val messages: ArrayList<Message>
) : RecyclerView.Adapter<ChatLogAdapter.ChatLogItemViewHolder>() {

    class ChatLogItemViewHolder(
        val binding: ChatLogItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatLogItemViewHolder {
        val binding = DataBindingUtil.inflate<ChatLogItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.chat_log_item,
            parent,
            false
        )
        return ChatLogItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatLogItemViewHolder, position: Int) {
        holder.binding.tvText.text = messages[position].text
        holder.binding.tvTimestamp.text = messages[position].timestamp
    }

    override fun getItemCount() = messages.size

    fun update(message: Message) {
        messages.add(message)
        notifyDataSetChanged()
    }

}