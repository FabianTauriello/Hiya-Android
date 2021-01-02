package io.github.fabiantauriello.hiya.app

import android.app.Application
import android.content.Context
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Hiya : Application() {

    init {
        instance = this
    }

    companion object {
        const val SHARED_PREFS = "sharedPrefs"
        const val SHARED_PREFS_USERNAME = "username"
        const val SHARED_PREFS_USER_ID = "userId"
        const val SHARED_PREFS_PROFILE_IMAGE_URI = "profileImageUri"
        const val CONTACTS_PERMISSION_REQUEST_CODE = 1
        const val STORY_INPUT_LIMIT = 1

        // TODO consider rethinking how these values are stored. Should they be nullable instead??
        var userId = ""
        var name = ""
        var profileImageUri = ""

        private var instance: Hiya? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

}