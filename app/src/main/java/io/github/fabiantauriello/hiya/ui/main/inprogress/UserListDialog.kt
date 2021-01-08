package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.Manifest
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
import io.github.fabiantauriello.hiya.databinding.UserListDialogBinding
import io.github.fabiantauriello.hiya.domain.QueryStatus
import io.github.fabiantauriello.hiya.domain.User
import io.github.fabiantauriello.hiya.viewmodels.StoryListViewModel
import io.github.fabiantauriello.hiya.viewmodels.UserListViewModel


class UserListDialog : BottomSheetDialogFragment(), UserClickListener {

    private val TAG = this::class.java.name

    private lateinit var binding: UserListDialogBinding

    private val viewModel: UserListViewModel by activityViewModels()

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
        binding = UserListDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvUsers.adapter = adapter

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
            viewModel.getUsersWhoAreContacts()
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
                    viewModel.getUsersWhoAreContacts()
                    observeUsersResponse()
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision.

                    showUserListTitle()
                    // Show phone entry layout
                    showPhoneEntryLayout()
                }
            }
        }
    }

    private fun observeUsersResponse() {
        // Observe users live data to populate rv
        viewModel.userListResponse.observe(viewLifecycleOwner, Observer { response ->
            when (response.queryStatus) {
                QueryStatus.SUCCESS -> {
                    adapter.update(response.data!!)
                    showUserListTitle()
                    showUserListLayout()
                }
                QueryStatus.ERROR -> {
                    // TODO
                }
            }
        })
    }

    private fun showUserListLayout() {
        binding.layoutUserList.visibility = View.VISIBLE
    }

    private fun showPhoneEntryLayout() {
        binding.layoutPhoneEntry.visibility = View.VISIBLE
    }

    private fun showUserListTitle() {
        binding.tvUserListTitle.visibility = View.VISIBLE
    }

    // create new story with given contact
    override fun onUserClick(contact: User) {
        Log.d(TAG, "onContactClick: ${contact.profileImageUri}")
        val action = UserListDialogDirections.actionUserListDialogToStoryLogFragment(
            contact, null
        )
        findNavController().navigate(action)
    }

}
