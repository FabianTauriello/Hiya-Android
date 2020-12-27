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
import io.github.fabiantauriello.hiya.domain.FirestoreQueryStatus
import io.github.fabiantauriello.hiya.domain.Author
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.domain.User

class InProgressSharedViewModel() : ViewModel() {

    private val TAG = this::class.java.name

    private val _addNewWordStatus = MutableLiveData<FirestoreQueryStatus>(FirestoreQueryStatus.PENDING)
    val addNewWordStatus: LiveData<FirestoreQueryStatus>
        get() = _addNewWordStatus

    private val _createNewStoryStatus = MutableLiveData<FirestoreQueryStatus>(FirestoreQueryStatus.PENDING)
    val createNewStoryStatus: LiveData<FirestoreQueryStatus>
        get() = _createNewStoryStatus

    private val _contacts = MutableLiveData<ArrayList<User>>()
    val contacts: LiveData<ArrayList<User>>
        get() = _contacts

    private val _storyList = MutableLiveData<ArrayList<Story>>()
    val storyList: LiveData<ArrayList<Story>>
        get() = _storyList

    private val _storyText = MutableLiveData<String>()
    val storyText: LiveData<String>
        get() = _storyText

    private val _storyTitle = MutableLiveData<String>()
    val storyTitle: LiveData<String>
        get() = _storyTitle

    private val _wordCount = MutableLiveData<Int>(0)
    val wordCount: LiveData<Int>
        get() = _wordCount

    lateinit var storyId: String

    // STORY LIST LOGIC

    fun getUsersMatchingContactsOnDevice() {
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
            }
            0 -> {
                // Insert code here to notify the user that no contacts were found. This isn't
                // necessarily an error.
            }
            else -> {
                // Insert code here to do something with the results

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
                            _contacts.value = matchingContacts
                        }
                    }
                    .addOnFailureListener {

                    }
            }
        }
    }

    fun configureStoriesListener() {
        /*
        * You can listen to a document with the onSnapshot() method. An initial call using the callback you
        * provide creates a document snapshot immediately with the current contents of the single document.
        * Then, each time the contents change, another call updates the document snapshot.
        */
        Firebase.firestore.collection("stories")
            .whereArrayContains("authors", Author(Hiya.userId, Hiya.username))
            .orderBy("lastUpdateTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                val newStories = arrayListOf<Story>()
                for (doc in snapshot?.documents!!) {
                    val story = doc.toObject(Story::class.java)!!
                    newStories.add(story)
                }
                _storyList.value = newStories
            }
    }

    // STORY LOG LOGIC

    fun configureMessagesListener() {
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
        val newParticipants = arrayListOf(
            Author(Hiya.userId, Hiya.username, Hiya.profileImageUri),
            Author(coAuthor.userId, coAuthor.name, coAuthor.profileImageUri)
        )
        val newStory = Story(id = newStoryId, title = newTitle, authors = newParticipants)

        storyRef.set(newStory)
            .addOnSuccessListener {
                storyId = newStoryId
                _createNewStoryStatus.value = FirestoreQueryStatus.SUCCESS
                _storyTitle.value = newTitle
                configureMessagesListener()
            }
            .addOnFailureListener {
                _createNewStoryStatus.value = FirestoreQueryStatus.FAILURE
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
                _addNewWordStatus.value = FirestoreQueryStatus.SUCCESS
                _wordCount.value = _wordCount.value?.plus(1)
            }
            .addOnFailureListener {
                _addNewWordStatus.value = FirestoreQueryStatus.FAILURE
            }
    }

    fun clearPropertiesForStoryLog() {
        storyId = ""
        _storyText.value = ""
        _storyTitle.value = ""
        _wordCount.value = 0
        _addNewWordStatus.value = FirestoreQueryStatus.PENDING
        _createNewStoryStatus.value = FirestoreQueryStatus.PENDING
    }

    fun updateTitle(newTitle: String) {
        _storyTitle.value = newTitle
    }


}