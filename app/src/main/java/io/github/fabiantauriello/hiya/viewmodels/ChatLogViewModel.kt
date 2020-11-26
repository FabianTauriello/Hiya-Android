package io.github.fabiantauriello.hiya.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.core.Repo
import io.github.fabiantauriello.hiya.domain.FirestoreQueryStatus
import io.github.fabiantauriello.hiya.domain.Message
import io.github.fabiantauriello.hiya.domain.Participant
import io.github.fabiantauriello.hiya.domain.User
import io.github.fabiantauriello.hiya.repository.Repository

class ChatLogViewModel(private val roomId: String?, private val privateParticipant: Participant?) : ViewModel() {

    private val _messages = Repository.messages
    val messages: LiveData<ArrayList<Message>>
        get() = _messages

    private val _newMessageStatus = Repository.newMessageStatus
    val newMessageStatus: LiveData<FirestoreQueryStatus>
        get() = _newMessageStatus

    fun getMessages() {
        Repository.getMessages(roomId!!)
    }

    fun setUpNewRoom() {
        Repository.setUpNewRoom(privateParticipant!!)
    }

    fun addNewMessage(text: String, roomId: String) {
        Repository.addNewMessage(text, roomId)
    }

}