package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.databinding.FragmentEditStoryDetailsBinding
import io.github.fabiantauriello.hiya.viewmodels.StoryLogViewModel


class EditStoryDetailsFragment : Fragment() {

    private val TAG = this::class.java.name

    private lateinit var binding: FragmentEditStoryDetailsBinding

    private val viewModel: StoryLogViewModel by navGraphViewModels(R.id.storyLogNestedGraph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditStoryDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // SETUP

        binding.fragmentEditStoryDetailsEtTitle.setText(viewModel.story.value?.data?.title.toString())

        // VIEW LISTENERS

        binding.fragmentEditStoryDetailsEtTitle.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable?) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // don't let user enter an empty title
                    binding.fragmentEditStoryDetailsBtnSave.isEnabled = !s?.trim().isNullOrEmpty()
                }
            }
        )
        binding.fragmentEditStoryDetailsBtnSave.setOnClickListener {
            val title = binding.fragmentEditStoryDetailsEtTitle.text.toString()
            val updates = mapOf<String, Any>(
                "title" to title
            )
            viewModel.updateStory(updates)
        }
    }

}