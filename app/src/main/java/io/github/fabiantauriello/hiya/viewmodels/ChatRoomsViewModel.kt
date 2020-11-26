package io.github.fabiantauriello.hiya.viewmodels

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.google.firebase.database.core.Repo
import io.github.fabiantauriello.hiya.domain.ChatRoom
import io.github.fabiantauriello.hiya.domain.User
import io.github.fabiantauriello.hiya.repository.Repository
import io.github.fabiantauriello.hiya.ui.main.ChatRoomsFragmentDirections


class ChatRoomsViewModel : ViewModel() {

    private val _contacts = Repository.contacts
    val contacts: LiveData<ArrayList<User>>
        get() = _contacts

    private val _rooms = Repository.rooms
    val rooms: LiveData<ArrayList<ChatRoom>>
        get() = _rooms

    init {
        Repository.getUsersMatchingContactsOnDevice()
        Repository.getChatRooms()
    }

    fun onRoomClick(room: ChatRoom, view: View) {
        val action =
            ChatRoomsFragmentDirections.actionChatRoomsFragmentToChatLogFragment(room.id)
        view.findNavController().navigate(action)
    }

}