package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.chip.Chip
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.bindImg
import io.github.fabiantauriello.hiya.databinding.InProgressListItemBinding
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.util.Utils

class InProgressStoriesAdapter(
    private var stories: ArrayList<Story>,
    private val listener: StoryListItemClickListener
) : RecyclerView.Adapter<InProgressStoriesAdapter.StoryItemViewHolder>() {

    private val TAG = this::class.java.name

    class StoryItemViewHolder(
        val binding: InProgressListItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryItemViewHolder {
        val binding = DataBindingUtil.inflate<InProgressListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.in_progress_list_item,
            parent,
            false
        )
        return StoryItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryItemViewHolder, position: Int) {
        // TODO THIS IS CALLED TWICE AFTER RETURNING TO INPROGRESS LIST FROM LOG
        holder.binding.story = stories[position]
        holder.binding.coAuthor = Utils.getCoAuthorFromStory(stories[position])

        holder.binding.inProItemChipGrp.removeAllViews()
        stories[position].tags.forEach { tag ->
            val chip = Chip(holder.binding.inProItemChipGrp.context).apply {
                text = tag
                isCloseIconVisible = false
                setEnsureMinTouchTargetSize(false)
                chipMinHeight = 24f
            }
            holder.binding.inProItemChipGrp.addView(chip as View)
        }

        // set click listener
        holder.binding.inProItemContainer.setOnClickListener {
            listener.onStoryClick(stories[position])
        }
    }

    override fun getItemCount() = stories.size

    fun updateList(newList: ArrayList<Story>?) {
        stories.clear()
        if (newList != null) {
            stories = newList
        }
        notifyDataSetChanged()
    }
}