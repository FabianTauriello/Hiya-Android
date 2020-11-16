package io.github.fabiantauriello.hiya.ui.main

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.Manifest
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.domain.ChatRoomItem
import io.github.fabiantauriello.hiya.databinding.FragmentChatRoomsBinding
import io.github.fabiantauriello.hiya.domain.Contact

// chat threads
class ChatRoomsFragment : Fragment(), ChatRoomClickListener {

    private val LOG_TAG = this::class.java.name

    private val CONTACTS_PERMISSION_REQUEST_CODE = 1

    private var _binding: io.github.fabiantauriello.hiya.databinding.FragmentChatRoomsBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // initialize with empty list
    private val contacts: ArrayList<Contact> = arrayListOf()

    private var contactsPermissionGranted = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentChatRoomsBinding.inflate(inflater, container, false)

        requestContactsPermission()

        initializeChatRoomsListener()
        configureNewMessageButtonListener()

        // Inflate the layout for this fragment
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
            getContacts()
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
                    getContacts()
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

    private fun getContacts() {
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

        when (cursor?.count) {
            null -> {
                // Insert code here to handle the error. Be sure not to use the cursor!
                // You may want to call android.util.Log.e() to log this error.
            }
            0 -> {
                // Insert code here to notify the user that no contacts were found. This isn't
                // necessarily an error.
            }
            else -> {
                // Insert code here to do something with the results

                val deviceContactNameList = arrayListOf<String>()
                val deviceContactPhoneNumberList = arrayListOf<String>()

                // get contact names and phone numbers from device
                while (cursor.moveToNext()) {
                    val name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                    deviceContactNameList.add(name)
                    deviceContactPhoneNumberList.add(number)
                }

                // get all user documents and check if its phone number is matches a phone number on the device
                // If so, then add it to the contacts list stored in this app
                Firebase.firestore.collection("users").get()
                    .addOnSuccessListener { users ->
                        if (!users.isEmpty) {
                            for (user in users.documents) {
                                val userPhoneNumber = user.get("phoneNumber") as String
                                val index = deviceContactPhoneNumberList.indexOf(userPhoneNumber)
                                if (index != -1) {
                                    // contact has Hiya
                                    contacts.add(Contact(user.id, deviceContactNameList[index], userPhoneNumber))
                                }
                            }
                        }

                        removeProgressBar()
                    }
                    .addOnFailureListener {

                    }
            }
        }
    }

    private fun removeProgressBar() {
        binding.pbLoadContacts.visibility = View.GONE
        binding.fabNewMessage.visibility = View.VISIBLE
        binding.rvChatRooms.visibility = View.VISIBLE
    }

    private fun initializeChatRoomsListener() {
        // setup and connect adapter
        val adapter = ChatRoomsAdapter(arrayListOf(), this)
        binding.rvChatRooms.adapter = adapter

        /*
        *
        * You can listen to a document with the onSnapshot() method. An initial call using the callback you
        * provide creates a document snapshot immediately with the current contents of the single document.
        * Then, each time the contents change, another call updates the document snapshot.
        *
        */

        Firebase.firestore.collection("rooms").whereArrayContains("participants", Hiya.userId)
            .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(LOG_TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            val newRooms = arrayListOf<ChatRoomItem>()
            for (doc in snapshot?.documents!!) {
                val id = doc.id
                val lastMessage = doc.get("lastMessage").toString()
                val lastMessageTimestamp = doc.get("lastMessageTimestamp").toString()
                newRooms.add(ChatRoomItem(id, lastMessage, lastMessageTimestamp))
            }
            adapter.replaceAllRooms(newRooms)
        }
    }

    private fun configureNewMessageButtonListener() {
        binding.fabNewMessage.setOnClickListener {
            val action = ChatRoomsFragmentDirections.actionChatRoomsFragmentToNewMessageDialog(
                contacts.toTypedArray(),
                contactsPermissionGranted
            )
            findNavController().navigate(action)
        }
    }

    override fun onChatRoomClick(chatRoom: ChatRoomItem) {
        val action = ChatRoomsFragmentDirections.actionChatRoomsFragmentToChatLogFragment(chatRoom.id)
        findNavController().navigate(action)
    }

}