package io.github.fabiantauriello.hiya.ui.main

import io.github.fabiantauriello.hiya.domain.ChatRoom

interface ChatRoomClickListener {
    fun onChatRoomClick(chatRoom: ChatRoom)
}