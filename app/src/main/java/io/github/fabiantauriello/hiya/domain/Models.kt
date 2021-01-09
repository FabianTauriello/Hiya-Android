package io.github.fabiantauriello.hiya.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// initialized with empty values to get deserialization from FireStore to work
@Parcelize
data class User(
    val id: String = "",
    // When writing name to firebase, 'name' represents the value entered in
    // profile creation. In all other instances, this property represents the name
    // saved for this contact on the user's phone TODO WHAT??
    val name: String = "",
    val phoneNumber: String = "",
    val profilePic: String = ""
) : Parcelable

// TODO consider abstracting author information into separate collection. will work with group stories better
@Parcelize
data class Story(
    var id: String = "",
    var title: String = "",
    var text: String = "",
    var lastUpdateTimestamp: String = "",
    var wordCount: Int = 0,
    var nextTurn: String = "",
    val authorIds: ArrayList<String> = arrayListOf(),
    val authors: ArrayList<Author> = arrayListOf()
) : Parcelable

@Parcelize
data class Author(
    val id: String = "",
    val name: String = "",
    val picture: String = "",
    val liked: Boolean = false,
    val done: Boolean = false
) : Parcelable

// Use this for when you want to know the status of a Firebase query WITH the data.
data class FirestoreResponse<T>(var queryStatus: QueryStatus, var data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): FirestoreResponse<T> {
            return FirestoreResponse(QueryStatus.SUCCESS, data, null)
        }
        fun <T> error(msg: String): FirestoreResponse<T> {
            return FirestoreResponse(QueryStatus.ERROR,  null, msg)
        }
    }
}

// Use this for when you want to know the status of a Firebase query WITHOUT data.
data class FirestoreResponseWithoutData(var queryStatus: QueryStatus, val message: String?) {
    companion object {
        fun success(): FirestoreResponseWithoutData {
            return FirestoreResponseWithoutData(QueryStatus.SUCCESS, null)
        }
        fun error(msg: String): FirestoreResponseWithoutData {
            return FirestoreResponseWithoutData(QueryStatus.ERROR,  msg)
        }
    }
}

enum class QueryStatus {
    SUCCESS,
    ERROR
}