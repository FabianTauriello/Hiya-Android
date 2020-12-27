package io.github.fabiantauriello.hiya.ui.main.completed

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.fabiantauriello.hiya.R

class CompletedStoriesFragment : Fragment() {

    private val TAG = this::class.java.name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(TAG, "onCreateView: called")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_completed_stories, container, false)
    }

}