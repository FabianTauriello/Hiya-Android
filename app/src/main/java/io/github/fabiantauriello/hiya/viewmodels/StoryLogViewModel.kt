package io.github.fabiantauriello.hiya.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.fabiantauriello.hiya.domain.*
import io.github.fabiantauriello.hiya.repositories.StoryLogRepository

class StoryLogViewModel : ViewModel() {

    private val TAG = this::class.java.name

    private val repo = StoryLogRepository()

    val addNewTextStatus = repo.addNewTextStatus

    val createNewStoryStatus = repo.createNewStoryStatus

    val story = repo.story

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

    fun addToStoryText(startIndex: Int, endIndex: Int) { // todo I just realized that this won't work either because the new text entered could be exactly the same as the previous text
        Log.d(TAG, "start: $startIndex")
        Log.d(TAG, "end: $endIndex")
        val text = story.value?.data?.text?.subSequence(startIndex, endIndex).toString()
        repo.addToStoryText(text)
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