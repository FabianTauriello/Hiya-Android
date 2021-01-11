package io.github.fabiantauriello.hiya.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.domain.Author
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.util.Utils
import kotlinx.android.synthetic.main.fragment_story_log.view.*

class StoriesViewModel : ViewModel() {

    private val TAG = this::class.java.name

    private val db = Firebase.firestore

    private val _inProgressStoryList = MutableLiveData<ArrayList<Story>>()
    val inProgressStoryList: LiveData<ArrayList<Story>>
        get() = _inProgressStoryList

    private val _finishedStoryList = MutableLiveData<ArrayList<Story>>()
    val finishedStoryList: LiveData<ArrayList<Story>>
        get() = _finishedStoryList

    private val _likedStoryList = MutableLiveData<ArrayList<Story>>()
    val likedStoryList: LiveData<ArrayList<Story>>
        get() = _likedStoryList

    lateinit var registration: ListenerRegistration

    // listens to multiple documents.
    // TODO check how many times this is called on updates (e.g. updating 'done' field). alternate way of doing this is separating functions
    // for each list (in-progress, finished, liked) - e.g. .whereArrayContains("authorIds", Hiya.userId) AND .whereContains("done", true)
    fun listenForStories() {
        Log.d(TAG, "listenForStories: called")

        val userStoriesRef = db.collection(Hiya.STORIES_COLLECTION_PATH)
            .whereArrayContains("authorIds", Hiya.userId)
            .orderBy("lastUpdateTimestamp", Query.Direction.DESCENDING)

        registration = userStoriesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            val inProgressTempList = arrayListOf<Story>()
            val finishedTempList = arrayListOf<Story>()
            val likedTempList = arrayListOf<Story>()

            for (doc in snapshot?.documents!!) {
                val story = doc.toObject(Story::class.java)!!

                // check if story is now finished (if all authors have marked story as done)
                val storyIsFinished = story.authors.all { it.done }
                val storyIsLiked = Utils.getAuthorFromStory(story, Hiya.userId).liked

                // Update temp lists for livedata
                if (storyIsFinished) {
                    finishedTempList.add(story)
                    if (storyIsLiked) {
                        likedTempList.add(story)
                    }
                } else {
                    inProgressTempList.add(story)
                }
            }
            _inProgressStoryList.value = inProgressTempList
            _finishedStoryList.value = finishedTempList
            _likedStoryList.value = likedTempList
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: clearing")
    }

}