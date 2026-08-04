package com.example.habittracker.util

import android.content.Context
import androidx.work.*
import com.example.habittracker.data.Habit
import java.util.concurrent.TimeUnit
import com.example.habittracker.work.ReminderWorker

/**
 * Utility object for scheduling and cancelling habit reminder work via WorkManager.
 */
object WorkManagerUtil {

    private const val WORK_NAME_PREFIX = "habit_reminder_"

    /**
     * Schedule a reminder for the given habit.
     * The reminder time is taken from the habit.reminderTime string (e.g., "08:00 AM").
     * If the time is in the past, the reminder will fire the next day.
     */
    fun scheduleReminder(context: Context, habit: Habit) {
        // Convert habit.reminderTime (HH:mm a) to epoch millis.
        val timeMillis = parseReminderTimeToMillis(habit.reminderTime)
        val delay = timeMillis - System.currentTimeMillis()
        val initialDelay = if (delay > 0) delay else TimeUnit.DAYS.toMillis(1) + delay

        val data = workDataOf(
            "habitId" to habit.id,
            "habitName" to habit.name
        )

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(WORK_NAME_PREFIX + habit.id)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(WORK_NAME_PREFIX + habit.id, ExistingWorkPolicy.REPLACE, workRequest)
    }

    /**
     * Cancel any scheduled reminder for the habit with the given id.
     */
    fun cancelReminder(context: Context, habitId: String) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_PREFIX + habitId)
    }

    /**
     * Helper to parse a time string like "08:00 AM" into epoch milliseconds for today.
     */
    private fun parseReminderTimeToMillis(timeString: String): Long {
        try {
            val formatter = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
            val parsedDate = formatter.parse(timeString) ?: return System.currentTimeMillis()
            
            val today = java.util.Calendar.getInstance()
            val calendar = java.util.Calendar.getInstance().apply {
                time = parsedDate
                set(java.util.Calendar.YEAR, today.get(java.util.Calendar.YEAR))
                set(java.util.Calendar.MONTH, today.get(java.util.Calendar.MONTH))
                set(java.util.Calendar.DAY_OF_MONTH, today.get(java.util.Calendar.DAY_OF_MONTH))
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            return calendar.timeInMillis
        } catch (e: Exception) {
            return System.currentTimeMillis()
        }
    }
}
