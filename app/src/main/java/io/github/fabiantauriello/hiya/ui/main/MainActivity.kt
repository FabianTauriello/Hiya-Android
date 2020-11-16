package io.github.fabiantauriello.hiya.ui.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val LOG_TAG = this::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "onCreate() called")
        super.onCreate(savedInstanceState)

        // set global variable user id for easy access to user node in Firestore
        Hiya.userId = FirebaseAuth.getInstance().uid!!

        // initialize binding
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(
                this,
                R.layout.activity_main
            )

        // get nav controller and set up navigation
        val navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNavigation.setupWithNavController(navController)
    }


}