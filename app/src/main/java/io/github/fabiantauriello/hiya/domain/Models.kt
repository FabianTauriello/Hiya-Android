package io.github.fabiantauriello.hiya.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

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

data class FirestoreResponse<T>(var queryStatus: QueryStatus, var data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): FirestoreResponse<T> {
            return FirestoreResponse(QueryStatus.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): FirestoreResponse<T> {
            return FirestoreResponse(QueryStatus.ERROR, data, msg)
        }

        fun <T> loading(data: T?): FirestoreResponse<T> {
            return FirestoreResponse(QueryStatus.PENDING, data, null)
        }
    }
}

enum class QueryStatus {
    PENDING,
    SUCCESS,
    ERROR
}

enum class EditingState {
    NEW_STORY,
    EXISTING_STORY
}