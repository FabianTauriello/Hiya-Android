package io.github.fabiantauriello.hiya.app

import android.app.Application
import android.content.Context

class Hiya : Application() {

    init {
        instance = this
    }

    companion object {
        const val SHARED_PREFS = "sharedPrefs"
        const val SHARED_PREFS_USERNAME = "username"
        const val SHARED_PREFS_USER_ID = "userId"
        const val CONTACTS_PERMISSION_REQUEST_CODE = 1

        // TODO consider rethinking how these values are stored. Should they be nullable??
        var userId = ""
        var username = ""

        private var instance: Hiya? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}