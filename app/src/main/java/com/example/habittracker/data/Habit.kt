package com.example.habittracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String, // Water, Exercise, Sleep, Study, Custom
    val dailyGoal: Int, // e.g., number of times per day
    val reminderHour: Int?, // 0-23 optional
    val reminderMinute: Int?, // 0-59 optional
    val completedToday: Int = 0 // count of completions for today
)
