package io.github.fabiantauriello.hiya.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.github.fabiantauriello.hiya.databinding.FragmentChatLogBinding
import io.github.fabiantauriello.hiya.domain.FirebaseChatRoom
import io.github.fabiantauriello.hiya.domain.Message

// individual chat between users
class ChatLogFragment : Fragment() {

    private val LOG_TAG = this::class.java.name

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
        getMessagesForRoom()
        configureNewMessageButtonListener()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun configureChatLogRecyclerView() {
        // initialize recyclerview with empty adapter
        val adapter = ChatLogAdapter(arrayListOf())
        binding.rvChatLog.adapter = adapter
    }

    private fun getMessagesForRoom() {
        if(roomId != null) {

        } else {
            // new room will need to be created

        }
    }

    private fun configureNewMessageButtonListener() {
        binding.btnSendNewMessage.setOnClickListener {

        }
    }

}