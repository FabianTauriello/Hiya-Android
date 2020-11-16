package io.github.fabiantauriello.hiya.ui.main

import io.github.fabiantauriello.hiya.domain.Contact

interface ContactClickListener {
    fun onContactClick(contact: Contact)
}