package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.github.fabiantauriello.hiya.R
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
        holder.binding.story = stories[position]
        holder.binding.coAuthor = Utils.getCoAuthorFromStory(stories[position])

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