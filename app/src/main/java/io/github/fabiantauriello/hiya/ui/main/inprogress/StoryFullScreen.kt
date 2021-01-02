package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.databinding.FragmentStoryFullScreenBinding
import io.github.fabiantauriello.hiya.viewmodels.InProgressSharedViewModel


class StoryFullScreen : Fragment() {

    private val LC_TAG = "LC_FULL"
    private val TAG = this::class.java.name

    private lateinit var binding: FragmentStoryFullScreenBinding

    private val sharedViewModel: InProgressSharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoryFullScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // set up 'complete' toggle for what decision the user has (or hasn't) made in the past
        binding.toggleComplete.isChecked = sharedViewModel.markedAsComplete

        // initialize story text view
        binding.tvStoryFull.text = sharedViewModel.storyText.value

        binding.toggleComplete.setOnCheckedChangeListener { _, isComplete ->
            Log.d(TAG, "onViewCreated: $isComplete")
            sharedViewModel.markAsComplete(isComplete)
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(LC_TAG, "full onAttach: called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(LC_TAG, "full onResume: called")
        // hide action bar
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(LC_TAG, "full onDestroy: called $id")
        // show action bar
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
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