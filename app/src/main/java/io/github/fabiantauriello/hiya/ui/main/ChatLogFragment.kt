package io.github.fabiantauriello.hiya.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.databinding.FragmentChatLogBinding
import io.github.fabiantauriello.hiya.domain.Message
import io.github.fabiantauriello.hiya.domain.PrivateChatRoom

// individual chat between users
class ChatLogFragment : Fragment() {

    private val TAG = this::class.java.name

    private val args: ChatLogFragmentArgs by navArgs()

    private var roomId: String? = null

    private var _binding: FragmentChatLogBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentChatLogBinding.inflate(inflater, container, false)

        roomId = args.roomId

        configureChatLogRecyclerView()
        checkIfNewRoomIsRequired()
        configureNewMessageButtonListener()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun configureChatLogRecyclerView() {
        // initialize recyclerview with empty adapter
        val adapter = ChatLogAdapter(arrayListOf())
        binding.rvChatLog.adapter = adapter
    }

    private fun checkIfNewRoomIsRequired() { // TODO MAY need to be converted to transaction or batch
        if (roomId != null) {
            // existing room in firebase - load existing messages
            binding.btnSendNewMessage.isEnabled = true
            initializeChatLogListener()
        } else {
            // new room will need to be created - TODO PRIVATE ONLY SO FAR
            val newRoom =
                PrivateChatRoom(arrayListOf(Hiya.userId, args.privateRoomReceiver!!), null, null)

            Firebase.firestore.collection("rooms").add(newRoom)
                .addOnSuccessListener {
                    roomId = it.id
                    binding.btnSendNewMessage.isEnabled = true

                    initializeChatLogListener()
                }
                .addOnFailureListener {

                }
        }
    }

    // adds all messages for a chat room
    private fun initializeChatLogListener() { // TODO MAY need to be converted to transaction or batch
        Firebase.firestore.collection("rooms").document(roomId!!).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                val newMessages = snapshot?.documentChanges
                if (newMessages != null) {
                    for (message in newMessages) {
                        val text = message.document.get("text").toString()
                        val timestamp = message.document.get("timestamp").toString()
                        val sender = message.document.get("sender").toString()
                        // update chat log adapter with the latest message
                        (binding.rvChatLog.adapter as ChatLogAdapter).addNewMessage(Message(text, timestamp, sender))
                    }
                }

            }
    }

    private fun configureNewMessageButtonListener() {
        binding.btnSendNewMessage.setOnClickListener {
            val text = binding.etNewMessage.text.toString()
            val timestamp = System.currentTimeMillis().toString()

            // get references for batch write
            val roomRef = Firebase.firestore.collection("rooms").document(roomId!!)
            val newMessageRef =
                Firebase.firestore.collection("rooms").document(roomId!!).collection("messages")
                    .document()

            // write new message to messages collection in room document and update room properties: lastMessage and lastMessageTimestamp
            Firebase.firestore.runBatch { batch ->
                batch.update(
                    roomRef,
                    mapOf("lastMessage" to text, "lastMessageTimestamp" to timestamp)
                )
                batch.set(newMessageRef, Message(text, timestamp, Hiya.userId))
            }
                .addOnSuccessListener {
                    binding.etNewMessage.text.clear()
                }
                .addOnFailureListener {
                    // TODO message failed
                }

        }
    }

}