package io.github.fabiantauriello.hiya.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.databinding.FragmentChatRoomsBinding
import io.github.fabiantauriello.hiya.domain.User
import io.github.fabiantauriello.hiya.viewmodels.ChatRoomsViewModel

// chat threads
class ChatRoomsFragment : Fragment() {

    private val TAG = this::class.java.name

    private lateinit var binding: FragmentChatRoomsBinding

    private val viewModel: ChatRoomsViewModel by viewModels()

    private var contacts: ArrayList<User> = arrayListOf()

    private var contactsPermissionGranted = true

    lateinit var adapter: ChatRoomsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentChatRoomsBinding.inflate(inflater, container, false)

        requestContactsPermission()

        configureChatRoomsRecyclerView()
        configureChatRoomsLiveDataObserver()
        configureNewMessageButtonListener()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun configureChatRoomsRecyclerView() {
        // setup and connect adapter
        adapter = ChatRoomsAdapter(arrayListOf(), viewModel)
        binding.rvChatRooms.adapter = adapter
    }

    private fun requestContactsPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            // request permission
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                Hiya.CONTACTS_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission is already granted, show contact list
            configureContactsLiveDataObserver()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Hiya.CONTACTS_PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted, show contact list
                    configureContactsLiveDataObserver()
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision.
                    contactsPermissionGranted = false
                    removeProgressBar()
                }
            }
        }
    }

    private fun configureContactsLiveDataObserver() {
        viewModel.contacts.observe(viewLifecycleOwner, Observer {
            contacts = it
            removeProgressBar()
        })
    }

    private fun configureChatRoomsLiveDataObserver() {
        viewModel.rooms.observe(viewLifecycleOwner, Observer {
            adapter.update(it)
        })
    }

    private fun removeProgressBar() { // TODO need to check how this is called
        binding.pbLoadContacts.visibility = View.GONE
        binding.fabNewMessage.visibility = View.VISIBLE
        binding.rvChatRooms.visibility = View.VISIBLE
    }

    private fun configureNewMessageButtonListener() {
        binding.fabNewMessage.setOnClickListener {
            val action = ChatRoomsFragmentDirections.actionChatRoomsFragmentToNewMessageDialog(
                contacts.toTypedArray(),
                contactsPermissionGranted,
                (binding.rvChatRooms.adapter as ChatRoomsAdapter).getRooms().toTypedArray()
            )
            findNavController().navigate(action)
        }
    }

}