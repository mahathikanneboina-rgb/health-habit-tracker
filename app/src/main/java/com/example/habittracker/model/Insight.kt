package com.example.habittracker.model

/**
 * Simple data class representing a health insight message generated from habit data.
 */
data class Insight(
    val message: String,
    val totalHabits: Int,
    val completedHabits: Int,
    val completionPercentage: Int,
    val currentStreak: Int,
    val weeklyProgress: String
)
