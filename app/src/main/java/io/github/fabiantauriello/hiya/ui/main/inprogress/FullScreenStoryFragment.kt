package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.databinding.FragmentFullScreenStoryBinding
import io.github.fabiantauriello.hiya.viewmodels.StoryLogViewModel
import kotlinx.android.synthetic.main.finished_list_item.*

class FullScreenStoryFragment : Fragment() {

    private val TAG = this::class.java.name

    private lateinit var binding: FragmentFullScreenStoryBinding

    private val viewModel: StoryLogViewModel by navGraphViewModels(R.id.storyLogNestedGraph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFullScreenStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // initialize story text view
        binding.fragmentFullScreenStoryStoryText.text = viewModel.story.value?.data?.text
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "full onAttach: called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "full onResume: called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "full onDestroy: called $id")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "full onDetach: called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "full onStop: called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "full onPause: called")
    }

}