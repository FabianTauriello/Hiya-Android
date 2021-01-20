package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.databinding.FragmentEditStoryDetailsBinding
import io.github.fabiantauriello.hiya.viewmodels.StoryLogViewModel


class EditStoryDetailsFragment : Fragment() {

    private val TAG = this::class.java.name

    private lateinit var binding: FragmentEditStoryDetailsBinding

    private val viewModel: StoryLogViewModel by navGraphViewModels(R.id.storyLogNestedGraph)

    private lateinit var tagAdapter: ArrayAdapter<String>

    private val tags = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tagAdapter = ArrayAdapter<String>(requireContext(), R.layout.tag_list_item, resources.getStringArray(R.array.story_tags))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditStoryDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // SETUP

        viewModel.story.value?.data?.tags?.forEach { addChipToGroup(it) }
        binding.editDetailsEtTitle.setText(viewModel.story.value?.data?.title)
        binding.editDetailsEtTags.setAdapter(tagAdapter)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel

        // VIEW LISTENERS

        binding.editDetailsEtTags.setOnItemClickListener { parent, _, position, _ ->
            binding.editDetailsEtTags.text = null
            val selectedItem = parent.getItemAtPosition(position) as String
            addChipToGroup(selectedItem)
        }
    }

    private fun addChipToGroup(newTag: String) {
        // create a new chip if it hasn't already been added
        if (!tags.contains(newTag)) {
            tags.add(newTag)
            val chip = Chip(context)
            chip.text = newTag
            chip.isCloseIconVisible = true
            chip.setEnsureMinTouchTargetSize(false)
            binding.editDetailsCgTags.addView(chip as View)

            chip.setOnCloseIconClickListener {
                tags.remove(newTag)
                binding.editDetailsCgTags.removeView(chip as View)
            }
        }
    }


    override fun onStop() {
        val title = binding.editDetailsEtTitle.text.toString()
        viewModel.updateTitle(title)
        viewModel.updateTags(tags)
        super.onStop()
    }




}