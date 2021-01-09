package io.github.fabiantauriello.hiya.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.domain.Author
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.util.Utils
import kotlinx.android.synthetic.main.fragment_story_log.view.*

class StoryListViewModel : ViewModel() {

    private val TAG = this::class.java.name

    private val db = Firebase.firestore

    private val _inProgressStoryList = MutableLiveData<ArrayList<Pair<Story, Author>>>()
    val inProgressStoryList: LiveData<ArrayList<Pair<Story, Author>>>
        get() = _inProgressStoryList

    private val _finishedStoryList = MutableLiveData<ArrayList<Pair<Story, Author>>>()
    val finishedStoryList: LiveData<ArrayList<Pair<Story, Author>>>
        get() = _finishedStoryList

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

            val inProgressTempList = arrayListOf<Pair<Story, Author>>()
            val completedTempList = arrayListOf<Pair<Story, Author>>()

            for (doc in snapshot?.documents!!) {
                val story = doc.toObject(Story::class.java)!!
                val author = Utils.getCoAuthorFromStory(story)

                // check if story is now finished (if all authors have marked story as done)
                val storyIsFinished = story.authors.all { it.done }

                // Update temp lists for livedata
                if (storyIsFinished) {
                    completedTempList.add(Pair(story, author))
                } else {
                    inProgressTempList.add(Pair(story, author))
                }
            }
            _inProgressStoryList.value = inProgressTempList
            _finishedStoryList.value = completedTempList
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: clearing")
    }

}