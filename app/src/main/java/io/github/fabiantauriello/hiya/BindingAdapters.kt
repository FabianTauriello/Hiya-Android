package io.github.fabiantauriello.hiya

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.github.fabiantauriello.hiya.domain.QueryStatus
import io.github.fabiantauriello.hiya.domain.StoriesResponse
import io.github.fabiantauriello.hiya.domain.Story

@BindingAdapter("storySnippet")
fun bindStorySnippet(tv: TextView, story: Story) {
    if (story.text.isEmpty()) {
        tv.text = "Not yet started"
    } else {
        tv.text = story.text
    }
}

@BindingAdapter("wordCount")
fun bindWordCount(tv: TextView, story: Story) {
    tv.text = story.wordCount.toString() + if (story.wordCount == 1) {
        " word"
    } else {
        " words"
    }
}

@BindingAdapter("img")
fun bindImg(iv: ImageView, profilePic: String) {
    val options: RequestOptions = RequestOptions()
//            .override(450, 600)
        .error(R.drawable.ic_profile_filled)
    Glide.with(iv.context)
        .load(profilePic)
        .circleCrop()
        .apply(options)
        .into(iv)
}

@BindingAdapter("pbVisibility")
fun toggleProgressBar(pb: ProgressBar, liveData: LiveData<StoriesResponse>) {
    pb.visibility = if (liveData.value?.queryStatus == QueryStatus.LOADING) View.VISIBLE else View.GONE
}

@BindingAdapter("emptyListMessageVisibility")
fun toggleEmptyDataMessage(tv: TextView, liveData: LiveData<StoriesResponse>) {
    if (liveData.value?.queryStatus == QueryStatus.SUCCESS && liveData.value?.data?.isEmpty()!!) {
        tv.visibility = View.VISIBLE
    } else {
        tv.visibility = View.GONE
    }
}


// TODO ADD THIS LOGIC ABOVE TO FINISHED AND LIKED LIST FOR PROGRESS BAR
