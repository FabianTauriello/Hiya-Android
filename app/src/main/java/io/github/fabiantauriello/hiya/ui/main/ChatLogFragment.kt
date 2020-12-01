package io.github.fabiantauriello.hiya.ui.main

import android.os.Bundle
import android.util.Log
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

    private lateinit var binding: FragmentChatLogBinding

    // need to specify view model factory because I need to pass in room Id
    private lateinit var viewModel: ChatLogViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentChatLogBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this, ChatLogViewModelFactory(args.roomId, args.privateRoomReceiver)).get(ChatLogViewModel::class.java)

        configureChatLogRecyclerView()
        configureObservers()
        configureNewMessageButtonListener()

        return binding.root
    }

    private fun configureChatLogRecyclerView() {
        // initialize recyclerview with empty adapter
        val adapter = ChatLogAdapter(arrayListOf())
        binding.rvChatLog.adapter = adapter
    }

    private fun configureObservers() {
        viewModel.messages.observe(viewLifecycleOwner, Observer { newMessages ->
            newMessages.forEach { (binding.rvChatLog.adapter as ChatLogAdapter).update(it) }
            binding.rvChatLog.scrollToPosition(newMessages.size - 1)
        })
        viewModel.newMessageStatus.observe(viewLifecycleOwner, Observer {
            when(it) {
                FirestoreQueryStatus.SUCCESS -> binding.etNewMessage.text.clear()
                FirestoreQueryStatus.FAILURE -> {} // TODO
            }
        })
        viewModel.createNewRoomStatus.observe(viewLifecycleOwner, Observer {
            // remove progress bar if new room is successfully created or a new room isn't required
            if (it == FirestoreQueryStatus.SUCCESS || args.roomId != null) {
                removeProgressBar()
            }
        })
    }

    private fun configureNewMessageButtonListener() {
        binding.btnSendNewMessage.setOnClickListener {
            val text = binding.etNewMessage.text.toString()
            if (text.trim().isNotEmpty()) {
                viewModel.addNewMessage(text)
            }
        }
    }

    private fun removeProgressBar() {
        binding.pbLoadRoom.visibility = View.GONE
        binding.rvChatLog.visibility = View.VISIBLE
        binding.etNewMessage.visibility = View.VISIBLE
        binding.btnSendNewMessage.visibility = View.VISIBLE
    }


}