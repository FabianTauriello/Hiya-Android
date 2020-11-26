package io.github.fabiantauriello.hiya.domain

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: String,
    // When writing name to firebase, 'name' represents the value entered in
    // profile creation. In all other instances, this property represents the name
    // saved for this contact on the user's phone
    val name: String,
    val phoneNumber: String,
    val profileImageUri: String? = null
) : Parcelable

@Parcelize
data class ChatRoom(
    val id: String?,
    val participants: ArrayList<Participant>,
    val lastMessage: String?,
    val lastMessageTimestamp: String?
) : Parcelable

@Parcelize
data class Participant(
    val userId: String,
    var name: String
) : Parcelable

data class Message(
    val text: String,
    val timestamp: String,
    val sender: String
)

enum class FirestoreQueryStatus {
    SUCCESS,
    FAILURE,
    LOADING
}