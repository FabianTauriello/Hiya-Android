package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.databinding.FragmentInProgressStoriesBinding
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.viewmodels.StoriesViewModel

// chat rooms
class InProgressStoriesFragment : Fragment(), StoryListItemClickListener {

    private val TAG = this::class.java.name

    private lateinit var binding: FragmentInProgressStoriesBinding

    private val viewModel: StoriesViewModel by activityViewModels()

    private lateinit var adapter: InProgressStoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = InProgressStoriesAdapter(arrayListOf(), this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: called")
        binding = FragmentInProgressStoriesBinding.inflate(inflater, container, false)

        // set default state of view before loading stories
        // TODO could put these in one function instead of three
        showProgressBar()
        hideFab()
        hideRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: called")
        // SETUP

        viewModel.listenForInProgressStories()
        binding.rvInProgressStories.adapter = adapter

        // VIEW LISTENERS

        binding.fabSelectUser.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.inProgressStoriesFragment) {
                findNavController().navigate(InProgressStoriesFragmentDirections.actionInProgressStoriesFragmentToUserListDialog())
            }
        }

        // OBSERVERS

        // observe in progress stories livedata and update rv
        viewModel.inProgressStoryList.observe(viewLifecycleOwner, { list ->
            adapter.updateList(list)
            Log.d(TAG, "onViewCreated: updated")
            hideProgressBar()
            showFab()
            showRecyclerView()
        })

        val s = Firebase.firestore.collection("stories").document("2KSsVIDkJ2Nwa3DwDZXw").collection("authors").document("EpTpbigrh2tzFQZcoqdB")
        s.update("done", false)

    }

    private fun hideRecyclerView() {
        binding.rvInProgressStories.visibility = View.GONE
    }

    private fun showRecyclerView() {
        binding.rvInProgressStories.visibility = View.VISIBLE
    }

    private fun hideFab() {
        binding.fabSelectUser.visibility = View.GONE
    }

    private fun showFab() {
        binding.fabSelectUser.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.pbLoadContacts.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.pbLoadContacts.visibility = View.VISIBLE
    }

    override fun onStoryClick(story: Story) {
        /*
        Check if the current destination matches this fragment because multiple quick taps of
        navigation buttons can cause an exception. After committing to one navigation action,
        Android can accept another action before the first one is complete, causing the exception.
         */
        if (findNavController().currentDestination?.id == R.id.inProgressStoriesFragment) {
            findNavController().navigate(
                InProgressStoriesFragmentDirections.actionInProgressStoriesFragmentToStoryLogFragment(
                    null, story
                )
            )
        }
    }

    // OTHER LIFECYCLE METHODS - TODO DELETE LATER

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach: called")
        super.onAttach(context)
    }

    override fun onResume() {
        Log.d(TAG, "onResume: called")
        super.onResume()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: called")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d(TAG, "onDetach: called")
        super.onDetach()
    }

    override fun onStop() {
        Log.d(TAG, "onStop: called")
        super.onStop()
    }

    override fun onPause() {
        Log.d(TAG, "onPause: called")
        super.onPause()
    }


}