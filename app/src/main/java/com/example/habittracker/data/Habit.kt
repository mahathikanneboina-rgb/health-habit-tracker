package com.example.habittracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Represents a Habit entity in the Room Database.
 * This class serves as the unified model for both the database and the presentation layer,
 * replacing the previous temporary model to avoid data representation mismatches.
 */
@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey 
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String, // "Water", "Exercise", "Sleep", "Study", "Meditation", "Custom"
    val dailyGoal: String, // e.g., "2.5 Liters", "30 Minutes"
    val reminderTime: String, // e.g., "08:00 AM"
    val notes: String = "",
    val isCompleted: Boolean = false
)
