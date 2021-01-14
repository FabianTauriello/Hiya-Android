package io.github.fabiantauriello.hiya.viewmodels

import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.domain.FirestoreResponse
import io.github.fabiantauriello.hiya.domain.User
import io.github.fabiantauriello.hiya.repositories.StoryListsRepository
import io.github.fabiantauriello.hiya.repositories.UserListRepository

class UserListViewModel : ViewModel() {

    private val repo = UserListRepository()

    private val _userListResponse = repo.userListResponse
    val userListResponse: LiveData<FirestoreResponse<ArrayList<User>>>
        get() = _userListResponse

    fun getUsersWhoAreContacts() {
        repo.getUsersWhoAreContacts()
    }

}