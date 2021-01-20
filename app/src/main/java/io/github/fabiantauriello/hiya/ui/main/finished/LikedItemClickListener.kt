package io.github.fabiantauriello.hiya.ui.main.finished

import io.github.fabiantauriello.hiya.domain.Author

interface LikedItemClickListener {
    fun onToggleLike(storyId: String, author: Author, liked: Boolean)
}