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
    var lastUpdateTimestamp: String = "",
    val finished: Boolean = false,
    var wordCount: Int = 0,
    val authors: ArrayList<String> = arrayListOf(),
    val authorsLiked: ArrayList<String> = arrayListOf(),
    val authorsDone: ArrayList<String> = arrayListOf()
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

        fun <T> loading(): FirestoreResponse<T> {
            return FirestoreResponse(QueryStatus.PENDING, null, null)
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

        fun loading(): FirestoreResponseWithoutData {
            return FirestoreResponseWithoutData(QueryStatus.PENDING, null)
        }
    }
}

enum class QueryStatus {
    PENDING,
    SUCCESS,
    ERROR
}

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}