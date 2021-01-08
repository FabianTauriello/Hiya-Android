package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Adapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.databinding.StoryListItemBinding
import io.github.fabiantauriello.hiya.domain.Author
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.domain.User
import io.github.fabiantauriello.hiya.util.Utils

class StoryListAdapter(
    private var stories: ArrayList<Pair<Story, Author>>,
    private val listener: StoryListItemClickListener
) : RecyclerView.Adapter<StoryListAdapter.StoryItemViewHolder>() {

    private val TAG = this::class.java.name

    class StoryItemViewHolder(
        val binding: StoryListItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryItemViewHolder {
        val binding = DataBindingUtil.inflate<StoryListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.story_list_item,
            parent,
            false
        )
        return StoryItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryItemViewHolder, position: Int) {
        val story = stories[position].first
        val author = stories[position].second

        // set list item properties
        holder.binding.tvStoryTitle.text = author.name
        holder.binding.tvStorySnippet.text = story.text
        holder.binding.tvLastUpdateTimestamp.text = Utils.formatTimestampToTime(story.lastUpdateTimestamp)
        holder.binding.tvWordCount.text = "${story.wordCount}" + if (story.wordCount == 1) " word" else " words"

        // set image
        val options: RequestOptions = RequestOptions()
//            .override(450, 600)
            .error(R.drawable.ic_profile)
        Glide.with(holder.binding.ivStoryPicture.context)
            .load(author.picture)
            .apply(options)
            .into(holder.binding.ivStoryPicture)

        // set click listener
        holder.binding.layoutChatRoomItem.setOnClickListener {
            listener.onStoryClick(story)
        }
    }

    override fun getItemCount() = stories.size

    fun clearList() {
        stories.clear()
        notifyDataSetChanged()
    }

    fun updateList(newList: ArrayList<Pair<Story, Author>>) {
        stories.clear()
        stories = newList
        notifyDataSetChanged()
    }

    fun getStories(): ArrayList<Pair<Story, Author>> {
        return stories
    }


}