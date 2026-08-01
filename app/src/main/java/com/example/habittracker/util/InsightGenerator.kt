package com.example.habittracker.util

import com.example.habittracker.data.Habit
import com.example.habittracker.model.Insight

/**
 * Generates a health insight message based on a list of habits.
 * Rule‑based logic:
 *  - If completion >= 80% → excellent progress.
 *  - If completion >= 50% → good progress.
 *  - Otherwise → suggest small improvements.
 */
object InsightGenerator {
    fun generateInsight(habits: List<Habit>): Insight {
        if (habits.isEmpty()) {
            return Insight("No habits yet. Start adding healthy routines!")
        }
        val total = habits.size
        val completed = habits.count { it.isCompleted }
        val percent = (completed * 100) / total
        val message = when {
            percent >= 80 -> "Excellent progress! Keep maintaining your healthy routine."
            percent >= 50 -> "Good progress! Try completing more habits consistently."
            else -> "Start with small improvements and build your daily routine."
        }
        return Insight(message)
    }
}
