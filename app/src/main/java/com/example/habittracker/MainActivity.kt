package com.example.habittracker

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.activity.viewModels
import com.example.habittracker.adapter.HabitAdapter
import com.example.habittracker.data.Habit
import com.example.habittracker.databinding.ActivityMainBinding
import com.example.habittracker.viewmodel.HabitViewModel
import com.example.habittracker.viewmodel.HabitViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var habitAdapter: HabitAdapter

    // Inject ViewModel using ViewModelProvider.Factory and the application singleton repository
    private val viewModel: HabitViewModel by viewModels {
        HabitViewModelFactory((application as HabitApplication).repository)
    }

    private val addHabitLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        // No manual refresh needed; LiveData handles UI updates reactively.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        displayTodayDate()
        displayMotivationalQuote()
        setupRecyclerView()
        setupBottomNavigation()
        setupFab()
        observeHabits()
        observeInsight()
    }

    private fun displayMotivationalQuote() {
        val quotes = resources.getStringArray(R.array.motivational_quotes)
        val randomQuote = quotes.random()
        binding.cardQuote.tvQuoteText.text = randomQuote
    }

    private fun displayTodayDate() {
        val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        binding.tvTodayDate.text = dateFormat.format(Date())
    }

    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter(
            onToggleComplete = { habit ->
                viewModel.toggleHabitCompletion(habit.id)
            },
            onEditClick = { habit ->
                val intent = Intent(this, AddHabitActivity::class.java).apply {
                    putExtra(AddHabitActivity.EXTRA_HABIT_ID, habit.id)
                }
                addHabitLauncher.launch(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            },
            onDeleteClick = { habit ->
                showDeleteConfirmationDialog(habit)
            }
        )
        binding.rvHabits.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = habitAdapter
            val animation = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.fade_in)
            layoutAnimation = android.view.animation.LayoutAnimationController(animation).apply {
                delay = 0.1f
                order = android.view.animation.LayoutAnimationController.ORDER_NORMAL
            }
        }
    }

    /**
     * Observes the LiveData stream of habits from the Room database.
     * Updates the adapter and dashboard stats reactively.
     */
    private fun observeHabits() {
        viewModel.allHabits.observe(this) { habits ->
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
    }

    /**
     * Observes the AI health insight from the ViewModel.
     * Updates the insight card message and statistics reactively.
     */
    private fun observeInsight() {
        viewModel.insight.observe(this) { insight ->
            binding.cardInsight.tvInsightMessage.text = insight.message
            binding.cardInsight.tvInsightCompletedVal.text = "${insight.completedHabits} / ${insight.totalHabits}"
            binding.cardInsight.tvInsightPercentVal.text = "${insight.completionPercentage}%"
            binding.cardInsight.tvInsightStreakVal.text = "🔥 ${insight.currentStreak} days"
            binding.cardInsight.tvInsightWeeklyVal.text = insight.weeklyProgress
        }
    }

    private fun updateProgress(habits: List<Habit>) {
        val totalHabits = habits.size
        if (totalHabits == 0) {
            binding.circularProgress.progress = 0
            binding.tvProgressPercentage.text = "0%"
            binding.tvProgressStats.text = "0 of 0 habits completed"
            return
        }

        val completedHabits = habits.count { it.isCompleted }
        val percentage = (completedHabits * 100) / totalHabits

        // Animate the circular progress
        val animator = ObjectAnimator.ofInt(binding.circularProgress, "progress", percentage)
        animator.duration = 500
        animator.interpolator = DecelerateInterpolator()
        animator.start()

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
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    true
                }
                R.id.nav_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Show confirmation dialog when user selects Logout / Profile.
     */
    private fun showLogoutConfirmationDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.logout_confirmation_title))
            .setMessage(getString(R.string.logout_confirmation_message))
            .setPositiveButton(getString(R.string.btn_logout)) { _, _ ->
                com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .show()
    }

    private fun showDeleteConfirmationDialog(habit: Habit) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_dialog_title))
            .setMessage(getString(R.string.delete_dialog_message, habit.name))
            .setPositiveButton(getString(R.string.btn_delete)) { _, _ ->
                viewModel.delete(habit.id)
            }
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .show()
    }

    private fun setupFab() {
        binding.fabAddHabit.setOnClickListener {
            val intent = Intent(this, AddHabitActivity::class.java)
            addHabitLauncher.launch(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}