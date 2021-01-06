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
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.databinding.FragmentStoryListBinding
import io.github.fabiantauriello.hiya.domain.QueryStatus
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.util.Utils
import io.github.fabiantauriello.hiya.viewmodels.StoryListViewModel

// chat rooms
class StoryListFragment : Fragment(), StoryListItemClickListener {

    private val LC_TAG = "LC_STORY_LIST"
    private val TAG = this::class.java.name

    private lateinit var binding: FragmentStoryListBinding

    private val viewModel: StoryListViewModel by activityViewModels()

    private lateinit var adapter: StoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = StoryListAdapter(arrayListOf(), viewModel, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // SETUP

        viewModel.listenForStories()
        binding.rvInProgressStories.adapter = adapter

        // VIEW LISTENERS

        binding.fabSelectUser.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.storyListFragment) {
                findNavController().navigate(StoryListFragmentDirections.actionStoryListFragmentToUserListDialog())
            }
        }

        // OBSERVERS

        // observe story list live data
        viewModel.storyListResponse.observe(viewLifecycleOwner, { response ->
            when (response.queryStatus) {
                QueryStatus.PENDING -> {
                }
                QueryStatus.SUCCESS -> {
                    adapter.clearList()
                    for (story in response.data!!) {
                        val userId = Utils.getCoAuthor(story)
                        viewModel.getUserInfo(userId, story)
                    }
                }
                QueryStatus.ERROR -> {
                    // TODO
                    // remove progress bar
                    removeProgressBar()
                }
            }
        })
        // observe user-story response to populate rv
        viewModel.userStoryPairResponse.observe(viewLifecycleOwner, { response ->
            when (response.queryStatus) {
                QueryStatus.SUCCESS -> {
                    adapter.addItem(response.data!!)
                    showRecyclerView()
                    showFab()
                    removeProgressBar()
                }
                QueryStatus.PENDING -> {}
                QueryStatus.ERROR -> {}
            }
        })

    }

    private fun showRecyclerView() {
        binding.rvInProgressStories.visibility = View.VISIBLE
    }

    private fun showFab() {
        binding.fabSelectUser.visibility = View.VISIBLE
    }

    private fun removeProgressBar() {
        binding.pbLoadContacts.visibility = View.GONE
    }

    override fun onStoryClick(story: Story) {
        /*
        Check if the current destination matches this fragment because multiple quick taps of
        navigation buttons can cause an exception. After committing to one navigation action,
        Android can accept another action before the first one is complete, causing the exception.
         */
        if (findNavController().currentDestination?.id == R.id.storyListFragment) {
            findNavController().navigate(
                StoryListFragmentDirections.actionStoryListFragmentToStoryLogFragment(
                    null, story
                )
            )
        }
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