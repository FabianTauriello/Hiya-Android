package io.github.fabiantauriello.hiya.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.domain.*

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

    private val _storyLogText = MutableLiveData("")
    val storyLogText: LiveData<String>
        get() = _storyLogText

//    private var _isDone = MutableLiveData(false)
//    val isDone: LiveData<Boolean>
//        get() = _isDone


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
                    // update text LiveData
                    _storyLogText.value = snapshot.getString("text")!!
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
    }

    fun addAuthorToDoneList() {
        db.collection(Hiya.STORIES_COLLECTION_PATH).document(storyId)
            .update("authorsDone", FieldValue.arrayUnion(Hiya.userId))
    }

    fun removeAuthorFromDoneList() {
        db.collection(Hiya.STORIES_COLLECTION_PATH).document(storyId)
            .update("authorsDone", FieldValue.arrayRemove(Hiya.userId))
    }

    fun createNewStory(coAuthorId: String) {
        // Initialize Firebase refs
        val storyRef = db.collection(Hiya.STORIES_COLLECTION_PATH).document()

        // Build new story
        val newStoryId = storyRef.id
        val newTimestamp = System.currentTimeMillis().toString()
        val newStory = Story(newStoryId, "", "", newTimestamp, false, 0, Hiya.userId, arrayListOf(Hiya.userId, coAuthorId))

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
            val storyRef = Firebase.firestore.collection(Hiya.STORIES_COLLECTION_PATH).document(storyId) // TODO change "stories" path string to use constant
            val snapshot = transaction.get(storyRef)

            val timestamp = System.currentTimeMillis().toString()
            val coAuthorId = (snapshot.get("authors") as ArrayList<String>).filter { it != Hiya.userId }[0]
            val newWordCount = snapshot.getDouble("wordCount")!! + 1
            var newText = snapshot.getString("text")!!
            if (newText.isNotEmpty()) {
                newText += " "
            }
            newText += newWord

            // update fields in db
            transaction.update(storyRef, "lastUpdateTimestamp", timestamp)
            transaction.update(storyRef, "wordCount", newWordCount)
            transaction.update(storyRef, "text", newText)
            transaction.update(storyRef, "nextTurn", coAuthorId)

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