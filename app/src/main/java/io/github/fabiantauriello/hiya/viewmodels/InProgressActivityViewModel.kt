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
import io.github.fabiantauriello.hiya.domain.FirestoreResponse
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.domain.User

class InProgressActivityViewModel : ViewModel() {

    private val TAG = this::class.java.name

    private val _storyListResponse = MutableLiveData<FirestoreResponse<ArrayList<Story>>>()
    val storyListResponse: LiveData<FirestoreResponse<ArrayList<Story>>>
        get() = _storyListResponse

    private val _userListResponse = MutableLiveData<FirestoreResponse<ArrayList<User>>>()
    val userListResponse: LiveData<FirestoreResponse<ArrayList<User>>>
        get() = _userListResponse

    private val db = Firebase.firestore

    init {
        Log.d(TAG, "init")
    }

    // listens to multiple documents
    fun listenForStories() {
        Log.d(TAG, "listenForStories: called")
        _storyListResponse.value = FirestoreResponse.loading()

        val userStoriesRef = db.collection("stories")
            .whereArrayContains("authorIds", Hiya.userId)
            .orderBy("lastUpdateTimestamp", Query.Direction.DESCENDING)

        userStoriesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                _storyListResponse.value =
                    FirestoreResponse.error(error.message ?: "Failed to retrieve stories.")
                return@addSnapshotListener
            }

            val newStories = arrayListOf<Story>()
            for (doc in snapshot?.documents!!) {
                val story = doc.toObject(Story::class.java)!!
                newStories.add(story)
            }
            _storyListResponse.value = FirestoreResponse.success(newStories)
        }
    }

    fun getUsersWhoAreContacts() { // TODO ugly - make it prettier
        Log.d(TAG, "getUsersWhoAreContacts: called")

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
                db.collection("users")
                    .get() // TODO do where contains here because there could be many users
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

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: clearing")
    }

}