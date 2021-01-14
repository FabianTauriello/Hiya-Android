package io.github.fabiantauriello.hiya.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.domain.StoriesResponse
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.util.Utils

class StoryListsRepository {

    private val TAG = this::class.java.name

    private val db = Firebase.firestore

    val inProgressStoryList = MutableLiveData<StoriesResponse>()
    val finishedStoryList = MutableLiveData<StoriesResponse>()
    val likedStoryList = MutableLiveData<StoriesResponse>()

    // for in-progress stories fragment
    fun listenForInProgressStories() {
        Log.d(TAG, "listenForInProgressStories: called")

        inProgressStoryList.value = StoriesResponse.loading()

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
            inProgressStoryList.value = StoriesResponse.success(inProgressTempList)
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
            finishedStoryList.value = StoriesResponse.success(finishedTempList)
            likedStoryList.value = StoriesResponse.success(likedTempList)
        }
    }
}