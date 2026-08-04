package com.example.habittracker

import android.app.Activity
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.habittracker.data.Habit
import com.example.habittracker.databinding.ActivityAddHabitBinding
import com.example.habittracker.viewmodel.HabitViewModel
import com.example.habittracker.viewmodel.HabitViewModelFactory
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class AddHabitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddHabitBinding

    // Inject ViewModel using ViewModelProvider.Factory and the application singleton repository
    private val viewModel: HabitViewModel by viewModels {
        HabitViewModelFactory((application as HabitApplication).repository)
    }

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

        setupCategoryDropdown()
        setupTimePicker()
        checkEditMode()
        setupListeners()
    }

    private fun setupCategoryDropdown() {
        val categories = arrayOf("Water", "Exercise", "Sleep", "Study", "Meditation", "Custom")
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
            lifecycleScope.launch {
                val habit = viewModel.getHabitById(id)
                if (habit != null) {
                    binding.tvAddHabitTitle.text = getString(R.string.title_edit_habit)
                    binding.btnSaveHabit.text = getString(R.string.btn_update_habit)

                    binding.etHabitName.setText(habit.name)
                    binding.actvCategory.setText(habit.category, false)
                    binding.etDailyGoal.setText(habit.dailyGoal)
                    binding.etReminderTime.setText(habit.reminderTime)
                    binding.etNotes.setText(habit.notes)
                    binding.switchEnableReminder.isChecked = habit.reminderEnabled
                }
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
        val notes = binding.etNotes.text?.toString()?.trim().orEmpty()

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

        // Retrieve reminder enabled state
        val reminderEnabled = binding.switchEnableReminder.isChecked

        if (editingHabitId != null) {
            lifecycleScope.launch {
                val existingHabit = viewModel.getHabitById(editingHabitId!!)
                if (existingHabit != null) {
                    val updatedHabit = existingHabit.copy(
                        name = name,
                        category = category,
                        dailyGoal = dailyGoal,
                        reminderTime = reminderTime,
                        notes = notes,
                        reminderEnabled = reminderEnabled
                    )
                    viewModel.update(updatedHabit)
                    // Schedule or cancel reminder based on toggle
                    if (reminderEnabled) {
                        com.example.habittracker.util.WorkManagerUtil.scheduleReminder(this@AddHabitActivity, updatedHabit)
                    } else {
                        com.example.habittracker.util.WorkManagerUtil.cancelReminder(this@AddHabitActivity, updatedHabit.id)
                    }
                    Toast.makeText(this@AddHabitActivity, getString(R.string.habit_updated_success), Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        } else {
            val newHabit = Habit(
                name = name,
                category = category,
                dailyGoal = dailyGoal,
                reminderTime = reminderTime,
                notes = notes,
                reminderEnabled = reminderEnabled
            )
            viewModel.insert(newHabit)
            // Schedule reminder if enabled
            if (reminderEnabled) {
                com.example.habittracker.util.WorkManagerUtil.scheduleReminder(this, newHabit)
            }
            Toast.makeText(this, getString(R.string.habit_saved_success), Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
