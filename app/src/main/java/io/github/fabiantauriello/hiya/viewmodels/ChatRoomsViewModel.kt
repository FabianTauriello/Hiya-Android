package io.github.fabiantauriello.hiya.viewmodels

import android.provider.ContactsContract
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.google.firebase.database.core.Repo
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.domain.ChatRoom
import io.github.fabiantauriello.hiya.domain.Participant
import io.github.fabiantauriello.hiya.domain.User
import io.github.fabiantauriello.hiya.repository.Repository
import io.github.fabiantauriello.hiya.ui.main.ChatRoomsFragmentDirections


class ChatRoomsViewModel : ViewModel() {

    private val _contacts = MutableLiveData<ArrayList<User>>()
    val contacts: LiveData<ArrayList<User>>
        get() = _contacts

    private val _rooms = MutableLiveData<ArrayList<ChatRoom>>()
    val rooms: LiveData<ArrayList<ChatRoom>>
        get() = _rooms

    init {
        getUsersMatchingContactsOnDevice()
        getChatRooms()
    }

    private fun getUsersMatchingContactsOnDevice() {
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
                                val index = deviceContactPhoneNumberList.indexOf(userPhoneNumber)
                                if (index != -1) {
                                    // contact has Hiya
                                    matchingContacts.add(
                                        User(
                                            user.id,
                                            deviceContactNameList[index],
                                            userPhoneNumber
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

    private fun getChatRooms() {
        /*
        * You can listen to a document with the onSnapshot() method. An initial call using the callback you
        * provide creates a document snapshot immediately with the current contents of the single document.
        * Then, each time the contents change, another call updates the document snapshot.
        */
        Firebase.firestore.collection("rooms")
            .whereArrayContains("participants", Participant(Hiya.userId, Hiya.username))
            .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                val newRooms = arrayListOf<ChatRoom>()
                for (doc in snapshot?.documents!!) {
                    val id = doc.id
                    val lastMessage = doc.get("lastMessage").toString()
                    val lastMessageTimestamp = doc.get("lastMessageTimestamp").toString()
                    val participantsMap = doc.get("participants") as List<Map<String, String>>
                    val participantsList = arrayListOf<Participant>()
                    participantsMap.forEach { map ->
                        participantsList.add(Participant(map["userId"] ?: "", map["name"] ?: ""))
                    }
                    newRooms.add(ChatRoom(id, participantsList, lastMessage, lastMessageTimestamp))
                }
                _rooms.value = newRooms
            }
    }

    fun onRoomClick(room: ChatRoom, view: View) {
        val action =
            ChatRoomsFragmentDirections.actionChatRoomsFragmentToChatLogFragment(room.id)
        view.findNavController().navigate(action)
    }

}