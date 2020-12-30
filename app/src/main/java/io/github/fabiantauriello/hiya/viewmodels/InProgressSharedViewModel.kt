package io.github.fabiantauriello.hiya.viewmodels

import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.domain.*

class InProgressSharedViewModel() : ViewModel() {

    private val TAG = this::class.java.name

    // STORY LIST AND USER SELECTION LIVE DATA

    private val _storyListResponse = MutableLiveData<FirestoreResponse<ArrayList<Story>>>()
    val storyListResponse: LiveData<FirestoreResponse<ArrayList<Story>>>
        get() = _storyListResponse

    private val _userListResponse = MutableLiveData<FirestoreResponse<ArrayList<User>>>()
    val userListResponse: LiveData<FirestoreResponse<ArrayList<User>>>
        get() = _userListResponse

    // STORY LOG LIVE DATA

    private val _addNewWordStatus = MutableLiveData<QueryStatus>(QueryStatus.PENDING)
    val addNewWordStatus: LiveData<QueryStatus>
        get() = _addNewWordStatus

    private val _storyText = MutableLiveData<String>("")
    val storyText: LiveData<String>
        get() = _storyText

    private val _storyTitle = MutableLiveData<String>("")
    val storyTitle: LiveData<String>
        get() = _storyTitle

    private lateinit var storyId: String

    // STORY LIST AND USER SELECTION LOGIC

    fun getUsersWhoAreContacts() { // TODO ugly - make it prettier

        // The content URI of the phone table
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        // A "projection" defines the columns that will be returned for each row
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        // get content resolver to interact with content provider
        val contentResolver = Hiya.applicationContext().contentResolver
        // queries the contacts and returns results
        val cursor = contentResolver.query(uri, projection, null, null, null)

        when (cursor?.count) {
            null -> {
                // Insert code here to handle the error. Be sure not to use the cursor!
                // You may want to call android.util.Log.e() to log this error.
                // TODO
            }
            0 -> {
                // Insert code here to notify the user that no contacts were found. This isn't
                // necessarily an error.
                // TODO
            }
            else -> {
                // Use the results

                val deviceContactNameList = arrayListOf<String>()
                val deviceContactPhoneNumberList = arrayListOf<String>()

                // get contact names and phone numbers from device
                while (cursor.moveToNext()) {
                    val name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val number =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                    deviceContactNameList.add(name)
                    deviceContactPhoneNumberList.add(number)
                }

                // get all user documents and check if its phone number matches a phone number on the device
                // If so, then add it to the contacts list variable
                _userListResponse.postValue(FirestoreResponse.loading())
                Firebase.firestore.collection("users").get()
                    .addOnSuccessListener { snapshot ->
                        if (!snapshot.isEmpty) {
                            val matchingContacts: ArrayList<User> = arrayListOf()
                            for (user in snapshot.documents) {
                                val userPhoneNumber = user.get("phoneNumber") as String
                                val profileImageUri = user.get("profileImageUri") as String
                                val index = deviceContactPhoneNumberList.indexOf(userPhoneNumber)
                                if (index != -1) {
                                    // contact has Hiya
                                    matchingContacts.add(
                                        User(
                                            user.id,
                                            deviceContactNameList[index],
                                            userPhoneNumber,
                                            profileImageUri
                                        )
                                    )
                                }
                            }
                            _userListResponse.value = FirestoreResponse.success(matchingContacts)
                        }
                    }
                    .addOnFailureListener { exception ->
                        _userListResponse.postValue(
                            FirestoreResponse.error(
                                exception.message ?: "Failed to retrieve users"
                            )
                        )
                    }
            }
        }

    }

    // listens to multiple documents
    fun startListeningForStories() {
        _storyListResponse.value = FirestoreResponse.loading()

        Firebase.firestore.collection("stories")
            .whereArrayContains("authors", Author(Hiya.userId, Hiya.username))
            .orderBy("lastUpdateTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    _storyListResponse.value =
                        FirestoreResponse.error(exception.message ?: "Failed to retrieve stories.")
                    return@addSnapshotListener
                }

                val newStories = arrayListOf<Story>()
                for (doc in snapshot?.documents!!) {
                    val story = doc.toObject(Story::class.java)!!
                    newStories.add(story)
                }
                Log.d(TAG, "startListeningForStories: about to update")
                _storyListResponse.value = FirestoreResponse.success(newStories)
            }
    }

    // STORY LOG LOGIC

    // listen to one document
    fun startListeningForTextChangesToStory() {
        Log.d(TAG, "configureMessagesListener: called")
        Firebase.firestore.collection("stories").document(storyId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    _storyText.value = snapshot.getString("text")!!
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
    }

    fun createNewStory(coAuthor: Author, newTitle: String) {
        Log.d(TAG, "createNewStory: called")
        val storyRef = Firebase.firestore.collection("stories").document()
        val newStoryId = storyRef.id
        val timestamp = System.currentTimeMillis().toString()
        val newParticipants = arrayListOf(
            Author(Hiya.userId, Hiya.username, Hiya.profileImageUri),
            Author(coAuthor.userId, coAuthor.name, coAuthor.profileImageUri)
        )
        val newStory = Story(newStoryId, newTitle, "", timestamp, 0, newParticipants)

        storyRef.set(newStory)
            .addOnSuccessListener {
                storyId = newStoryId
                _storyTitle.value = newTitle
                startListeningForTextChangesToStory()
            }
            .addOnFailureListener {

            }
    }

    fun addNewWord(newWord: String) {
        Firebase.firestore.runTransaction { transaction ->
            val storyRef = Firebase.firestore.collection("stories")
                .document(storyId) // TODO change "stories" path string to use constant
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
                _addNewWordStatus.value = QueryStatus.SUCCESS
            }
            .addOnFailureListener {
                _addNewWordStatus.value = QueryStatus.ERROR
            }
    }

    fun updateTitle(newTitle: String) {
        _storyTitle.value = newTitle
    }

    fun updateStoryId(newStoryId: String) {
        storyId = newStoryId
    }

    // OTHER LOGIC

    fun clearPropertiesForStoryLog() {
        storyId = ""
        _storyText.value = ""
        _storyTitle.value = ""
        _addNewWordStatus.value = QueryStatus.PENDING
    }

}