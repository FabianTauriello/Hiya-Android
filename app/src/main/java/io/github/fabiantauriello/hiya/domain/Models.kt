package io.github.fabiantauriello.hiya.domain

data class User(
    val name: String,
    val phoneNumber: String,
    val profileImageUri: String,
    val rooms: ArrayList<Pair<String, Boolean>>
)

data class Message(
    val sender: String,
    val text: String,
    val timestamp: String
)

data class FirebaseChatRoom(
    val lastMessage: String,
    val lastMessageTimestamp: String
)

data class ChatRoom(
    val id: String,
    val title: String = "", // default it empty for private chat rooms
    val lastMessage: String,
    val lastMessageTimestamp: String
) {
    override fun equals(other: Any?): Boolean {
        return if (other is ChatRoom) {
            this.id == other.id
        } else {
            false
        }
    }
}