package io.github.fabiantauriello.hiya.viewmodels

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

    private val _userStoryPairResponse = MutableLiveData<FirestoreResponse<Pair<User, Story>>>()
    val userStoryPairResponse: LiveData<FirestoreResponse<Pair<User, Story>>>
        get() = _userStoryPairResponse

    // listens to multiple documents
    fun listenForStories() {
        Log.d(TAG, "listenForStories: called")
        _storyListResponse.value = FirestoreResponse.loading()

        val userStoriesRef = db.collection(Hiya.STORIES_COLLECTION_PATH)
            .whereArrayContains(Hiya.AUTHORS_COLLECTION_PATH, Hiya.userId)
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

    fun getUserInfo(userId: String, story: Story) {
        val userRef = db.collection(Hiya.USERS_COLLECTION_PATH).document(userId)
        userRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val data = Pair(document.toObject(User::class.java)!!, story)
                _userStoryPairResponse.value = FirestoreResponse.success(data)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: clearing")
    }

}