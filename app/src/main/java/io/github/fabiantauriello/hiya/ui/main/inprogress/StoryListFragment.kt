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
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.databinding.FragmentStoryListBinding
import io.github.fabiantauriello.hiya.domain.Author
import io.github.fabiantauriello.hiya.domain.QueryStatus
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.domain.User
import io.github.fabiantauriello.hiya.util.Utils
import io.github.fabiantauriello.hiya.viewmodels.StoryListViewModel

// chat rooms
class StoryListFragment : Fragment(), StoryListItemClickListener {

    private val TAG = this::class.java.name

    private lateinit var binding: FragmentStoryListBinding

    private val viewModel: StoryListViewModel by activityViewModels()

    private lateinit var adapter: StoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = StoryListAdapter(arrayListOf(), this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: called")
        binding = FragmentStoryListBinding.inflate(inflater, container, false)

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

        viewModel.listenForStories()
        binding.rvInProgressStories.adapter = adapter // TODO only assign adapter after updates

        // VIEW LISTENERS

        binding.fabSelectUser.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.storyListFragment) {
                findNavController().navigate(StoryListFragmentDirections.actionStoryListFragmentToUserListDialog())
            }
        }

        // OBSERVERS

        // observe user-story response to populate rv
        viewModel.userStoryPairResponse.observe(viewLifecycleOwner, { list ->
            adapter.updateList(list)
            hideProgressBar()
            showFab()
            showRecyclerView()
        })

//        val ref = Firebase.firestore.collection("stories").document()
//        ref.set(Story(
//            ref.id,
//            "story title",
//            "once upon a time",
//            System.currentTimeMillis().toString(),
//            false,
//            4,
//            nextTurn = Hiya.userId,
//            authorIds = arrayListOf("YY4TIEm4G59uSfBX8ZVX", "yLcVVkVEKMbjLrCm4UaM"),
//            authors = arrayListOf(
//                Author("YY4TIEm4G59uSfBX8ZVX", "Fabian", "https://firebasestorage.googleapis.com/v0/b/hiya-57ff9.appspot.com/o/images%2F15120231-213d-4217-b7ee-eed6ed2653db?alt=media&token=1a68a451-73a8-4f49-9775-dd672863b99d", liked = false, done = false),
//                Author("yLcVVkVEKMbjLrCm4UaM", "Lisa Simpson", "https://firebasestorage.googleapis.com/v0/b/hiya-57ff9.appspot.com/o/images%2FLisa_Simpson_official.png?alt=media&token=f0371dec-c61d-4741-b3bd-c1b2f30d407f", false, false)
//            )
//        ))
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