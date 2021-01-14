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
import io.github.fabiantauriello.hiya.domain.StoriesResponse
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.util.Utils
import kotlinx.android.synthetic.main.fragment_story_log.view.*

class StoriesViewModel : ViewModel() {

    private val TAG = this::class.java.name

    private val db = Firebase.firestore

    private val _inProgressStoryList = MutableLiveData<StoriesResponse>()
    val inProgressStoryList: LiveData<StoriesResponse>
        get() = _inProgressStoryList

    private val _finishedStoryList = MutableLiveData<StoriesResponse>()
    val finishedStoryList: LiveData<StoriesResponse>
        get() = _finishedStoryList

    private val _likedStoryList = MutableLiveData<StoriesResponse>()
    val likedStoryList: LiveData<StoriesResponse>
        get() = _likedStoryList

    // for in-progress stories fragment
    fun listenForInProgressStories() {
        Log.d(TAG, "listenForInProgressStories: called")

        _inProgressStoryList.value = StoriesResponse.loading()

        val storiesRef = db.collection(Hiya.STORIES_COLLECTION_PATH)
            .whereArrayContains("authorIds", Hiya.userId).whereEqualTo("finished", false)
            .orderBy("lastUpdateTimestamp", Query.Direction.DESCENDING)

        storiesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            val inProgressTempList = arrayListOf<Story>()
            for (doc in snapshot?.documents!!) {
                val story = doc.toObject(Story::class.java)!!
                inProgressTempList.add(story)
            }
            _inProgressStoryList.value = StoriesResponse.success(inProgressTempList)
        }
    }

    // for finished stories fragment and liked stories fragment
    fun listenForFinishedStories() {
        Log.d(TAG, "listenForFinishedStories: called")

        val storiesRef = db.collection(Hiya.STORIES_COLLECTION_PATH)
            .whereArrayContains("authorIds", Hiya.userId).whereEqualTo("finished", true)
            .orderBy("lastUpdateTimestamp", Query.Direction.DESCENDING)

        storiesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            val finishedTempList = arrayListOf<Story>()
            val likedTempList = arrayListOf<Story>()
            for (doc in snapshot?.documents!!) {
                val story = doc.toObject(Story::class.java)!!
                val storyIsLiked = Utils.getAuthorFromStory(story, Hiya.userId).liked
                if (storyIsLiked) {
                    likedTempList.add(story)
                }
                finishedTempList.add(story)
            }
            _finishedStoryList.value = StoriesResponse.success(finishedTempList)
            _likedStoryList.value = StoriesResponse.success(likedTempList)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: clearing")
    }

}