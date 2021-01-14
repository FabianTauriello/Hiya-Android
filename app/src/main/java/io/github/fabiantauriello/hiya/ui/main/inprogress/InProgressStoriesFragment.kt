package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.databinding.FragmentInProgressStoriesBinding
import io.github.fabiantauriello.hiya.domain.QueryStatus
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: called")
        // SETUP

        viewModel.listenForInProgressStories()

        val dividerItemDecoration = DividerItemDecoration(binding.fragmentInProgressStoriesRvInProgressStories.context, LinearLayout.VERTICAL)
        binding.fragmentInProgressStoriesRvInProgressStories.addItemDecoration(dividerItemDecoration)
        binding.fragmentInProgressStoriesRvInProgressStories.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel

        // VIEW LISTENERS

        binding.fragmentInProgressStoriesFabSelectUser.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.inProgressStoriesFragment) {
                findNavController().navigate(InProgressStoriesFragmentDirections.actionInProgressStoriesFragmentToUserListDialog())
            }
        }

        // OBSERVERS

        // observe in progress stories livedata and update rv
        viewModel.inProgressStoryList.observe(viewLifecycleOwner, { response ->
            Log.d(TAG, "onViewCreated: observed")
            if (response.queryStatus == QueryStatus.SUCCESS) {
                adapter.updateList(response.list)
            }
        })

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

//    override fun onAttach(context: Context) {
//        Log.d(TAG, "onAttach: called")
//        super.onAttach(context)
//    }
//
//    override fun onResume() {
//        Log.d(TAG, "onResume: called")
//        super.onResume()
//    }
//
//    override fun onDestroy() {
//        Log.d(TAG, "onDestroy: called")
//        super.onDestroy()
//    }
//
//    override fun onDetach() {
//        Log.d(TAG, "onDetach: called")
//        super.onDetach()
//    }
//
//    override fun onStop() {
//        Log.d(TAG, "onStop: called")
//        super.onStop()
//    }
//
//    override fun onPause() {
//        Log.d(TAG, "onPause: called")
//        super.onPause()
//    }


}