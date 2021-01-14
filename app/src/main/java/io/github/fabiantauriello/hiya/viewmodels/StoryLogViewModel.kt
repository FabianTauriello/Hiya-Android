package io.github.fabiantauriello.hiya.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.domain.*
import io.github.fabiantauriello.hiya.repositories.StoryLogRepository
import io.github.fabiantauriello.hiya.util.Utils

class StoryLogViewModel : ViewModel() {

    private val TAG = this::class.java.name

    private val repo = StoryLogRepository()

    private val _addNewWordStatus = repo.addNewWordStatus
    val addNewWordStatus: LiveData<FirestoreResponseWithoutData>
        get() = _addNewWordStatus

    private val _createNewStoryStatus = repo.createNewStoryStatus
    val createNewStoryStatus: LiveData<FirestoreResponseWithoutData>
        get() = _createNewStoryStatus

    private val _story = repo.story
    val story: LiveData<FirestoreResponse<Story>>
        get() = _story

    fun listenForChangesToStory() {
        repo.listenForChangesToStory()
    }

    fun addAuthorToDoneList() {
        repo.addAuthorToDoneList()
    }

    fun removeAuthorFromDoneList() {
        repo.removeAuthorFromDoneList()
    }

    fun createNewStory(coAuthor: User) {
        repo.createNewStory(coAuthor)
    }

    fun addNewWord(newWord: String) {
        repo.addNewWord(newWord)
    }

    fun updateStory(updates: Map<String, Any>) {
        repo.updateStory(updates)
    }

    fun updateStoryId(newStoryId: String) {
        repo.updateStoryId(newStoryId)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: clearing")
    }

}