package io.github.fabiantauriello.hiya.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.databinding.ChatRoomItemBinding
import io.github.fabiantauriello.hiya.domain.ChatRoom
import kotlin.collections.ArrayList

class ChatRoomsAdapter(
    private val chatRooms: ArrayList<ChatRoom>,
    private val listener: ChatRoomClickListener
) : RecyclerView.Adapter<ChatRoomsAdapter.ChatRoomItemViewHolder>() {

    class ChatRoomItemViewHolder(
        val binding: ChatRoomItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomItemViewHolder {
        val binding = DataBindingUtil.inflate<ChatRoomItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.chat_room_item,
            parent,
            false
        )
        return ChatRoomItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatRoomItemViewHolder, position: Int) {
        holder.binding.tvChatRoomTitle.text = chatRooms[position].participants.filter { it != Hiya.userId }[0]
        holder.binding.tvChatLastMessage.text = chatRooms[position].lastMessage
        holder.binding.tvLastMessageTimestamp.text = chatRooms[position].lastMessageTimestamp

        // set click listener
        holder.binding.layoutChatRoomItem.setOnClickListener {
            listener.onChatRoomClick(chatRooms[position])
        }
    }

    override fun getItemCount() = chatRooms.size

    fun replaceAllRooms(newChatRooms: ArrayList<ChatRoom>) {
        chatRooms.clear()
        chatRooms.addAll(newChatRooms)
    }

    fun getRooms(): ArrayList<ChatRoom> {
        return chatRooms
    }


}