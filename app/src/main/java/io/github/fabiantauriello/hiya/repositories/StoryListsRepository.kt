package io.github.fabiantauriello.hiya.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.domain.FirestoreResponse
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.util.Utils

class StoryListsRepository {

    private val TAG = this::class.java.name

    private val db = Firebase.firestore

    val inProgressStoryList = MutableLiveData<FirestoreResponse<ArrayList<Story>>>()
    val finishedStoryList = MutableLiveData<FirestoreResponse<ArrayList<Story>>>()
    val likedStoryList = MutableLiveData<FirestoreResponse<ArrayList<Story>>>()

    // for in-progress stories fragment
    fun listenForInProgressStories() {
        Log.d(TAG, "listenForInProgressStories: called")

        inProgressStoryList.value = FirestoreResponse.loading()

        val storiesRef = db.collection(Hiya.STORIES_COLLECTION_PATH)
            .whereArrayContains("authorIds", Hiya.userId).whereEqualTo("finished", false)
            .orderBy("lastUpdateTimestamp", Query.Direction.DESCENDING)

        storiesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            val inProTempList = arrayListOf<Story>()
            for (doc in snapshot?.documents!!) {
                val story = doc.toObject(Story::class.java)!!
                inProTempList.add(story)
            }
            inProgressStoryList.value = if(inProTempList.isEmpty()) FirestoreResponse.success(null) else FirestoreResponse.success(inProTempList)
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
            val finTempList = arrayListOf<Story>()
            val likedTempList = arrayListOf<Story>()
            for (doc in snapshot?.documents!!) {
                val story = doc.toObject(Story::class.java)!!
                val storyIsLiked = Utils.getAuthorFromStory(story, Hiya.userId).liked
                if (storyIsLiked) {
                    likedTempList.add(story)
                }
                finTempList.add(story)
            }
            finishedStoryList.value = if (finTempList.isEmpty()) FirestoreResponse.success(null) else FirestoreResponse.success(finTempList)
            likedStoryList.value = if (likedTempList.isEmpty()) FirestoreResponse.success(null) else FirestoreResponse.success(likedTempList)
        }
    }
}