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

    private val TAG = this::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate() called")
        super.onCreate(savedInstanceState)

        // set global variables user id and username for easy access to user node in Firestore
        val sharedPreferences = getSharedPreferences(Hiya.SHARED_PREFS, MODE_PRIVATE)
        Hiya.userId = sharedPreferences.getString(Hiya.SHARED_PREFS_USER_ID, "") ?: ""
        Hiya.username = sharedPreferences.getString(Hiya.SHARED_PREFS_USERNAME, "") ?: ""

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