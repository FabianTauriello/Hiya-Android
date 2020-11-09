package io.github.fabiantauriello.hiya.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.databinding.FragmentNewMessageDialogBinding
import io.github.fabiantauriello.hiya.domain.User
import io.github.fabiantauriello.hiya.ui.registration.CodeEntryFragmentDirections


class NewMessageDialog : BottomSheetDialogFragment(), ContactClickListener {

    private val LOG_TAG = this::class.java.name

    private val CONTACTS_PERMISSION_REQUEST_CODE = 1

    private var _binding: FragmentNewMessageDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        val ref = FirebaseDatabase.getInstance().getReference("/users/QYNpAZrG02dd0vSImlV0RgX4w3q3")
//        ref.setValue(User("jeb", "+4407283948271", ""))

        _binding = FragmentNewMessageDialogBinding.inflate(inflater, container, false)

        requestContactsPermission()

        return binding.root
    }

    private fun requestContactsPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            // request permission
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                CONTACTS_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission is granted, show contact list
            configureContactListView()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CONTACTS_PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted, show contact list
                    configureContactListView()
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision.
                    configurePhoneNumberEntryView()
                }
            }
        }
    }

    private fun configureContactListView() {

        // The content URI of the phone table
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        // A "projection" defines the columns that will be returned for each row
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        // Selection criteria
        val selection = null
        // Selection criteria
        val selectionArgs = null
        // The sort order for the returned rows
        val sortOrder = null

        // get content resolver to interact with content provider
        val contentResolver = requireActivity().contentResolver
        // queries the contacts and returns results
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)


//        // log all data from cursor // TODO remove later
//        DatabaseUtils.dumpCursor(cursor)

        when (cursor?.count) {
            null -> {
                // Insert code here to handle the error. Be sure not to use the cursor!
                // You may want to call android.util.Log.e() to log this error.
            }
            0 -> {
                // Insert code here to notify the user that no contacts were found. This isn't
                // necessarily an error.
                binding.layoutShowPhoneEntry.visibility = View.VISIBLE
            }
            else -> {
                // Insert code here to do something with the results

                // show recycler view
                binding.layoutShowContactList.visibility = View.VISIBLE

                // setup and connect adapter
                val adapter = ContactsAdapter(arrayListOf(), this)
                binding.rvContacts.adapter = adapter

                while (cursor.moveToNext()) {
                    val name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                    Firebase.firestore

                    // Create a new user with a first and last name
//                    val user = User(
//                        name,
//                        number,
//
//                    )




                }

            }
        }

    }

    private fun configurePhoneNumberEntryView() {
        binding.layoutShowPhoneEntry.visibility = View.VISIBLE
        binding.layoutContactsPermissionDeniedWarning.visibility = View.VISIBLE
    }

    override fun onContactClick() {
        // TODO get actual room id pass in empty string if no private chat exists with desired contact
        val roomId = null
        val action = NewMessageDialogDirections.actionNewMessageDialogToChatLogFragment(roomId, "title")
        findNavController().navigate(action)
    }


}
