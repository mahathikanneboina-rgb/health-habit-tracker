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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        displayTodayDate()
        setupBottomNavigation()
        setupFab()
        updateProgress()
    }

    private fun displayTodayDate() {
        val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        binding.tvTodayDate.text = dateFormat.format(Date())
    }

    private fun updateProgress() {
        val totalHabits = 4
        val completedHabits = 3
        val percentage = 75

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
                    val intent = android.content.Intent(this, ReportsActivity::class.java)
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

    private fun setupFab() {
        binding.fabAddHabit.setOnClickListener {
            Toast.makeText(this, getString(R.string.add_habit_clicked), Toast.LENGTH_SHORT).show()
        }
    }
}