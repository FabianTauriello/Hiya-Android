package io.github.fabiantauriello.hiya.ui.main.finished

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.databinding.FinishedListItemBinding
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.ui.main.inprogress.StoryListItemClickListener
import io.github.fabiantauriello.hiya.util.Utils

class FinishedStoriesAdapter(
    private var stories: ArrayList<Story>,
    private val listener: StoryListItemClickListener
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
        val story = stories[position]
        val coAuthor = Utils.getCoAuthorFromStory(stories[position])

        // set list item properties
        holder.binding.finishedListItemTitle.text = story.title
        holder.binding.finishedListItemAuthor.text = coAuthor.name
        holder.binding.finishedListItemStorySnippet.text = story.text
        holder.binding.finishedListItemWordCount.text = "${story.wordCount}" + if (story.wordCount == 1) " word" else " words"

        // set image
        val options: RequestOptions = RequestOptions()
//            .override(450, 600)
            .error(R.drawable.ic_profile_filled)
        Glide.with(holder.binding.finishedListItemProfilePic.context)
            .load(coAuthor.profilePic)
            .apply(options)
            .into(holder.binding.finishedListItemProfilePic)

        // set click listener
        holder.binding.finishedListItemContainer.setOnClickListener {
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