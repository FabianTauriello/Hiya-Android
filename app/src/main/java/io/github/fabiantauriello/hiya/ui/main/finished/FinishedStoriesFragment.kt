package io.github.fabiantauriello.hiya.ui.main.finished

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import io.github.fabiantauriello.hiya.databinding.FragmentFinishedStoriesBinding
import io.github.fabiantauriello.hiya.domain.QueryStatus
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.ui.main.inprogress.StoryListItemClickListener
import io.github.fabiantauriello.hiya.viewmodels.StoriesViewModel

class FinishedStoriesFragment : Fragment(), StoryListItemClickListener {

    private val TAG = this::class.java.name

    private lateinit var binding: FragmentFinishedStoriesBinding

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
        binding = FragmentFinishedStoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // SETUP

        viewModel.listenForFinishedStories()

        val dividerItemDecoration = DividerItemDecoration(binding.fragmentFinishedStoriesRvFinishedStories.context, LinearLayout.VERTICAL)
        binding.fragmentFinishedStoriesRvFinishedStories.addItemDecoration(dividerItemDecoration)
        binding.fragmentFinishedStoriesRvFinishedStories.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel

        // observe finished stories livedata and update rv
        viewModel.finishedStoryList.observe(viewLifecycleOwner, { response ->
            if (response.queryStatus == QueryStatus.SUCCESS) {
                adapter.updateList(response.list)
            }
        })
    }

    override fun onStoryClick(story: Story) {

    }

}