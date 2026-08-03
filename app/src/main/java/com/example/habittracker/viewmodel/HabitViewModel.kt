package com.example.habittracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.example.habittracker.data.Habit
import com.example.habittracker.data.HabitRepository
import com.example.habittracker.model.Insight
import com.example.habittracker.util.InsightGenerator

/**
 * HabitViewModel class that exposes database data using LiveData and
 * executes writes in the background coroutine scope.
 */
class HabitViewModel(private val repository: HabitRepository) : ViewModel() {

    // Converts Flow from repository to LiveData, allowing views to observe updates.
    val allHabits: LiveData<List<Habit>> = repository.allHabits.asLiveData()

    // Insight LiveData generated from habit list using Flow mapping
    val insight: LiveData<Insight> = repository.allHabits.map { habitList ->
        InsightGenerator.generateInsight(habitList)
    }.asLiveData()

    /**
     * Coroutine-bound database insert operation.
     */
    fun insert(habit: Habit) = viewModelScope.launch {
        repository.addHabit(habit)
    }

    /**
     * Coroutine-bound database update operation.
     */
    fun update(habit: Habit) = viewModelScope.launch {
        repository.updateHabit(habit)
    }

    /**
     * Coroutine-bound database delete operation.
     */
    fun delete(id: String) = viewModelScope.launch {
        repository.deleteHabit(id)
    }

    /**
     * Coroutine-bound database operation to toggle habit completion state.
     */
    fun toggleHabitCompletion(id: String) = viewModelScope.launch {
        repository.toggleHabitCompletion(id)
    }

    /**
     * Suspends to fetch a specific habit by its ID.
     */
    suspend fun getHabitById(id: String): Habit? {
        return repository.getHabitById(id)
    }
}

