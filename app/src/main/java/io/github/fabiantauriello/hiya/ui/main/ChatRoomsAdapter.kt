package io.github.fabiantauriello.hiya.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.databinding.ChatRoomItemBinding
import io.github.fabiantauriello.hiya.domain.ChatRoom
import io.github.fabiantauriello.hiya.viewmodels.ChatRoomsViewModel

class ChatRoomsAdapter(
    private val chatRooms: ArrayList<ChatRoom>,
    private val viewModel: ChatRoomsViewModel
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
        // bind data to display
        holder.binding.tvChatRoomTitle.text =
            chatRooms[position].participants.filter { it.userId != Hiya.userId }[0].name
        holder.binding.tvChatLastMessage.text = chatRooms[position].lastMessage
        holder.binding.tvLastMessageTimestamp.text = chatRooms[position].lastMessageTimestamp

        // set data binding variables
        holder.binding.viewModel = viewModel
        holder.binding.room = chatRooms[position]
    }

    override fun getItemCount() = chatRooms.size

    fun update(newChatRooms: ArrayList<ChatRoom>) {
        chatRooms.clear()
        chatRooms.addAll(newChatRooms)
    }

    fun getRooms(): ArrayList<ChatRoom> {
        return chatRooms
    }


}