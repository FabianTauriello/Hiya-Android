package io.github.fabiantauriello.hiya.util

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.domain.Author
import io.github.fabiantauriello.hiya.domain.Story
import java.util.*

object Utils {
    private val TAG = this::class.java.name

    fun formatTimestampToDate(timestamp: String): String {
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp.toLong()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return "$day/$month/$year"
    }

    fun formatTimestampToTime(timestamp: String): String {
        // check if timestamp is null because a chat room can be created without messages
        return if (timestamp == "") {
            ""
        } else {
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp.toLong()

            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            String.format("%02d:%02d", hour, minute)
        }
    }

    fun getCoAuthorForStory(story: Story): Author {
        return story.authors.filter { it.userId != Hiya.userId }[0]
    }

    // debugging only
    fun displayBackStack(tag: String, navController: NavController) {
        val backStack = navController.backStack
        Log.d(tag, "BackStack (count = ${backStack.count()})...")
        for (frag in backStack) {
            Log.d(tag, frag.destination.displayName)
        }
    }

}