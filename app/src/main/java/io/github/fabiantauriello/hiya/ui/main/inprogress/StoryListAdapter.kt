package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.databinding.StoryListItemBinding
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.domain.User
import io.github.fabiantauriello.hiya.util.Utils
import io.github.fabiantauriello.hiya.viewmodels.StoryListViewModel

class StoryListAdapter(
    private val stories: ArrayList<Pair<User, Story>>,
    private val viewModel: StoryListViewModel,
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
        val user = stories[position].first
        val story = stories[position].second

        // set list item properties
        holder.binding.tvStoryTitle.text = user.name
        holder.binding.tvStorySnippet.text = story.text
        holder.binding.tvLastUpdateTimestamp.text = Utils.formatTimestampToTime(story.lastUpdateTimestamp)
        holder.binding.tvWordCount.text = "${story.wordCount}" + if (story.wordCount == 1) " word" else " words"

        // set image
        val options: RequestOptions = RequestOptions()
//            .override(450, 600)
            .error(R.drawable.ic_profile)
        Glide.with(holder.binding.ivStoryPicture.context)
            .load(user.profileImageUri)
            .apply(options)
            .into(holder.binding.ivStoryPicture)

        // set click listener
        holder.binding.layoutChatRoomItem.setOnClickListener {
            listener.onStoryClick(story)
        }
    }

    override fun getItemCount() = stories.size

    fun addItem(pair: Pair<User, Story>) {
        stories.add(pair)
        notifyDataSetChanged()
    }

    fun clearList() {
        stories.clear()
        notifyDataSetChanged()
    }

    fun getStories(): ArrayList<Pair<User, Story>> {
        return stories
    }


}