package com.example.habittracker.model

import java.util.UUID

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var category: String, // "Water", "Exercise", "Sleep", "Study", "Custom"
    var dailyGoal: String, // e.g. "2.5 Liters", "30 Minutes"
    var reminderTime: String, // e.g. "08:00 AM"
    var isCompleted: Boolean = false
)
