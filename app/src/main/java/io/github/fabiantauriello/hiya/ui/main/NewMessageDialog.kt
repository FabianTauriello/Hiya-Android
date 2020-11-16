package io.github.fabiantauriello.hiya.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.fabiantauriello.hiya.databinding.FragmentNewMessageDialogBinding
import io.github.fabiantauriello.hiya.domain.Contact


class NewMessageDialog : BottomSheetDialogFragment(), ContactClickListener {

    private val LOG_TAG = this::class.java.name

    private val CONTACTS_PERMISSION_REQUEST_CODE = 1

    private var _binding: FragmentNewMessageDialogBinding? = null
    private val binding get() = _binding!!

    private val args: NewMessageDialogArgs by navArgs()

    private lateinit var contacts: Array<Contact>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNewMessageDialogBinding.inflate(inflater, container, false)

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

    override fun onContactClick(contact: Contact) {
        // TODO get actual room id pass in empty string if no private chat exists with desired contact
        val roomId = null
        val action = NewMessageDialogDirections.actionNewMessageDialogToChatLogFragment(null, contact.id, null)
        findNavController().navigate(action)
    }


}
