package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.databinding.FragmentStoryListBinding
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.util.Utils
import io.github.fabiantauriello.hiya.viewmodels.InProgressSharedViewModel

// chat rooms
class StoryListFragment : Fragment(), StoryListItemClickListener {

    private val TAG = this::class.java.name

    private lateinit var binding: FragmentStoryListBinding

    private val sharedViewModel: InProgressSharedViewModel by activityViewModels()

    private var contactsPermissionGranted = true

    lateinit var adapter: StoryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: called")
        binding = FragmentStoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: called")

        val s = findNavController().backStack
        Log.d(TAG, "Backstack...")
        for (frag in s) {
            Log.d(TAG, "${frag.destination.displayName}")
        }

        sharedViewModel.getUsersMatchingContactsOnDevice()
        sharedViewModel.configureStoriesListener()

        requestContactsPermission()

        configureRecyclerView()
        configureObservers()
        configureNewStoryButtonListener()
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
        sharedViewModel.contacts.observe(viewLifecycleOwner, Observer {
            removeProgressBar()
        })
    }

    private fun removeProgressBar() { // TODO need to check how this is called
        binding.pbLoadContacts.visibility = View.GONE
        binding.fabNewMessage.visibility = View.VISIBLE
        binding.rvInProgressStories.visibility = View.VISIBLE
    }

    private fun configureRecyclerView() {
        // setup and connect adapter
        adapter = StoryListAdapter(arrayListOf(), sharedViewModel, this)
        binding.rvInProgressStories.adapter = adapter
    }

    private fun configureObservers() {
        sharedViewModel.storyList.observe(viewLifecycleOwner, Observer { storyList ->
            adapter.update(storyList)
        })
    }

    private fun configureNewStoryButtonListener() {
        binding.fabNewMessage.setOnClickListener {
            val action =
                StoryListFragmentDirections.actionStoryListFragmentToContactSelectionDialog(
                    contactsPermissionGranted
                )
            findNavController().navigate(action)
        }
    }

    override fun onStoryClick(story: Story) {
        findNavController().navigate(StoryListFragmentDirections.actionStoryListFragmentToStoryLogFragment(
            Utils.getCoAuthorForStory(story), story.id, story.title))
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: called")
    }

}