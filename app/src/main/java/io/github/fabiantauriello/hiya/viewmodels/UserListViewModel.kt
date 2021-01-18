package io.github.fabiantauriello.hiya.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.fabiantauriello.hiya.domain.FirestoreResponse
import io.github.fabiantauriello.hiya.domain.User
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