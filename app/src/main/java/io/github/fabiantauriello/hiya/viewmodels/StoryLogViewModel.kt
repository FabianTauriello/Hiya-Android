package io.github.fabiantauriello.hiya.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.domain.*
import io.github.fabiantauriello.hiya.util.Utils

class StoryLogViewModel : ViewModel() {

    private val TAG = this::class.java.name

    private val db = Firebase.firestore

    private lateinit var storyId: String

    private val _addNewWordStatus = MutableLiveData<FirestoreResponseWithoutData>()
    val addNewWordStatus: LiveData<FirestoreResponseWithoutData>
        get() = _addNewWordStatus

    private val _createNewStoryStatus = MutableLiveData<FirestoreResponseWithoutData>()
    val createNewStoryStatus: LiveData<FirestoreResponseWithoutData>
        get() = _createNewStoryStatus

    private val _story = MutableLiveData<FirestoreResponse<Story>>()
    val story: LiveData<FirestoreResponse<Story>>
        get() = _story

    // listen to one story document for changes. called once at beginning and then again with each change
    // THIS SHOULD ONLY BE CALLED ONCE! And everything else should just listen to the changes made (e.g storyText)
    fun listenForChangesToStory() {
        db.collection(Hiya.STORIES_COLLECTION_PATH).document(storyId)
            .addSnapshotListener { snapshot, e ->
                Log.d(TAG, "listenForChangesToStory: called")
                if (e != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val story = snapshot.toObject(Story::class.java)!!
                    _story.value = FirestoreResponse.success(story)
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
    }

    fun addAuthorToDoneList() {
        db.runBatch {
            val ref = db.collection(Hiya.STORIES_COLLECTION_PATH).document(storyId)
            ref.update("authors", FieldValue.arrayRemove(Author(Hiya.userId, Hiya.name, Hiya.profilePic, liked = false, done = false)))
            ref.update("authors", FieldValue.arrayUnion(Author(Hiya.userId, Hiya.name, Hiya.profilePic, liked = false, done = true)))
        }
    }

    fun removeAuthorFromDoneList() {
        db.runBatch {
            val ref = db.collection(Hiya.STORIES_COLLECTION_PATH).document(storyId)
            ref.update("authors", FieldValue.arrayRemove(Author(Hiya.userId, Hiya.name, Hiya.profilePic, liked = false, done = true)))
            ref.update("authors", FieldValue.arrayUnion(Author(Hiya.userId, Hiya.name, Hiya.profilePic, liked = false, done = false)))
        }
    }

    fun createNewStory(coAuthor: User) {
        // Initialize Firebase refs
        val storyRef = db.collection(Hiya.STORIES_COLLECTION_PATH).document()

        // Build new story
        val newStoryId = storyRef.id
        val newTimestamp = System.currentTimeMillis().toString()
        val newStory = Story(
            id = newStoryId,
            title ="",
            text ="",
            lastUpdateTimestamp = newTimestamp,
            wordCount = 0,
            nextTurn = Hiya.userId,
            authorIds = arrayListOf(Hiya.userId, coAuthor.id),
            authors = arrayListOf(
                Author(id = Hiya.userId, name = Hiya.name, profilePic = Hiya.profilePic, liked = false, done = false),
                Author(id = coAuthor.id, name = coAuthor.name, profilePic = coAuthor.profilePic, liked = false, done = false)
            )
        )

        // Add new story doc
        storyRef.set(newStory)
            .addOnSuccessListener {
                updateStoryId(newStoryId)
                _createNewStoryStatus.value = FirestoreResponseWithoutData.success()
                listenForChangesToStory()
            }
            .addOnFailureListener { e ->
                _createNewStoryStatus.value = FirestoreResponseWithoutData.error(e.message.toString())
            }

    }

    fun addNewWord(newWord: String) {
        db.runTransaction { transaction ->
            val storyRef = Firebase.firestore.collection(Hiya.STORIES_COLLECTION_PATH).document(storyId)
            val snapshot = transaction.get(storyRef)

            val timestamp = System.currentTimeMillis().toString()
            val coAuthorId = Utils.getCoAuthorFromStory(snapshot.toObject(Story::class.java)!!).id
            val newWordCount = snapshot.getDouble("wordCount")!! + 1
            var newText = snapshot.getString("text")!!
            if (newText.isNotEmpty()) {
                newText += " "
            }
            newText += newWord

            // update fields in db
            transaction.update(storyRef, "lastUpdateTimestamp", timestamp)
            transaction.update(storyRef, "wordCount", newWordCount)
            transaction.update(storyRef, "nextTurn", coAuthorId)
            transaction.update(storyRef, "text", newText)

            // Success
            null
        }
            .addOnSuccessListener {
                // update newWordStatus LiveData
                _addNewWordStatus.value = FirestoreResponseWithoutData.success()
            }
            .addOnFailureListener { e ->
                _addNewWordStatus.value = FirestoreResponseWithoutData.error(e.message.toString())
            }
    }

    fun updateStoryId(newStoryId: String) {
        storyId = newStoryId
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: clearing")
    }

}