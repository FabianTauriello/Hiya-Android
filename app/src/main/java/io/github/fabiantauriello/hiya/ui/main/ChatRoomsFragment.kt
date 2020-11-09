package io.github.fabiantauriello.hiya.ui.main

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.databinding.FragmentChatRoomsBinding
import io.github.fabiantauriello.hiya.domain.ChatRoom

// chat threads
class ChatRoomsFragment : Fragment(), ChatRoomClickListener {

    private val LOG_TAG = this::class.java.name

    private var _binding: FragmentChatRoomsBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentChatRoomsBinding.inflate(inflater, container, false)

        configureNewMessageButtonListener()
        configureChatRoomsRecyclerView()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun configureChatRoomsRecyclerView() {
        // setup and connect adapter
        val adapter = ChatRoomsAdapter(arrayListOf(), this)
        binding.rvChatRooms.adapter = adapter

        getRooms(true)
        getRooms(false)
    }

    private fun getRooms(private: Boolean) {
        val uid = FirebaseAuth.getInstance().uid

    }

    private fun configureNewMessageButtonListener() {
        binding.fabNewMessage.setOnClickListener {
            findNavController().navigate(R.id.action_chatRoomsFragment_to_newMessageDialog)
        }
    }

    override fun onChatRoomClick(chatRoom: ChatRoom) {
        Log.d(LOG_TAG, "onChatRoomClick: $chatRoom")
        val action = ChatRoomsFragmentDirections.actionChatRoomsFragmentToChatLogFragment(chatRoom.id, chatRoom.title)
        findNavController().navigate(action)
    }


}