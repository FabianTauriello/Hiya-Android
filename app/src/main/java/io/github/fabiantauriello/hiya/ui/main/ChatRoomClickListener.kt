package io.github.fabiantauriello.hiya.ui.main

import io.github.fabiantauriello.hiya.domain.ChatRoomItem

interface ChatRoomClickListener {
    fun onChatRoomClick(chatRoom: ChatRoomItem)
}