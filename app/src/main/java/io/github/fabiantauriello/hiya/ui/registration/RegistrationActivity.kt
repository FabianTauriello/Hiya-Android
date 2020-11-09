package io.github.fabiantauriello.hiya.ui.registration

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.fabiantauriello.hiya.R

class RegistrationActivity : AppCompatActivity() {

    private val LOG_TAG = this::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_verification)
    }

}