package com.example.habittracker.data

import android.content.Context
import android.content.SharedPreferences
import com.example.habittracker.model.Habit
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class HabitRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "habit_tracker_prefs"
        private const val KEY_HABITS = "habits_json_list"
    }

    init {
        // Seed default habits if empty
        if (!prefs.contains(KEY_HABITS)) {
            val defaultHabits = listOf(
                Habit(name = "Drink 2.5L Water", category = "Water", dailyGoal = "2.5 Liters", reminderTime = "08:00 AM", isCompleted = true),
                Habit(name = "30 Min Morning Workout", category = "Exercise", dailyGoal = "30 Minutes", reminderTime = "07:30 AM", isCompleted = true),
                Habit(name = "Read 15 Pages of Book", category = "Study", dailyGoal = "15 Pages", reminderTime = "09:00 PM", isCompleted = true),
                Habit(name = "10 Min Evening Meditation", category = "Custom", dailyGoal = "10 Minutes", reminderTime = "10:00 PM", isCompleted = false)
            )
            saveAll(defaultHabits)
        }
    }

    fun getAllHabits(): MutableList<Habit> {
        val jsonString = prefs.getString(KEY_HABITS, "[]") ?: "[]"
        val habits = mutableListOf<Habit>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                habits.add(
                    Habit(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        name = obj.optString("name", ""),
                        category = obj.optString("category", "Custom"),
                        dailyGoal = obj.optString("dailyGoal", ""),
                        reminderTime = obj.optString("reminderTime", "08:00 AM"),
                        isCompleted = obj.optBoolean("isCompleted", false)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return habits
    }

    fun getHabitById(id: String): Habit? {
        return getAllHabits().find { it.id == id }
    }

    fun addHabit(habit: Habit) {
        val habits = getAllHabits()
        habits.add(0, habit) // Add to top
        saveAll(habits)
    }

    fun updateHabit(updatedHabit: Habit) {
        val habits = getAllHabits()
        val index = habits.indexOfFirst { it.id == updatedHabit.id }
        if (index != -1) {
            habits[index] = updatedHabit
            saveAll(habits)
        }
    }

    fun deleteHabit(habitId: String) {
        val habits = getAllHabits()
        val removed = habits.removeAll { it.id == habitId }
        if (removed) {
            saveAll(habits)
        }
    }

    fun toggleHabitCompletion(habitId: String): Boolean {
        val habits = getAllHabits()
        val habit = habits.find { it.id == habitId }
        if (habit != null) {
            habit.isCompleted = !habit.isCompleted
            saveAll(habits)
            return habit.isCompleted
        }
        return false
    }

    private fun saveAll(habits: List<Habit>) {
        val jsonArray = JSONArray()
        for (habit in habits) {
            val obj = JSONObject().apply {
                put("id", habit.id)
                put("name", habit.name)
                put("category", habit.category)
                put("dailyGoal", habit.dailyGoal)
                put("reminderTime", habit.reminderTime)
                put("isCompleted", habit.isCompleted)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_HABITS, jsonArray.toString()).apply()
    }
}
