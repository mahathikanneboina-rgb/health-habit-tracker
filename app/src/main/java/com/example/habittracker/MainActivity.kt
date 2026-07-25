package com.example.habittracker

import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.habittracker.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var habitCheckBoxes: List<CheckBox>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        habitCheckBoxes = listOf(
            binding.cbHabit1,
            binding.cbHabit2,
            binding.cbHabit3,
            binding.cbHabit4
        )

        displayTodayDate()
        setupHabitsChecklist()
        setupBottomNavigation()
        setupFab()
        updateProgress()
    }

    private fun displayTodayDate() {
        val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        binding.tvTodayDate.text = dateFormat.format(Date())
    }

    private fun setupHabitsChecklist() {
        habitCheckBoxes.forEach { checkBox ->
            checkBox.setOnCheckedChangeListener { _, _ ->
                updateProgress()
            }
        }
    }

    private fun updateProgress() {
        val totalHabits = habitCheckBoxes.size
        if (totalHabits == 0) return

        val completedHabits = habitCheckBoxes.count { it.isChecked }
        val percentage = (completedHabits * 100) / totalHabits

        binding.pbHabits.progress = percentage
        binding.tvProgressPercentage.text = "$percentage%"
        binding.tvProgressStats.text = "$completedHabits of $totalHabits habits completed"
    }

    private fun setupBottomNavigation() {
        binding.bnvMain.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, getString(R.string.tab_selected_format, getString(R.string.nav_home)), Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_reports -> {
                    Toast.makeText(this, getString(R.string.tab_selected_format, getString(R.string.nav_reports)), Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    Toast.makeText(this, getString(R.string.tab_selected_format, getString(R.string.nav_profile)), Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, getString(R.string.tab_selected_format, getString(R.string.nav_settings)), Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupFab() {
        binding.fabAddHabit.setOnClickListener {
            Toast.makeText(this, getString(R.string.add_habit_clicked), Toast.LENGTH_SHORT).show()
        }
    }
}