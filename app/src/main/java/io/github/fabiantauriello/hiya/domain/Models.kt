package io.github.fabiantauriello.hiya.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlin.collections.ArrayList

@Parcelize
data class User(
    val id: String,
    // When writing name to firebase, 'name' represents the value entered in
    // profile creation. In all other instances, this property represents the name
    // saved for this contact on the user's phone TODO WHAT??
    val name: String,
    val phoneNumber: String,
    val profileImageUri: String = ""
) : Parcelable

@Parcelize
data class Story(
    var id: String = "",
    var title: String = "",
    var text: String = "",
    val lastUpdateTimestamp: String = "",
    val wordCount: Int = 0,
    val authors: ArrayList<Author> = arrayListOf()
) : Parcelable

@Parcelize
data class Author(
    val userId: String = "",
    var name: String = "",
    val profileImageUri: String = ""
) : Parcelable

enum class FirestoreQueryStatus {
    PENDING,
    SUCCESS,
    FAILURE
}

enum class EditingState {
    NEW_STORY,
    EXISTING_STORY
}