package io.github.fabiantauriello.hiya.ui.main.liked

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.databinding.FragmentFinishedStoriesBinding
import io.github.fabiantauriello.hiya.databinding.FragmentLikedStoriesBinding
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.ui.main.finished.FinishedStoriesAdapter
import io.github.fabiantauriello.hiya.ui.main.inprogress.StoryListItemClickListener
import io.github.fabiantauriello.hiya.viewmodels.StoriesViewModel

class LikedStoriesFragment : Fragment(), StoryListItemClickListener {

    private val TAG = this::class.java.name

    private lateinit var binding: FragmentLikedStoriesBinding

    private val viewModel: StoriesViewModel by activityViewModels()

    private lateinit var adapter: FinishedStoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = FinishedStoriesAdapter(arrayListOf(), this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLikedStoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.listenForStories()
        binding.rvLikedStories.adapter = adapter

        // observe finished stories livedata and update rv
        viewModel.likedStoryList.observe(viewLifecycleOwner, { list ->
            adapter.updateList(list)
        })
    }

    override fun onStoryClick(story: Story) {

    }

}