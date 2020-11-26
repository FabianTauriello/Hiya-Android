package io.github.fabiantauriello.hiya.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.fabiantauriello.hiya.databinding.FragmentNewMessageDialogBinding
import io.github.fabiantauriello.hiya.domain.Participant
import io.github.fabiantauriello.hiya.domain.User


class NewMessageDialog : BottomSheetDialogFragment(), ContactClickListener {

    private val TAG = this::class.java.name

    private lateinit var binding: FragmentNewMessageDialogBinding

    private val args: NewMessageDialogArgs by navArgs()

    private lateinit var contacts: Array<User>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(TAG, "onCreateView: called")

        binding = FragmentNewMessageDialogBinding.inflate(inflater, container, false)

        contacts = args.contacts

        configureContactsRecyclerView()

        return binding.root
    }

    private fun configureContactsRecyclerView() {
        if (contacts.isNotEmpty()) {
            // show recycler view
            binding.layoutShowContactList.visibility = View.VISIBLE

            // setup and connect adapter
            val adapter = ContactsAdapter(contacts, this)
            binding.rvContacts.adapter = adapter
        } else if (!args.contactsPermissionGranted) {
            // permission denied
            binding.layoutShowPhoneEntry.visibility = View.VISIBLE
            binding.layoutContactsPermissionDeniedWarning.visibility = View.VISIBLE
        } else {
            // no contacts on device matching firebase records
            // TODO
        }
    }

    override fun onContactClick(contact: User) {

        // check if any of the user's chat rooms contain the contact clicked on as a participant
        // if so, send that room id so that specific chat is opened instead of creating a new one
//        val roomsContainingContact = args.chatRooms.filter { room ->
//            room.participants.contains(contact.id)
//        }
//        val roomId = if (roomsContainingContact.isEmpty()) {
//            null
//        } else {
//            // only 1 room should be returned so we can use 0 as index // TODO will only work for private chats
//            roomsContainingContact[0].id
//        }
        // TODO UGLY. change it maybe with filter
        var roomId: String? = null
        for (room in args.chatRooms) {
            for (participant in room.participants) {
                if (participant.userId == contact.id) {
                    roomId = room.id
                }
            }
        }

        val action = NewMessageDialogDirections.actionNewMessageDialogToChatLogFragment(
            roomId,
            Participant(contact.id, contact.name)
        )
        findNavController().navigate(action)
    }


}
