package io.github.fabiantauriello.hiya.ui.main

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.databinding.ActivityMainBinding
import io.github.fabiantauriello.hiya.viewmodels.StoryListViewModel
import io.github.fabiantauriello.hiya.viewmodels.UserListViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = this::class.java.name

    private lateinit var binding: ActivityMainBinding

    // activity ViewModels for storing story list and user list for lifecycle of activity
    private val storyListViewModel: StoryListViewModel by viewModels()
    private val userListViewModel: UserListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize binding and set layout
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // set global variables user id and username for easy access to user node in Firestore
        val sharedPreferences = getSharedPreferences(Hiya.SHARED_PREFS, MODE_PRIVATE)
        Hiya.userId = sharedPreferences.getString(Hiya.SHARED_PREFS_USER_ID, "") ?: ""
        Hiya.name = sharedPreferences.getString(Hiya.SHARED_PREFS_USERNAME, "") ?: ""
        Hiya.profileImageUri = sharedPreferences.getString(Hiya.SHARED_PREFS_PROFILE_IMAGE_URI, "") ?: ""

        val navController = findNavController(R.id.navHostFragment)

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
        binding.bottomNav.setupWithNavController(navController)

//        // change action bar title based on story title or just use default label one from fragment
//        sharedViewModel.storyLogTitle.observe(this, Observer {
//            if (it.isNotEmpty()) {
//                supportActionBar?.title = it
//            } else {
//                Log.d(TAG, "onCreate: ${navController.currentDestination?.label}")
//                supportActionBar?.title = navController.currentDestination?.label
//            }
//        })


        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.fullScreenStoryFragment -> {
                    hideNavBar()
                    hideToolbar()
                }
                R.id.editStoryDetailsFragment -> {
                    hideNavBar()
                    showToolbar()
                }
                R.id.storyLogFragment ->  {
                    hideNavBar()
                }
                else -> {
                    showNavBar()
                    showToolbar()
                }
            }
        }

    }

    private fun showNavBar() {
        binding.bottomNav.visibility = View.VISIBLE
    }

    private fun hideNavBar() {
        binding.bottomNav.visibility = View.GONE
    }

    private fun showToolbar() {
        binding.toolbar.visibility = View.VISIBLE
    }

    private fun hideToolbar() {
        binding.toolbar.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.navHostFragment).navigateUp() || super.onSupportNavigateUp()
    }



}