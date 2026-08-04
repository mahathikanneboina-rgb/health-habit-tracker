package com.example.habittracker.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.UUID

/**
 * Room Database class for the Habit Tracker application.
 * Manages the SQLite database, entity mapping, and versioning.
 */
@Database(entities = [Habit::class], version = 1, exportSchema = false)
abstract class HabitDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao

    companion object {
        private const val TAG = "HabitDatabase"

        @Volatile
        private var INSTANCE: HabitDatabase? = null

        /**
         * Returns the database singleton instance.
         * Uses a callback that seeds default habits directly via SQL when the DB is first created.
         */
        fun getDatabase(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habit_database"
                )
                .addCallback(SeedDatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Database callback that seeds default habits using raw SQL via the
     * SupportSQLiteDatabase reference provided by Room's onCreate.
     * This approach is more reliable than using the DAO inside the callback,
     * because the INSTANCE singleton may not be assigned yet when onCreate fires.
     */
    private class SeedDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d(TAG, "onCreate: Seeding default habits")

            // Helper to insert one default habit via ContentValues
            fun insertSeed(name: String, category: String, goal: String, time: String) {
                val values = ContentValues().apply {
                    put("id", UUID.randomUUID().toString())
                    put("name", name)
                    put("category", category)
                    put("dailyGoal", goal)
                    put("reminderTime", time)
                    put("notes", "")
                    put("reminderEnabled", 0) // false
                    put("isCompleted", 0) // false
                }
                db.insert("habits", SQLiteDatabase.CONFLICT_REPLACE, values)
            }

            insertSeed("Drink 2.5L Water",         "Water",    "2.5 Liters",  "08:00 AM")
            insertSeed("30 Min Morning Workout",    "Exercise", "30 Minutes",  "07:30 AM")
            insertSeed("Read 15 Pages of Book",     "Study",    "15 Pages",    "09:00 PM")
            insertSeed("10 Min Evening Meditation",  "Custom",   "10 Minutes",  "10:00 PM")

            Log.d(TAG, "onCreate: Default habits seeded successfully")
        }
    }
}
