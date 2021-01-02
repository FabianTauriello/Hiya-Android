package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.github.fabiantauriello.hiya.databinding.EditStoryTitleDialogBinding
import io.github.fabiantauriello.hiya.viewmodels.InProgressSharedViewModel


class EditStoryTitleDialog : DialogFragment() {

    private val TAG = this::class.java.name

    private val args: EditStoryTitleDialogArgs by navArgs()

    private lateinit var binding: EditStoryTitleDialogBinding

    private val sharedViewModel: InProgressSharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // prevent user from cancelling this dialog by tapping outside of view
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = EditStoryTitleDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.etTitle.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable?) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // don't let user enter an empty title
                    binding.btnDone.isEnabled = !s?.trim().isNullOrEmpty()
                }
            }
        )
        binding.btnDone.setOnClickListener {
            // create a new story
            sharedViewModel.createNewStory(args.coAuthor, binding.etTitle.text.trim().toString())
            dismiss()
        }
        binding.btnCancel.setOnClickListener {
            // go back to story list
            findNavController().navigate(EditStoryTitleDialogDirections.actionEditStoryTitleDialogToStoryListFragment())
        }
    }

}