package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.material.transition.MaterialContainerTransform
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.databinding.FragmentStoryLogBinding
import io.github.fabiantauriello.hiya.util.Utils
import io.github.fabiantauriello.hiya.viewmodels.StoryLogViewModel


// individual chat between users
class StoryLogFragment : Fragment() {

    private val TAG = this::class.java.name

    private val args: StoryLogFragmentArgs by navArgs()

    private lateinit var binding: FragmentStoryLogBinding

    private val viewModel: StoryLogViewModel by navGraphViewModels(R.id.storyLogNestedGraph)

    private var isDone = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    // returning to fragment picks up here
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoryLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // SETUP

        // Detect if a new story is required
        if (args.story == null) {
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

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel

        // VIEW LISTENERS

        binding.logFabAddText.setOnClickListener {
            // hide keyboard
            val imm = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)

            val newText = binding.logEtStoryText.text.toString().trim()
            viewModel.updateText(newText)
            binding.logFabAddText.isEnabled = false
        }
        binding.logEtStoryText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                val storyText = viewModel.story.value?.data?.text ?: ""

                editable?.let {
                    val newText = editable.subSequence(storyText.length, editable.length).toString()

                    // check if user is trying to delete pre-existing characters
                    if (!editable.startsWith(storyText)) {
                        // reset text depending on if no characters have been entered
                        if (editable.length <= storyText.length) {          // TODO || sequence.substring(0, storyText.length - 1) == storyText
                            // no characters have been added and user has attempted to delete pre-existing characters
                            // reset editable to pre-existing text only
                            binding.logEtStoryText.setText(storyText)
                        } else {
                            // characters have been added and user has attempted to delete pre-existing characters
                            // reset editable to pre-existing text plus any new text entered
//                            val newText = editable.subSequence(storyText.length - 1, editable.length)
                            binding.logEtStoryText.setText(storyText + newText)
                        }
                        // move cursor to end
                        binding.logEtStoryText.setSelection(editable.count())
                    }
                    // enable button if the user has entered a new sentence
                    if (editable.length > storyText.length) {
                        val trimmedText = editable.trim()
                        val lastChar = trimmedText[trimmedText.length - 1]
                        binding.logFabAddText.isEnabled = lastChar == '.' || lastChar == '?' || lastChar == '!'
                    }
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
            menu.findItem(R.id.action_done).setIcon(R.drawable.ic_done_outline)
        }

        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_fullscreen -> {
                findNavController().navigate(StoryLogFragmentDirections.actionStoryLogFragmentToFullScreenStoryFragment())
                true
            }
            R.id.action_done -> {
                Log.d(TAG, "onOptionsItemSelected: done clicked")
                isDone = if (isDone) {
                    viewModel.removeAuthorFromDoneList()
                    item.setIcon(R.drawable.ic_done_outline)
                    false
                } else {
                    viewModel.addAuthorToDoneList()
                    item.setIcon(R.drawable.ic_done_filled)
                    true
                }
                true
            }
            R.id.action_settings -> {
                findNavController().navigate(StoryLogFragmentDirections.actionStoryLogFragmentToEditStoryDetailsFragment())
                viewModel.removeAuthorFromDoneList()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        super.onStop()

        // Hide keyboard if shown
        val imm = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
    }


}