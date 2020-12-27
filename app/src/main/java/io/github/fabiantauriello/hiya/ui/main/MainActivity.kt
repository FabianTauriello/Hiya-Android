package io.github.fabiantauriello.hiya.ui.main

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.databinding.ActivityMainBinding
import io.github.fabiantauriello.hiya.viewmodels.InProgressSharedViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = this::class.java.name

    private lateinit var binding: ActivityMainBinding

    private val sharedViewModel: InProgressSharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate() called")
        super.onCreate(savedInstanceState)

        // initialize binding and set layout
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // set global variables user id and username for easy access to user node in Firestore
        val sharedPreferences = getSharedPreferences(Hiya.SHARED_PREFS, MODE_PRIVATE)
        Hiya.userId = sharedPreferences.getString(Hiya.SHARED_PREFS_USER_ID, "") ?: ""
        Hiya.username = sharedPreferences.getString(Hiya.SHARED_PREFS_USERNAME, "") ?: ""
        Hiya.profileImageUri = sharedPreferences.getString(Hiya.SHARED_PREFS_PROFILE_IMAGE_URI, "") ?: ""

        val navController = findNavController(R.id.nav_host_fragment)

        // initialize action bar with my toolbar
        setSupportActionBar(toolbar)

        // configure action bar
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.storyListFragment,
            R.id.completedStoriesFragment,
            R.id.settingsFragment
        ).build()
        setupActionBarWithNavController(navController, appBarConfiguration)

        // configure bottom nav
        binding.bottomNavigation.setupWithNavController(navController)

        // change action bar title based on story title or just use default label one from fragment
        sharedViewModel.storyTitle.observe(this, Observer {
            Log.d(TAG, "new title: $it")
            if (it.isNotEmpty()) {
                supportActionBar?.title = it
            }
        })
    }



    override fun onSupportNavigateUp(): Boolean {
        Log.d(TAG, "onSupportNavigateUp: up pressed")

        // hide keyboard if shown
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        return findNavController(R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp()
    }



}