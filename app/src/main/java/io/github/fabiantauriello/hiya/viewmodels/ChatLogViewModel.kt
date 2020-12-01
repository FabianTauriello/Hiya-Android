package io.github.fabiantauriello.hiya.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.domain.ChatRoom
import io.github.fabiantauriello.hiya.domain.FirestoreQueryStatus
import io.github.fabiantauriello.hiya.domain.Message
import io.github.fabiantauriello.hiya.domain.Participant
import io.github.fabiantauriello.hiya.util.Utils

class ChatLogViewModel(private var roomId: String?, private val privateParticipant: Participant?) : ViewModel() {

    private val TAG = this::class.java.name

    private val _messages = MutableLiveData<ArrayList<Message>>()
    val messages: LiveData<ArrayList<Message>>
        get() = _messages

    private val _newMessageStatus = MutableLiveData<FirestoreQueryStatus>()
    val newMessageStatus: LiveData<FirestoreQueryStatus>
        get() = _newMessageStatus

    private val _createNewRoomStatus = MutableLiveData<FirestoreQueryStatus>(FirestoreQueryStatus.FAILURE)
    val createNewRoomStatus: LiveData<FirestoreQueryStatus>
        get() = _createNewRoomStatus

    init {
        if (roomId == null) {
            // new room required
            createNewRoom()
        } else {
            // get messages from existing room
            configureMessagesListener()
        }
    }

    private fun configureMessagesListener() {
        Firebase.firestore.collection("rooms").document(roomId!!).collection("messages")
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
                    _messages.value = newMessages
                }

            }
    }

    private fun createNewRoom() {

        val roomsRef = Firebase.firestore.collection("rooms").document()
        val newRoomId = roomsRef.id
        val newParticipants = arrayListOf(
            Participant(Hiya.userId, Hiya.username),
            Participant(privateParticipant!!.userId, privateParticipant.name)
        )
        // create room
        roomsRef.set(ChatRoom(newRoomId, newParticipants, null, null))
            .addOnSuccessListener {
                _createNewRoomStatus.value = FirestoreQueryStatus.SUCCESS
                roomId = newRoomId
                configureMessagesListener()
            }
            .addOnFailureListener {
                _createNewRoomStatus.value = FirestoreQueryStatus.FAILURE
            }
    }

    fun addNewMessage(text: String) {
        val timestamp = System.currentTimeMillis().toString()
        val formattedTimestamp = Utils.formatTimestamp(timestamp)

        // get references for batch write
        val roomRef = Firebase.firestore.collection("rooms").document(roomId!!)
        val newMessageRef =
            Firebase.firestore.collection("rooms").document(roomId!!).collection("messages")
                .document()

        // write new message to messages collection in room document and update room properties: lastMessage and lastMessageTimestamp
        Firebase.firestore.runBatch { batch ->
            batch.update(roomRef, mapOf("lastMessage" to text, "lastMessageTimestamp" to formattedTimestamp))
            batch.set(newMessageRef, Message(text, formattedTimestamp, Hiya.userId))
        }
            .addOnSuccessListener {
                // TODO message successful
                _newMessageStatus.value = FirestoreQueryStatus.SUCCESS
            }
            .addOnFailureListener {
                // TODO message failed
            }
    }


}