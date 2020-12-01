package io.github.fabiantauriello.hiya.util

import java.util.*

object Utils {
    fun formatTimestamp(timestamp: String): String {
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp.toLong()

        val mYear: Int = calendar.get(Calendar.YEAR)
        val mMonth: Int = calendar.get(Calendar.MONTH)
        val mDay: Int = calendar.get(Calendar.DAY_OF_MONTH)

        return "$mDay/$mMonth/$mYear"
    }
}