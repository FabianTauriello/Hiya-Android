package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.databinding.FragmentStoryLogBinding
import io.github.fabiantauriello.hiya.domain.QueryStatus
import io.github.fabiantauriello.hiya.util.Utils
import io.github.fabiantauriello.hiya.viewmodels.StoryLogViewModel


// individual chat between users
class StoryLogFragment : Fragment() {

    private val LC_TAG = "LC_LOG"
    private val TAG = this::class.java.name

    private val args: StoryLogFragmentArgs by navArgs()

    private lateinit var binding: FragmentStoryLogBinding

    private val viewModel: StoryLogViewModel by navGraphViewModels(R.id.storyLogNestedGraph)

    private var newStoryRequired = false

    private var isDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enable options menu for actions
        setHasOptionsMenu(true)

        // Detect if a new story is required
        newStoryRequired = args.story == null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoryLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // SETUP

        binding.tvStoryLog.visibility = if (args.story?.text.isNullOrEmpty()) View.GONE else View.VISIBLE

        if (newStoryRequired) {
            // new story required
            viewModel.createNewStory(args.coAuthor!!)
        } else {
            // existing story can be shown
            args.story?.let { story ->
                isDone = Utils.getAuthorFromStory(story, Hiya.userId).done
                viewModel.updateStoryId(story.id)
                viewModel.listenForChangesToStory()
            }
        }

        // VIEW LISTENERS

        binding.btnAddToStory.setOnClickListener {
            val text = binding.etNewWord.text.toString().trim()
            binding.etNewWord.text.clear()
            binding.etNewWord.isEnabled = false
            binding.btnAddToStory.isEnabled = false
            viewModel.addNewWord(text)
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
                binding.btnAddToStory.isEnabled =
                    s.toString().trim().isNotEmpty() && numOfWordsEntered == Hiya.STORY_INPUT_LIMIT
            }

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })
        binding.tvStoryLog.setOnClickListener {
            findNavController().navigate(StoryLogFragmentDirections.actionStoryLogFragmentToFullScreenStoryFragment())
        }

        // OBSERVERS

        // listen for story changes
        viewModel.story.observe(viewLifecycleOwner, { response ->
            Log.d(TAG, "onViewCreated: new text")
            when (response.queryStatus) {
                QueryStatus.SUCCESS -> {
                    binding.etNewWord.isEnabled = response.data?.nextTurn == Hiya.userId
                    binding.tvStoryLog.text = response.data?.text
                    binding.tvStoryLog.visibility = if (response.data?.text.isNullOrEmpty()) View.GONE else View.VISIBLE
                }
                QueryStatus.ERROR -> {}
            }

        })
        // listen for when a new word has been added
        viewModel.addNewWordStatus.observe(viewLifecycleOwner, { response ->
            when (response.queryStatus) {
                QueryStatus.SUCCESS -> {
                    Toast.makeText(requireActivity(), "Successfully added word!", Toast.LENGTH_SHORT).show()
                }
                QueryStatus.ERROR -> {
                    Toast.makeText(requireActivity(), "Failed to add word: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
        // listen for when a new story is created
        viewModel.createNewStoryStatus.observe(viewLifecycleOwner, { response ->
            when (response.queryStatus) {
                QueryStatus.SUCCESS -> {
                }
                QueryStatus.ERROR -> {
                    Toast.makeText(requireActivity(), "Failed to create a new story - ${response.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.story_log_menu, menu)

        // set drawable for done icon
        if (isDone) {
            menu.findItem(R.id.action_done).setIcon(R.drawable.ic_done_filled)
        } else {
            menu.findItem(R.id.action_done).setIcon(R.drawable.ic_done_open)
        }

        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_done -> {
                Log.d(TAG, "onOptionsItemSelected: done clicked")
                isDone = if (isDone) {
                    viewModel.removeAuthorFromDoneList()
                    item.setIcon(R.drawable.ic_done_open)
                    false
                } else {
                    viewModel.addAuthorToDoneList()
                    item.setIcon(R.drawable.ic_done_filled)
                    true
                }
                true
            }
            R.id.action_settings -> {
                Log.d(TAG, "onOptionsItemSelected: settings clicked")
                findNavController().navigate(StoryLogFragmentDirections.actionStoryLogFragmentToEditStoryDetailsFragment())
                viewModel.removeAuthorFromDoneList()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(LC_TAG, "log onAttach: called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(LC_TAG, "log onResume: called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(LC_TAG, "log onDestroy: called $id")

        // Hide keyboard if shown
        val imm = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(LC_TAG, "log onDetach: called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(LC_TAG, "log onStop: called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(LC_TAG, "log onPause: called")
    }




}