package io.github.fabiantauriello.hiya.domain

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

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

@Parcelize
data class ChatRoom(
    val id: String?,
    val participants: ArrayList<String>,
    val lastMessage: String?,
    val lastMessageTimestamp: String?
) : Parcelable
