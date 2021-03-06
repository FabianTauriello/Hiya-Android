package io.github.fabiantauriello.hiya.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.domain.*
import io.github.fabiantauriello.hiya.util.Utils

class StoryLogRepository {

    private val TAG = this::class.java.name

    private val db = Firebase.firestore

    val addNewTextStatus = MutableLiveData<FirestoreResponseWithoutData>()

    val createNewStoryStatus = MutableLiveData<FirestoreResponseWithoutData>()

    val story = MutableLiveData<FirestoreResponse<Story>>()

    private lateinit var storyId: String

    // listen to one story document for changes. called once at beginning and then again with each change
    fun listenForChangesToStory() {
        Log.d(TAG, "listenForChangesToStory: triggered")
        val storyRef = db.collection(Hiya.STORIES_COLLECTION_PATH).document(storyId)
        storyRef.addSnapshotListener { snapshot, e ->
            Log.d(TAG, "listenForChangesToStory: called")
            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                story.value = FirestoreResponse.success(snapshot.toObject(Story::class.java)!!)
            } else {
                Log.d(TAG, "Current data: null")
            }
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
            title = "",
            text = "",
            lastUpdateTimestamp = newTimestamp,
            finished = false,
            tags = arrayListOf(),
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
                createNewStoryStatus.value = FirestoreResponseWithoutData.success()
                listenForChangesToStory()
            }
            .addOnFailureListener { e ->
                createNewStoryStatus.value = FirestoreResponseWithoutData.error(e.message.toString())
            }
    }

    fun updateStoryId(newStoryId: String) {
        storyId = newStoryId
    }

    fun updateText(newText: String) {
        val storyRef = Firebase.firestore.collection(Hiya.STORIES_COLLECTION_PATH).document(storyId)
        db.runBatch { batch ->
            val timestamp = System.currentTimeMillis().toString()
            val newWordCount = newText.split("\\s+".toRegex()).toTypedArray().size
            val coAuthorId = Utils.getCoAuthorFromStory(story.value?.data!!).id

            // update fields in db
            batch.update(storyRef, "lastUpdateTimestamp", timestamp)
            batch.update(storyRef, "wordCount", newWordCount)
            batch.update(storyRef, "nextTurn", coAuthorId)
            batch.update(storyRef, "text", newText)
        }
            .addOnSuccessListener {
                addNewTextStatus.value = FirestoreResponseWithoutData.success()
            }
            .addOnFailureListener { e ->
                addNewTextStatus.value = FirestoreResponseWithoutData.error(e.message.toString())
            }
    }

    fun addAuthorToDoneList() {
        val storyRef = db.collection(Hiya.STORIES_COLLECTION_PATH).document(storyId)
        db.runTransaction { transaction ->
            // mark the whole story as finished if coAuthor has already marked the story as done
            val story = transaction.get(storyRef).toObject(Story::class.java)!!
            val coAuthor = Utils.getCoAuthorFromStory(story)
            if (coAuthor.done) {
                transaction.update(storyRef, "finished", true)
            }
            // mark the story as done for this author
            transaction.update(storyRef, "authors", FieldValue.arrayRemove(Author(Hiya.userId, Hiya.name, Hiya.profilePic, liked = false, done = false)))
            transaction.update(storyRef, "authors", FieldValue.arrayUnion(Author(Hiya.userId, Hiya.name, Hiya.profilePic, liked = false, done = true)))
        }
    }

    fun removeAuthorFromDoneList() {
        val storyRef = db.collection(Hiya.STORIES_COLLECTION_PATH).document(storyId)
        db.runBatch { batch ->
            batch.update(storyRef, "authors", FieldValue.arrayRemove(Author(Hiya.userId, Hiya.name, Hiya.profilePic, liked = false, done = true)))
            batch.update(storyRef, "authors", FieldValue.arrayUnion(Author(Hiya.userId, Hiya.name, Hiya.profilePic, liked = false, done = false)))
        }
    }

    fun updateTags(newTags: ArrayList<String>) {
        val storyRef = db.collection(Hiya.STORIES_COLLECTION_PATH).document(storyId)
        storyRef.update("tags", newTags)
    }

    fun updateTitle(newTitle: String) {
        val storyRef = db.collection(Hiya.STORIES_COLLECTION_PATH).document(storyId)
        storyRef.update("title", newTitle)
    }

}