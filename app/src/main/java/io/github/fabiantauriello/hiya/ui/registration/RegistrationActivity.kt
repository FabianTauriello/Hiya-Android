package io.github.fabiantauriello.hiya.ui.registration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.domain.User
import io.github.fabiantauriello.hiya.ui.main.MainActivity

class RegistrationActivity : AppCompatActivity() {

    private val LOG_TAG = this::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_verification)

        checkIfUserIsAlreadySignedIn()
    }

    private fun checkIfUserIsAlreadySignedIn() {
        // Check if user is signed in (non-null)
        val currentUser = Firebase.auth.currentUser
        if(currentUser != null) {
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        // Prepare and launch MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        this.finish()
    }

}