package io.github.fabiantauriello.hiya.viewmodels

import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.domain.FirestoreResponse
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.domain.User

class StoryListViewModel : ViewModel() {

    private val TAG = this::class.java.name

    private val db = Firebase.firestore

    private val _storyListResponse = MutableLiveData<FirestoreResponse<ArrayList<Story>>>()
    val storyListResponse: LiveData<FirestoreResponse<ArrayList<Story>>>
        get() = _storyListResponse

    // listens to multiple documents
    fun listenForStories() {
        Log.d(TAG, "listenForStories: called")
        _storyListResponse.value = FirestoreResponse.loading()

        val userStoriesRef = db.collection("stories")
            .whereArrayContains("authors", Hiya.userId)
            .orderBy("lastUpdateTimestamp", Query.Direction.DESCENDING)

        userStoriesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                _storyListResponse.value =
                    FirestoreResponse.error(error.message ?: "Failed to retrieve stories.")
                return@addSnapshotListener
            }

            val newStories = arrayListOf<Story>()
            for (doc in snapshot?.documents!!) {
                val story = doc.toObject(Story::class.java)!!
                newStories.add(story)
            }
            _storyListResponse.value = FirestoreResponse.success(newStories)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: clearing")
    }

}