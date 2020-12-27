package io.github.fabiantauriello.hiya.ui.main

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.domain.Story

@BindingAdapter("chatRoomVisibility")
fun setChatRoomVisibility(container: ConstraintLayout, story: Story) {
    // hide chat room if it doesn't contain any messages (I only need to check last message)
//    if (story.lastMessage == "null") { // TODO
//        container.visibility = View.GONE
//    }
}