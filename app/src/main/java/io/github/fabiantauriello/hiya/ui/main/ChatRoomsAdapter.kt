package io.github.fabiantauriello.hiya.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.databinding.ChatRoomItemBinding
import io.github.fabiantauriello.hiya.domain.ChatRoom

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
        holder.binding.tvChatRoomTitle.text = chatRooms[position].title
        holder.binding.tvChatLastMessage.text = chatRooms[position].lastMessage
        holder.binding.tvLastMessageTimestamp.text = chatRooms[position].lastMessageTimestamp

        // set click listener
        holder.binding.layoutChatRoomItem.setOnClickListener {
            listener.onChatRoomClick(chatRooms[position])
        }
    }

    override fun getItemCount() = chatRooms.size

    fun update(chatRoom: ChatRoom) {
        // update existing chat room or add a new chat room to list
        if (chatRooms.contains(chatRoom)) {
            chatRooms[chatRooms.indexOf(chatRoom)] = chatRoom
        } else {
            chatRooms.add(chatRoom)
        }
        notifyDataSetChanged()
    }


}