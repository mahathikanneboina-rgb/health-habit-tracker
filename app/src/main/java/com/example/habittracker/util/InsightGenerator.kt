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
            return Insight(
                message = "No habits yet. Start adding healthy routines!",
                totalHabits = 0,
                completedHabits = 0,
                completionPercentage = 0,
                currentStreak = 0,
                weeklyProgress = "No progress tracked yet."
            )
        }

        val totalHabits = habits.size
        val completedHabits = habits.count { it.isCompleted }
        val completionPercentage = (completedHabits * 100) / totalHabits

        // Rule-based current streak calculation (simulated from completion rate)
        val currentStreak = when {
            completionPercentage > 80 -> 5
            completionPercentage >= 50 -> 2
            completedHabits > 0 -> 1
            else -> 0
        }

        // Rule-based weekly progress description
        val weeklyProgress = when {
            completionPercentage > 80 -> "Excellent! On track for 85%+ weekly completion."
            completionPercentage >= 50 -> "On track for 60% weekly completion. Try to do slightly more."
            else -> "On track for under 40% weekly completion. Try setting reminders."
        }

        // Generate personalized insight message based on completion rate
        val message = when {
            completionPercentage > 80 -> "Excellent progress! Keep maintaining your healthy routine."
            completionPercentage >= 50 -> "Good progress! Try completing more habits consistently."
            else -> "Start with small improvements and build your daily routine."
        }

        return Insight(
            message = message,
            totalHabits = totalHabits,
            completedHabits = completedHabits,
            completionPercentage = completionPercentage,
            currentStreak = currentStreak,
            weeklyProgress = weeklyProgress
        )
    }
}
