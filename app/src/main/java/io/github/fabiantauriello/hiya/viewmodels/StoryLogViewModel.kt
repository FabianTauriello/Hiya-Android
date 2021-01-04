package io.github.fabiantauriello.hiya.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.domain.*

class StoryLogViewModel : ViewModel() {

    private val TAG = this::class.java.name

    private val _addNewWordStatus = MutableLiveData<FirestoreResponseWithoutData>()
    val addNewWordStatus: LiveData<FirestoreResponseWithoutData>
        get() = _addNewWordStatus

    private val _createNewStoryStatus = MutableLiveData<FirestoreResponseWithoutData>()
    val createNewStoryStatus: LiveData<FirestoreResponseWithoutData>
        get() = _createNewStoryStatus

    private val _storyLogText = MutableLiveData<String>("")
    val storyLogText: LiveData<String>
        get() = _storyLogText

    private val _storyLogTitle = MutableLiveData<String>("")
    val storyLogTitle: LiveData<String>
        get() = _storyLogTitle

    private val _storyLogAuthors = MutableLiveData<ArrayList<String>>(arrayListOf())
    val storyLogAuthors: LiveData<ArrayList<String>>
        get() = _storyLogAuthors

    private lateinit var storyId: String

    init {
        Log.d(TAG, "init")
    }

    // listen to one story document for changes. called once at beginning and then again with each change
    // THIS SHOULD ONLY BE CALLED ONCE! And everything else should just listen to the changes made (e.g storyText)
    fun listenForChangesToStory() {
        Firebase.firestore.collection("stories").document(storyId)
            .addSnapshotListener { snapshot, e ->
                Log.d(TAG, "listenForChangesToStory: called")
                if (e != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    // Update live data variables

                    // Update story title
                    _storyLogTitle.value = snapshot.getString("title")

                    // Update story text
                    _storyLogText.value = snapshot.getString("text")

                    // Update authors list
                    _storyLogAuthors.value = snapshot.get("authorIds") as ArrayList<String>
                    Log.d(TAG, "listenForChangesToStory: ${_storyLogAuthors.value}")
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
    }

    fun createNewStory(coAuthor: Author) {
        // Initialize Firebase refs
        val storyRef = Firebase.firestore.collection("stories").document()
        val foundingAuthorRef =
            Firebase.firestore.collection("stories").document(storyRef.id).collection("authors")
                .document(Hiya.userId)
        val coAuthorRef =
            Firebase.firestore.collection("stories").document(storyRef.id).collection("authors")
                .document(coAuthor.userId)

        // Build new story
        val newStoryId = storyRef.id
        val newTimestamp = System.currentTimeMillis().toString()
        val newStory = Story(newStoryId, "", "", newTimestamp, false, 0, arrayListOf(Hiya.userId, coAuthor.userId))

        // Execute changes
        Firebase.firestore.runBatch {
            // Add new story doc
            storyRef.set(newStory)
            // Add new founding Author doc
            foundingAuthorRef.set(Author(Hiya.userId, false))
            // Add new co-author doc
            coAuthorRef.set(Author(coAuthor.userId, false))
        }
            .addOnSuccessListener {
                _createNewStoryStatus.value = FirestoreResponseWithoutData.success()
                storyId = newStoryId
                listenForChangesToStory()
            }
            .addOnFailureListener { e ->
                _createNewStoryStatus.value = FirestoreResponseWithoutData.error(e.message.toString())
            }

    }

    fun addNewWord(newWord: String) {
        Firebase.firestore.runTransaction { transaction ->
            val storyRef = Firebase.firestore.collection("stories").document(storyId) // TODO change "stories" path string to use constant
            val snapshot = transaction.get(storyRef)

            val timestamp = System.currentTimeMillis().toString()
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

            // Success
            null
        }
            .addOnSuccessListener {
                _addNewWordStatus.value = FirestoreResponseWithoutData.success()
            }
            .addOnFailureListener { e ->
                _addNewWordStatus.value = FirestoreResponseWithoutData.error(e.message.toString())
            }
    }

    fun updateStoryId(newStoryId: String) {
        storyId = newStoryId
    }

    fun changeIsDoneFlag(isDone: Boolean) {
        val authorsRef =
            Firebase.firestore.collection("stories").document(storyId).collection("authors")
                .document(Hiya.userId)

        authorsRef.update("isDone", isDone)
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: clearing")
    }

}