package io.github.fabiantauriello.hiya.repository

import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.domain.*

// singleton
object Repository {

    private val TAG = this::class.java.name

    // for ChatRoomsViewModel
    var contacts: MutableLiveData<ArrayList<User>> = MutableLiveData()
    var rooms: MutableLiveData<ArrayList<ChatRoom>> = MutableLiveData()

    // for ChatLogViewModel
    var messages: MutableLiveData<ArrayList<Message>> = MutableLiveData()

    var newMessageStatus: MutableLiveData<FirestoreQueryStatus> = MutableLiveData(FirestoreQueryStatus.LOADING)

    // TODO detach listeners

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
                            contacts.postValue(matchingContacts)
                        }
                    }
                    .addOnFailureListener {

                    }
            }
        }
    }

    fun getChatRooms() {

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
                    Log.w(TAG, "Listen failed.", e)
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
                rooms.postValue(newRooms)
            }
    }

    fun getMessages(roomId: String) {
        Firebase.firestore.collection("rooms").document(roomId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                val documentChanges = snapshot?.documentChanges
                val newMessages = arrayListOf<Message>()
                if (documentChanges != null) {
                    for (message in documentChanges) {
                        val text = message.document.get("text").toString()
                        val timestamp = message.document.get("timestamp").toString()
                        val sender = message.document.get("sender").toString()
                        // update chat log adapter with the latest message
                        newMessages.add(Message(text, timestamp, sender))
                    }
                    messages.value = newMessages
                }

            }
    }

    fun setUpNewRoom(privateParticipant: Participant) {
        val roomsRef = Firebase.firestore.collection("rooms").document()
        val newRoomId = roomsRef.id
        val newParticipants = arrayListOf(
            Participant(Hiya.userId, Hiya.username),
            Participant(privateParticipant.userId, privateParticipant.name)
        )
        roomsRef.set(ChatRoom(newRoomId, newParticipants, null, null))
            .addOnSuccessListener {
                // roomId = newRoomId
                // initializeChatLogListener() // getMessages
                getMessages(newRoomId)
            }
            .addOnFailureListener {

            }
    }

    fun addNewMessage(text: String, roomId: String) {
        newMessageStatus.value = FirestoreQueryStatus.LOADING

        val timestamp = System.currentTimeMillis().toString()

        // get references for batch write
        val roomRef = Firebase.firestore.collection("rooms").document(roomId)
        val newMessageRef =
            Firebase.firestore.collection("rooms").document(roomId).collection("messages")
                .document()

        // write new message to messages collection in room document and update room properties: lastMessage and lastMessageTimestamp
        Firebase.firestore.runBatch { batch ->
            batch.update(roomRef, mapOf("lastMessage" to text, "lastMessageTimestamp" to timestamp))
            batch.set(newMessageRef, Message(text, timestamp, Hiya.userId))
        }
            .addOnSuccessListener {
                newMessageStatus.value = FirestoreQueryStatus.SUCCESS
            }
            .addOnFailureListener {
                // TODO message failed
                newMessageStatus.value = FirestoreQueryStatus.FAILURE
            }

    }
}