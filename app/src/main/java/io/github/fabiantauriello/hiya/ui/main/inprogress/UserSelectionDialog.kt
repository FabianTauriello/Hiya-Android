package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.databinding.UserSelectionDialogBinding
import io.github.fabiantauriello.hiya.domain.Author
import io.github.fabiantauriello.hiya.domain.QueryStatus
import io.github.fabiantauriello.hiya.domain.User
import io.github.fabiantauriello.hiya.viewmodels.InProgressSharedViewModel


class UserSelectionDialog : BottomSheetDialogFragment(), UserClickListener {

    private val TAG = this::class.java.name

    private lateinit var binding: UserSelectionDialogBinding

    private val sharedViewModel: InProgressSharedViewModel by activityViewModels()

    private lateinit var adapter: UserListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = UserListAdapter(arrayListOf(), this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = UserSelectionDialogBinding.inflate(inflater, container, false)
        binding.rvUsers.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requestContactsPermission()
    }

    private fun requestContactsPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            // request permission
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                Hiya.CONTACTS_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission is already granted, get and show user list
            sharedViewModel.getUsersWhoAreContacts()
            observeUsersResponse()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Hiya.CONTACTS_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    sharedViewModel.getUsersWhoAreContacts()
                    observeUsersResponse()
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision.
                    showUserListTitle()
                    showPhoneEntryLayout()
                }
            }
        }
    }

    private fun observeUsersResponse() {
        // Observe users live data to populate rv
        sharedViewModel.userListResponse.observe(viewLifecycleOwner, Observer { response ->
            when (response.queryStatus) {
                QueryStatus.PENDING -> {
                    Log.d(TAG, "observeUsersResponse: pending")
                }
                QueryStatus.SUCCESS -> {
                    adapter.update(response.data!!)
                    showUserListTitle()
                    showUserListLayout()
                }
                QueryStatus.ERROR -> {
                    showError()
                }
            }
        })
    }

    private fun showPhoneEntryLayout() {
        binding.layoutPhoneEntry.visibility = View.VISIBLE
    }

    private fun showUserListLayout() {
        binding.layoutUserList.visibility = View.VISIBLE
    }

    private fun showUserListTitle() {
        binding.tvUserSelectionTitle.visibility = View.VISIBLE
    }

    private fun showError() {
        // TODO
    }

    // create new story with given contact
    override fun onUserClick(contact: User) {
        Log.d(TAG, "onContactClick: ${contact.profileImageUri}")
        val action = UserSelectionDialogDirections.actionUserSelectionDialogToStoryLogFragment(
            Author(contact.id, contact.name, contact.profileImageUri)
        )
        findNavController().navigate(action)
    }

}
