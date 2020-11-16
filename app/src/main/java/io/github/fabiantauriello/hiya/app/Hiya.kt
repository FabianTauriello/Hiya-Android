package io.github.fabiantauriello.hiya.app

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Hiya: Application() {

    companion object {
        var userId = ""
    }
}