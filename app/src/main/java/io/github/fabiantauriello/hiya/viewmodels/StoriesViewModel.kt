package io.github.fabiantauriello.hiya.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.domain.StoriesResponse
import io.github.fabiantauriello.hiya.repositories.StoryListsRepository

class StoriesViewModel : ViewModel() {

    private val repo = StoryListsRepository()

    private val db = Firebase.firestore

    private val _inProgressStoryList = repo.inProgressStoryList
    val inProgressStoryList: LiveData<StoriesResponse>
        get() = _inProgressStoryList

    private val _finishedStoryList = repo.finishedStoryList
    val finishedStoryList: LiveData<StoriesResponse>
        get() = _finishedStoryList

    private val _likedStoryList = repo.likedStoryList
    val likedStoryList: LiveData<StoriesResponse>
        get() = _likedStoryList

    fun listenForInProgressStories() {
        repo.listenForInProgressStories()
    }

    fun listenForFinishedStories() {
        repo.listenForFinishedStories()
    }

}