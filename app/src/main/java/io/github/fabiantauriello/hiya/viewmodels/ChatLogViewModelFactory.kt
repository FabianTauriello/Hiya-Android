package io.github.fabiantauriello.hiya.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.fabiantauriello.hiya.domain.Participant

class ChatLogViewModelFactory(private val roomId: String?, private val privateParticipant: Participant?) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatLogViewModel(roomId, privateParticipant) as T
    }
}