package io.github.fabiantauriello.hiya.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.domain.Author
import io.github.fabiantauriello.hiya.domain.FirestoreResponse
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.repositories.StoryListsRepository

class StoriesViewModel : ViewModel() {

    private val repo = StoryListsRepository()

    private val db = Firebase.firestore

    private val _inProgressStoryList = repo.inProgressStoryList
    val inProgressStoryList: LiveData<FirestoreResponse<ArrayList<Story>>>
        get() = _inProgressStoryList

    private val _finishedStoryList = repo.finishedStoryList
    val finishedStoryList: LiveData<FirestoreResponse<ArrayList<Story>>>
        get() = _finishedStoryList

    private val _likedStoryList = repo.likedStoryList
    val likedStoryList: LiveData<FirestoreResponse<ArrayList<Story>>>
        get() = _likedStoryList

    fun listenForInProgressStories() {
        repo.listenForInProgressStories()
    }

    fun listenForFinishedStories() {
        repo.listenForFinishedStories()
    }

    fun updateLikeStatus(storyId: String, author: Author, liked: Boolean) {
        repo.updateLikeStatus(storyId, author, liked)
    }

}