package io.github.fabiantauriello.hiya.ui.main.inprogress

import io.github.fabiantauriello.hiya.domain.Author
import io.github.fabiantauriello.hiya.domain.Story

interface StoryListItemClickListener {
    fun onStoryClick(story: Story)
}