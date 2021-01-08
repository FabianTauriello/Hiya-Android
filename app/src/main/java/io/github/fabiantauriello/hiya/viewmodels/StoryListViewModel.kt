package io.github.fabiantauriello.hiya.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.domain.Author
import io.github.fabiantauriello.hiya.domain.FirestoreResponse
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.domain.User
import io.github.fabiantauriello.hiya.util.Utils
import kotlinx.android.synthetic.main.fragment_story_log.view.*
import kotlinx.coroutines.tasks.await

class StoryListViewModel : ViewModel() {

    private val TAG = this::class.java.name

    private val db = Firebase.firestore

    private val _userStoryPairResponse = MutableLiveData<ArrayList<Pair<Story, Author>>>()
    val userStoryPairResponse: LiveData<ArrayList<Pair<Story, Author>>>
        get() = _userStoryPairResponse

    // listens to multiple documents
    fun listenForStories() {
        Log.d(TAG, "listenForStories: called")

        val userStoriesRef = db.collection(Hiya.STORIES_COLLECTION_PATH)
            .whereArrayContains("authorIds", Hiya.userId)
            .orderBy("lastUpdateTimestamp", Query.Direction.DESCENDING)

        userStoriesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            val resultList = arrayListOf<Pair<Story, Author>>()
            for (doc in snapshot?.documents!!) {
                val story = doc.toObject(Story::class.java)!!
                val author = Utils.getCoAuthorFromStory(story)
                resultList.add(Pair(story, author))
            }
            _userStoryPairResponse.value = resultList
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: clearing")
    }

}