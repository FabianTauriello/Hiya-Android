package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.databinding.FragmentFullScreenStoryBinding
import io.github.fabiantauriello.hiya.viewmodels.StoryLogViewModel


class FullScreenStoryFragment : Fragment() {

    private val LC_TAG = "LC_FULL"
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
        binding.tvStoryFull.text = viewModel.storyLogText.value
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(LC_TAG, "full onAttach: called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(LC_TAG, "full onResume: called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(LC_TAG, "full onDestroy: called $id")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(LC_TAG, "full onDetach: called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(LC_TAG, "full onStop: called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(LC_TAG, "full onPause: called")
    }

}