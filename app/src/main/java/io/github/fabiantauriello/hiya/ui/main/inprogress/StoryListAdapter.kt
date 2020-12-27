package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.databinding.StoryListItemBinding
import io.github.fabiantauriello.hiya.domain.Story
import io.github.fabiantauriello.hiya.util.Utils
import io.github.fabiantauriello.hiya.viewmodels.InProgressSharedViewModel

class StoryListAdapter(
    private val stories: ArrayList<Story>,
    private val viewModel: InProgressSharedViewModel,
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
        // set data binding variables
        holder.binding.story = stories[position]

        // set image
        val coAuthorProfileImageUri = Utils.getCoAuthorForStory(stories[position]).profileImageUri
        Log.d(TAG, "onBindViewHolder: $coAuthorProfileImageUri")
        val options: RequestOptions = RequestOptions()
//            .override(450, 600)
            .error(R.drawable.ic_broken_image)

        Glide.with(holder.binding.ivStoryPicture.context)
            .load(coAuthorProfileImageUri)
            .apply(options)
            .into(holder.binding.ivStoryPicture)

        // set click listener
        holder.binding.layoutChatRoomItem.setOnClickListener {
            listener.onStoryClick(stories[position])
        }
    }

    override fun getItemCount() = stories.size

    fun update(newStories: ArrayList<Story>) {
        stories.clear()
        stories.addAll(newStories)
    }

    fun getStories(): ArrayList<Story> {
        return stories
    }


}