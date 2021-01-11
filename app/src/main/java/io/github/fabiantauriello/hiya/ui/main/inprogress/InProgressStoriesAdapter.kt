package io.github.fabiantauriello.hiya.ui.main.inprogress

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
        val story = stories[position]
        val coAuthor = Utils.getCoAuthorFromStory(stories[position])

        // set list item properties
        holder.binding.tvStoryTitle.text = coAuthor.name
        holder.binding.tvStorySnippet.text = story.text
        holder.binding.tvLastUpdateTimestamp.text = Utils.formatTimestampToTime(story.lastUpdateTimestamp)
        holder.binding.tvWordCount.text = "${story.wordCount}" + if (story.wordCount == 1) " word" else " words"

        // set image
        val options: RequestOptions = RequestOptions()
//            .override(450, 600)
            .error(R.drawable.ic_profile_filled)
        Glide.with(holder.binding.ivStoryPicture.context)
            .load(coAuthor.profilePic)
            .apply(options)
            .into(holder.binding.ivStoryPicture)

        // set click listener
        holder.binding.layoutStoryItem.setOnClickListener {
            listener.onStoryClick(story)
        }
    }

    override fun getItemCount() = stories.size

    fun updateList(newList: ArrayList<Story>) {
        stories.clear()
        stories = newList
        notifyDataSetChanged()
    }
}