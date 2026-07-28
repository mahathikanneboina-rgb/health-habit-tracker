package com.example.habittracker

import android.app.Activity
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.habittracker.data.HabitRepository
import com.example.habittracker.databinding.ActivityAddHabitBinding
import com.example.habittracker.model.Habit
import java.util.Calendar
import java.util.Locale

class AddHabitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddHabitBinding
    private lateinit var repository: HabitRepository

    private var editingHabitId: String? = null
    private var selectedHour = 8
    private var selectedMinute = 0

    companion object {
        const val EXTRA_HABIT_ID = "extra_habit_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHabitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = HabitRepository(this)

        setupCategoryDropdown()
        setupTimePicker()
        checkEditMode()
        setupListeners()
    }

    private fun setupCategoryDropdown() {
        val categories = arrayOf("Water", "Exercise", "Sleep", "Study", "Custom")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(adapter)
        binding.actvCategory.setText(categories[0], false)
    }

    private fun setupTimePicker() {
        binding.etReminderTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { _, hourOfDay, selectedMin ->
                    selectedHour = hourOfDay
                    selectedMinute = selectedMin

                    val formatTime = formatTime(hourOfDay, selectedMin)
                    binding.etReminderTime.setText(formatTime)
                    binding.tilReminderTime.error = null
                },
                hour,
                minute,
                false
            )
            timePickerDialog.show()
        }
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val amPm = if (hour >= 12) "PM" else "AM"
        val hour12 = if (hour % 12 == 0) 12 else hour % 12
        return String.format(Locale.getDefault(), "%02d:%02d %s", hour12, minute, amPm)
    }

    private fun checkEditMode() {
        editingHabitId = intent.getStringExtra(EXTRA_HABIT_ID)
        editingHabitId?.let { id ->
            val habit = repository.getHabitById(id)
            if (habit != null) {
                binding.tvAddHabitTitle.text = getString(R.string.title_edit_habit)
                binding.btnSaveHabit.text = getString(R.string.btn_update_habit)

                binding.etHabitName.setText(habit.name)
                binding.actvCategory.setText(habit.category, false)
                binding.etDailyGoal.setText(habit.dailyGoal)
                binding.etReminderTime.setText(habit.reminderTime)
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnCancelHabit.setOnClickListener {
            finish()
        }

        binding.etHabitName.doOnTextChanged { _, _, _, _ ->
            binding.tilHabitName.error = null
        }

        binding.etDailyGoal.doOnTextChanged { _, _, _, _ ->
            binding.tilDailyGoal.error = null
        }

        binding.btnSaveHabit.setOnClickListener {
            saveHabit()
        }
    }

    private fun saveHabit() {
        val name = binding.etHabitName.text?.toString()?.trim().orEmpty()
        val category = binding.actvCategory.text?.toString()?.trim().orEmpty().ifEmpty { "Custom" }
        val dailyGoal = binding.etDailyGoal.text?.toString()?.trim().orEmpty()
        val reminderTime = binding.etReminderTime.text?.toString()?.trim().orEmpty().ifEmpty { "08:00 AM" }

        var isValid = true

        if (name.isEmpty()) {
            binding.tilHabitName.error = getString(R.string.error_empty_habit_name)
            isValid = false
        }

        if (dailyGoal.isEmpty()) {
            binding.tilDailyGoal.error = getString(R.string.error_empty_daily_goal)
            isValid = false
        }

        if (!isValid) return

        if (editingHabitId != null) {
            val existingHabit = repository.getHabitById(editingHabitId!!)
            if (existingHabit != null) {
                existingHabit.name = name
                existingHabit.category = category
                existingHabit.dailyGoal = dailyGoal
                existingHabit.reminderTime = reminderTime
                repository.updateHabit(existingHabit)
                Toast.makeText(this, getString(R.string.habit_updated_success), Toast.LENGTH_SHORT).show()
            }
        } else {
            val newHabit = Habit(
                name = name,
                category = category,
                dailyGoal = dailyGoal,
                reminderTime = reminderTime,
                isCompleted = false
            )
            repository.addHabit(newHabit)
            Toast.makeText(this, getString(R.string.habit_saved_success), Toast.LENGTH_SHORT).show()
        }

        setResult(Activity.RESULT_OK)
        finish()
    }
}
