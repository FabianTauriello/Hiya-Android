package io.github.fabiantauriello.hiya.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.fabiantauriello.hiya.domain.ChatRoom
import io.github.fabiantauriello.hiya.domain.User
import io.github.fabiantauriello.hiya.repository.Repository

class ChatRoomsViewModel : ViewModel() {

    private val repository = Repository()

    private val _contacts = repository.contacts
    val contacts: LiveData<ArrayList<User>>
        get() = _contacts

    private val _rooms = repository.rooms
    val rooms: LiveData<ArrayList<ChatRoom>>
        get() = _rooms

    init {
        repository.getUsersMatchingContactsOnDevice()
        repository.getChatRooms()
    }

}