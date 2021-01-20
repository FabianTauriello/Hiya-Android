package io.github.fabiantauriello.hiya.ui.main.finished

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.databinding.FinishedListItemBinding
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.ui.main.inprogress.StoryListItemClickListener
import io.github.fabiantauriello.hiya.util.Utils

class FinishedStoriesAdapter(
    private var stories: ArrayList<Story>,
    private val listener: StoryListItemClickListener,
    private val likedListener: LikedItemClickListener,
) : RecyclerView.Adapter<FinishedStoriesAdapter.StoryItemViewHolder>() {

    private val TAG = this::class.java.name

    class StoryItemViewHolder(
        val binding: FinishedListItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryItemViewHolder {
        val binding = DataBindingUtil.inflate<FinishedListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.finished_list_item,
            parent,
            false
        )
        return StoryItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryItemViewHolder, position: Int) {
        holder.binding.story = stories[position]
        holder.binding.coAuthor = Utils.getCoAuthorFromStory(stories[position])

        // set click listener for container
        holder.binding.finishedListItemContainer.setOnClickListener {
            listener.onStoryClick(stories[position])
        }
        // set click listener for like toggle
        holder.binding.finishedListItemTglLike.setOnClickListener {
            val author = Utils.getAuthorFromStory(stories[position], Hiya.userId)
            val liked = holder.binding.finishedListItemTglLike.isChecked
            likedListener.onToggleLike(stories[position].id, author, liked)
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