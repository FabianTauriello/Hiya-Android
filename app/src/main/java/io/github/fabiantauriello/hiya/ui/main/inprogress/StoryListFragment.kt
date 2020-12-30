package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import io.github.fabiantauriello.hiya.databinding.FragmentStoryListBinding
import io.github.fabiantauriello.hiya.domain.QueryStatus
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.util.Utils
import io.github.fabiantauriello.hiya.viewmodels.InProgressSharedViewModel

// chat rooms
class StoryListFragment : Fragment(), StoryListItemClickListener {

    private val LC_TAG = "LC_SLF"
    private val TAG = this::class.java.name

    private lateinit var binding: FragmentStoryListBinding

    private val sharedViewModel: InProgressSharedViewModel by activityViewModels()

    private lateinit var adapter: StoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LC_TAG, "onCreate: called")
        adapter = StoryListAdapter(arrayListOf(), sharedViewModel, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(LC_TAG, "onCreateView: called")

        binding = FragmentStoryListBinding.inflate(inflater, container, false)
        binding.rvInProgressStories.adapter = adapter
        binding.fabSelectUser.setOnClickListener {
            sharedViewModel.updateUserListIsUnseenFlag(false)
            findNavController().navigate(StoryListFragmentDirections.actionStoryListFragmentToUserSelectionDialog())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(LC_TAG, "onViewCreated: called")

        sharedViewModel.startListeningForStories()

        // observe story list live data to populate rv
        sharedViewModel.storyListResponse.observe(viewLifecycleOwner, Observer { response ->
            Log.d(TAG, "onViewCreated: response = ${response.queryStatus}")
            when (response.queryStatus) {
                QueryStatus.PENDING -> {
                    Log.d(TAG, "onViewCreated: pending")
                }
                QueryStatus.SUCCESS -> {
                    adapter.update(response.data!!)
                    showData()
                    removeProgressBar()
                }
                QueryStatus.ERROR -> {
                    Log.d(TAG, "failed")
                    showError()
                    removeProgressBar()
                }
            }
        })

        // Observe user list flag and enable fab if user list is hidden
        sharedViewModel.userListIsUnseen.observe(viewLifecycleOwner, Observer { userListIsHidden ->
            binding.fabSelectUser.isEnabled = userListIsHidden
        })

    }

    private fun removeProgressBar() {
        binding.pbLoadContacts.visibility = View.GONE
    }

    private fun showData() {
        binding.rvInProgressStories.visibility = View.VISIBLE
        binding.fabSelectUser.visibility = View.VISIBLE
    }

    private fun showError() {
        // TODO
    }

    override fun onStoryClick(story: Story) {
        findNavController().navigate(
            StoryListFragmentDirections.actionStoryListFragmentToStoryLogFragment(
                Utils.getCoAuthorForStory(story), story.id, story.title
            )
        )
    }

    // OTHER LIFECYCLE METHODS - TODO DELETE LATER

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(LC_TAG, "onAttach: called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(LC_TAG, "onResume: called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(LC_TAG, "onDestroy: called $id")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(LC_TAG, "onDetach: called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(LC_TAG, "onStop: called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(LC_TAG, "onPause: called")
    }


}