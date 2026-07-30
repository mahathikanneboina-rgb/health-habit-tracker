package com.example.habittracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittracker.adapter.HabitAdapter
import com.example.habittracker.data.HabitRepository
import com.example.habittracker.databinding.ActivityMainBinding
import com.example.habittracker.model.Habit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: HabitRepository
    private lateinit var habitAdapter: HabitAdapter

    private val addHabitLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadHabits()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = HabitRepository(this)

        displayTodayDate()
        setupRecyclerView()
        setupBottomNavigation()
        setupFab()
        loadHabits()
    }

    private fun displayTodayDate() {
        val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        binding.tvTodayDate.text = dateFormat.format(Date())
    }

    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter(
            onToggleComplete = { habit ->
                repository.toggleHabitCompletion(habit.id)
                loadHabits()
            },
            onEditClick = { habit ->
                val intent = Intent(this, AddHabitActivity::class.java).apply {
                    putExtra(AddHabitActivity.EXTRA_HABIT_ID, habit.id)
                }
                addHabitLauncher.launch(intent)
            },
            onDeleteClick = { habit ->
                showDeleteConfirmationDialog(habit)
            }
        )
        binding.rvHabits.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = habitAdapter
        }
    }

    private fun loadHabits() {
        val habits = repository.getAllHabits()
        habitAdapter.submitList(habits)

        if (habits.isEmpty()) {
            binding.llEmptyState.visibility = View.VISIBLE
            binding.rvHabits.visibility = View.GONE
        } else {
            binding.llEmptyState.visibility = View.GONE
            binding.rvHabits.visibility = View.VISIBLE
        }

        updateProgress(habits)
    }

    private fun updateProgress(habits: List<Habit>) {
        val totalHabits = habits.size
        if (totalHabits == 0) {
            binding.pbHabits.progress = 0
            binding.tvProgressPercentage.text = "0%"
            binding.tvProgressStats.text = "0 of 0 habits completed"
            return
        }

        val completedHabits = habits.count { it.isCompleted }
        val percentage = (completedHabits * 100) / totalHabits

        binding.pbHabits.progress = percentage
        binding.tvProgressPercentage.text = "$percentage%"
        binding.tvProgressStats.text = "$completedHabits of $totalHabits habits completed"
    }

    private fun setupBottomNavigation() {
        binding.bnvMain.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> true
                R.id.nav_reports -> {
                    val intent = Intent(this, ReportsActivity::class.java)
                    startActivity(intent)
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

    private fun showDeleteConfirmationDialog(habit: Habit) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_dialog_title))
            .setMessage(getString(R.string.delete_dialog_message, habit.name))
            .setPositiveButton(getString(R.string.btn_delete)) { _, _ ->
                repository.deleteHabit(habit.id)
                loadHabits()
            }
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .show()
    }

    private fun setupFab() {
        binding.fabAddHabit.setOnClickListener {
            val intent = Intent(this, AddHabitActivity::class.java)
            addHabitLauncher.launch(intent)
        }
    }
}