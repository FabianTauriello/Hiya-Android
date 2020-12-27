package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.fabiantauriello.hiya.databinding.ContactSelectionDialogBinding
import io.github.fabiantauriello.hiya.domain.Author
import io.github.fabiantauriello.hiya.domain.User
import io.github.fabiantauriello.hiya.viewmodels.InProgressSharedViewModel


class ContactSelectionDialog : BottomSheetDialogFragment(), ContactClickListener {

    private val TAG = this::class.java.name

    private lateinit var binding: ContactSelectionDialogBinding

    private val args: ContactSelectionDialogArgs by navArgs()

    private val sharedViewModel: InProgressSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ContactSelectionDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureContactsLiveDataObserver()
    }

    private fun configureContactsLiveDataObserver() {
        sharedViewModel.contacts.observe(viewLifecycleOwner, Observer { contacts ->
            if (contacts.isNotEmpty()) {
                // show recycler view
                binding.layoutShowContactList.visibility = View.VISIBLE

                // setup and connect adapter
                val adapter = ContactSelectionAdapter(contacts, this)
                binding.rvContacts.adapter = adapter
            } else if (!args.contactsPermissionGranted) {
                // permission denied
                binding.layoutShowPhoneEntry.visibility = View.VISIBLE
                binding.layoutContactsPermissionDeniedWarning.visibility = View.VISIBLE
            } else {
                // no contacts on device matching firebase records
                // TODO
            }
        })
    }

    // create new story with given contact
    override fun onContactClick(contact: User) {
        Log.d(TAG, "onContactClick: ${contact.profileImageUri}")
        val action = ContactSelectionDialogDirections.actionContactSelectionDialogToStoryLogFragment(
            Author(contact.id, contact.name, contact.profileImageUri)
        )
        findNavController().navigate(action)
    }


}
