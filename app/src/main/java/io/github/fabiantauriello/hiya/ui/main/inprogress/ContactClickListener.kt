package io.github.fabiantauriello.hiya.ui.main.inprogress

import io.github.fabiantauriello.hiya.domain.User

interface ContactClickListener {
    fun onContactClick(contact: User)
}