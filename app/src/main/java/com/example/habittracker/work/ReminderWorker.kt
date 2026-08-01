package com.example.habittracker.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result
import androidx.work.WorkerParameters
import com.example.habittracker.util.NotificationHelper

/**
 * Worker that runs at the scheduled reminder time and displays a notification for the habit.
 */
class ReminderWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Retrieve habit information from input data.
        val habitName = inputData.getString("habitName") ?: return Result.failure()
        // Show the reminder notification.
        NotificationHelper.showHabitReminder(applicationContext, habitName)
        return Result.success()
    }
}
