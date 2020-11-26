package io.github.fabiantauriello.hiya.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import io.github.fabiantauriello.hiya.databinding.FragmentChatLogBinding
import io.github.fabiantauriello.hiya.domain.FirestoreQueryStatus
import io.github.fabiantauriello.hiya.viewmodels.ChatLogViewModel
import io.github.fabiantauriello.hiya.viewmodels.ChatLogViewModelFactory

// individual chat between users
class ChatLogFragment : Fragment() {

    private val TAG = this::class.java.name

    private val args: ChatLogFragmentArgs by navArgs()

    private var roomId: String? = null

    private lateinit var binding: FragmentChatLogBinding

    // need to specify view model factory because I need to pass in room Id
    private lateinit var viewModel: ChatLogViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        roomId = args.roomId

        binding = FragmentChatLogBinding.inflate(inflater, container, false)

        if (roomId != null) {
            viewModel = ViewModelProvider(this, ChatLogViewModelFactory(roomId!!)).get(ChatLogViewModel::class.java)
            viewModel.getMessages()
        } else {
            // TODO
            // cannot retrieve messages because roomId is null or create new room
            viewModel = ViewModelProvider(this, ChatLogViewModelFactory(null, args.privateRoomReceiver)).get(ChatLogViewModel::class.java)
            viewModel.setUpNewRoom()
        }

        configureChatLogRecyclerView()
        configureNewMessageButtonListener()
        configureObservers()

        // Inflate the layout for this fragment
        return binding.root
    }


    private fun configureChatLogRecyclerView() {
        // initialize recyclerview with empty adapter
        val adapter = ChatLogAdapter(arrayListOf())
        binding.rvChatLog.adapter = adapter
    }

    private fun configureObservers() {
        viewModel.messages.observe(viewLifecycleOwner, Observer {
            (binding.rvChatLog.adapter as ChatLogAdapter).update(it)
        })
        viewModel.newMessageStatus.observe(viewLifecycleOwner, Observer {
            when(it) {
                FirestoreQueryStatus.SUCCESS -> binding.etNewMessage.text.clear()
                FirestoreQueryStatus.FAILURE -> {} // TODO
                FirestoreQueryStatus.LOADING -> {} // TODO
            }
        })
    }

    private fun configureNewMessageButtonListener() {
        binding.btnSendNewMessage.setOnClickListener {
            val text = binding.etNewMessage.text.toString()
            if (text.trim().isNotEmpty()) {
                viewModel.addNewMessage(text, roomId!!)
            }
        }
    }

}