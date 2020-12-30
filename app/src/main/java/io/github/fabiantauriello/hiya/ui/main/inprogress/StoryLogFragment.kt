package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.databinding.FragmentStoryLogBinding
import io.github.fabiantauriello.hiya.domain.QueryStatus
import io.github.fabiantauriello.hiya.viewmodels.InProgressSharedViewModel


// individual chat between users
class StoryLogFragment : Fragment() {

    private val TAG = this::class.java.name

    private val args: StoryLogFragmentArgs by navArgs()

    private lateinit var binding: FragmentStoryLogBinding

    private val sharedViewModel: InProgressSharedViewModel by activityViewModels()

    private var isNewStory = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isNewStory = args.storyId.isEmpty()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoryLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (args.storyId.isEmpty()) {
            // new story required
            findNavController().navigate(StoryLogFragmentDirections.actionStoryLogFragmentToEditStoryTitleDialog(args.coAuthor))
        } else {
            // existing story can be shown
            sharedViewModel.updateStoryId(args.storyId)
            sharedViewModel.startListeningForTextChangesToStory()
        }

        binding.viewModel = sharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.btnAddToStory.setOnClickListener {
            val text = binding.etNewWord.text.toString().trim()
            binding.etNewWord.text.clear()
            binding.btnAddToStory.isEnabled = false
            sharedViewModel.addNewWord(text)
        }
        binding.etNewWord.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                /*
                Calculate the number of words entered by the user.
                '\\s+' = 1 or more white spaces.
                I use this regular expression to split the text input into separate words,
                based on the space that divides them.
                e.g. "hello there" AND "hello    there" = 2 words
                */
                val numOfWordsEntered = s.toString().trim().split("\\s+".toRegex()).size
                // enable the button if the text input is not empty AND the user has entered only 1 word
                binding.btnAddToStory.isEnabled = s.toString().trim().isNotEmpty() && numOfWordsEntered == Hiya.STORY_INPUT_LIMIT
            }
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        setupObservers()
    }

    private fun setupObservers() {
        // listen for when title has changed
        sharedViewModel.storyTitle.observe(viewLifecycleOwner, Observer { newTitle ->
            (requireActivity() as AppCompatActivity).supportActionBar?.title = newTitle
        })
        // listen for when a new word has been added
        sharedViewModel.addNewWordStatus.observe(viewLifecycleOwner, Observer { status ->
            when (status) {
                QueryStatus.PENDING -> {}
                QueryStatus.SUCCESS -> {
                    Toast.makeText(requireActivity(), "Successfully added word!", Toast.LENGTH_SHORT).show()
                }
                QueryStatus.ERROR -> {
                    Toast.makeText(requireActivity(), "Failed to add word", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedViewModel.updateTitle(args.storyTitle)
    }

    override fun onResume() {
        // hide bottom navigation bar
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = View.GONE
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        // show navigation bar
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = View.VISIBLE
        sharedViewModel.clearPropertiesForStoryLog()
    }

}