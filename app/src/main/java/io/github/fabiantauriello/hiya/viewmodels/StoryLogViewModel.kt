package io.github.fabiantauriello.hiya.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.fabiantauriello.hiya.domain.*
import io.github.fabiantauriello.hiya.repositories.StoryLogRepository

class StoryLogViewModel : ViewModel() {

    private val TAG = this::class.java.name

    private val repo = StoryLogRepository()

    // don't update here anything directly. It's updated on the fly
    val story = repo.story

    fun createNewStory(coAuthor: User) {
        repo.createNewStory(coAuthor)
    }

    fun updateStoryId(newStoryId: String) {
        repo.updateStoryId(newStoryId)
    }

    fun listenForChangesToStory() {
        repo.listenForChangesToStory()
    }

    fun updateText(newText: String) {
        repo.updateText(newText)
    }

    fun addAuthorToDoneList() {
        repo.addAuthorToDoneList()
    }

    fun removeAuthorFromDoneList() {
        repo.removeAuthorFromDoneList()
    }

    fun updateTags(newTags: ArrayList<String>) {
        repo.updateTags(newTags)
    }

    fun updateTitle(newTitle: String) {
        repo.updateTitle(newTitle)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: clearing")
    }
}