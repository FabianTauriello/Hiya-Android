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

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: ")

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: ")

        binding = FragmentStoryLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: ")

        if (args.storyId.isEmpty()) {
            // new story
            findNavController().navigate(StoryLogFragmentDirections.actionStoryLogFragmentToEditStoryTitleDialog(args.coAuthor))
        } else {
            // existing story
            sharedViewModel.storyId = args.storyId
            sharedViewModel.configureMessagesListener()
        }

        configureObservers()

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
    }

    private fun configureObservers() {
        // listen for when title has changed
        sharedViewModel.storyTitle.observe(viewLifecycleOwner, Observer { newTitle ->
            Log.d(TAG, "configureObservers: NEW TITLE... $newTitle")
            (requireActivity() as AppCompatActivity).supportActionBar?.title = newTitle
        })
        // listen for when story text is changed
        sharedViewModel.storyText.observe(viewLifecycleOwner, Observer { newText ->
            Log.d(TAG, "configureObservers: NEW TEXT... $newText")
            binding.tvStory.text = newText
        })
        // listen for when a new word has been added
        sharedViewModel.addNewWordStatus.observe(viewLifecycleOwner, Observer { status ->
            Log.d(TAG, "configureObservers: NEW WORD STATUS... $status")
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
        sharedViewModel.wordCount.observe(viewLifecycleOwner, Observer { wordCount ->
            // show story text view. testing for wordCount == 1 so visibility is only executed once
            if (wordCount == 1 || args.storyId.isNotEmpty()) { // TODO replace "args.storyId.isNotEmpty()" with EDITING STATE ENUM
                binding.tvStory.visibility = View.VISIBLE
            }
        })
        // listen for when a new story has been created
        sharedViewModel.createNewStoryStatus.observe(viewLifecycleOwner, Observer { status ->
            Log.d(TAG, "configureObservers: NEW WORD STORY STATUS... $status")
            if (status == QueryStatus.SUCCESS || args.storyId.isNotEmpty()) {
                binding.pbLoadLog.visibility = View.GONE
                binding.etNewWord.visibility = View.VISIBLE
                binding.btnAddToStory.visibility = View.VISIBLE
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