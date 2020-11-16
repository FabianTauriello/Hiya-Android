package io.github.fabiantauriello.hiya.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class User(
    val name: String,
    val phoneNumber: String,
    val profileImageUri: String? = null
)

@Parcelize
data class Contact(
    val id: String,
    val name: String,
    val profileImageUri: String?
) : Parcelable

data class Message(
    val text: String,
    val timestamp: String,
    val sender: String
)

sealed class ChatRoom(
    val participants: ArrayList<String>,
    val lastMessage: String?,
    val lastMessageTimestamp: String?
)

class PrivateChatRoom(
    participants: ArrayList<String>,
    lastMessage: String?,
    lastMessageTimestamp: String?
) : ChatRoom(participants, lastMessage, lastMessageTimestamp)

//class GroupChatRoom(
//    val name: String,
//    val photoUri: String,
//    participants: ArrayList<String>,
//    lastMessage: String?,
//    lastMessageTimestamp: String?
//) : ChatRoom(participants, lastMessage, lastMessageTimestamp)

// TODO for rooms rv only
data class ChatRoomItem(
    val id: String,
    val lastMessage: String,
    val lastMessageTimestamp: String
)