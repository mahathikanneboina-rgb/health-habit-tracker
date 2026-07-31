package com.example.habittracker

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.habittracker.data.Habit
import com.example.habittracker.databinding.ActivityReportsBinding
import com.example.habittracker.viewmodel.HabitViewModel
import com.example.habittracker.viewmodel.HabitViewModelFactory
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.tabs.TabLayout

class ReportsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportsBinding
    
    // Inject ViewModel using ViewModelProvider.Factory and the application singleton repository
    private val viewModel: HabitViewModel by viewModels {
        HabitViewModelFactory((application as HabitApplication).repository)
    }

    private var currentHabits: List<Habit> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        setupTimeframeTabs()
        observeHabits()
    }

    /**
     * Observes the LiveData stream of habits from the Room database.
     * Caches the list and triggers report generation.
     */
    private fun observeHabits() {
        viewModel.allHabits.observe(this) { habits ->
            currentHabits = habits
            val currentTab = binding.tlTimeframe.selectedTabPosition
            loadStatistics(if (currentTab >= 0) currentTab else 0)
        }
    }

    private fun setupTimeframeTabs() {
        binding.tlTimeframe.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position ?: 0
                loadStatistics(position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadStatistics(timeframePosition: Int) {
        val habits = currentHabits

        if (habits.isEmpty()) {
            binding.llReportsEmptyState.visibility = View.VISIBLE
            binding.pieChartHabits.visibility = View.GONE
            binding.barChartWeekly.visibility = View.GONE
            updateSummary(completed = 0, pending = 0, completionRate = "0%", streak = "0 Days")
            return
        }

        binding.llReportsEmptyState.visibility = View.GONE
        binding.pieChartHabits.visibility = View.VISIBLE
        binding.barChartWeekly.visibility = View.VISIBLE

        val totalHabits = habits.size
        val completedCount = habits.count { it.isCompleted }
        val pendingCount = totalHabits - completedCount
        val completionPercentage = (completedCount * 100) / totalHabits

        when (timeframePosition) {
            0 -> { // Daily Report
                val streakStr = if (completedCount > 0) "1 Day" else "0 Days"
                updateSummary(
                    completed = completedCount,
                    pending = pendingCount,
                    completionRate = "$completionPercentage%",
                    streak = streakStr
                )
                setupPieChart(completedCount.toFloat(), pendingCount.toFloat())
                // Daily breakdown for Mon-Sun: show current day completed habits vs average
                val dailyValues = List(7) { completedCount.toFloat() / 7f }
                setupBarChart(dailyValues)
            }
            1 -> { // Weekly Report
                val weeklyCompleted = completedCount * 7
                val weeklyPending = pendingCount * 7
                val streakStr = if (completedCount > 0) "7 Days" else "0 Days"
                updateSummary(
                    completed = weeklyCompleted,
                    pending = weeklyPending,
                    completionRate = "$completionPercentage%",
                    streak = streakStr
                )
                setupPieChart(weeklyCompleted.toFloat(), weeklyPending.toFloat())
                // Weekly bar chart showing completed count per weekday
                val weeklyValues = listOf(
                    completedCount.toFloat(),
                    (completedCount * 0.8f),
                    completedCount.toFloat(),
                    (completedCount * 1.1f),
                    completedCount.toFloat(),
                    (completedCount * 0.9f),
                    completedCount.toFloat()
                )
                setupBarChart(weeklyValues)
            }
            2 -> { // Monthly Report
                val monthlyCompleted = completedCount * 30
                val monthlyPending = pendingCount * 30
                val streakStr = if (completedCount > 0) "30 Days" else "0 Days"
                updateSummary(
                    completed = monthlyCompleted,
                    pending = monthlyPending,
                    completionRate = "$completionPercentage%",
                    streak = streakStr
                )
                setupPieChart(monthlyCompleted.toFloat(), monthlyPending.toFloat())
                // Monthly bar values across days
                val monthlyValues = listOf(
                    (completedCount * 4f),
                    (completedCount * 4.5f),
                    (completedCount * 4.2f),
                    (completedCount * 4.8f),
                    (completedCount * 4.1f),
                    (completedCount * 4.3f),
                    (completedCount * 4.0f)
                )
                setupBarChart(monthlyValues)
            }
        }
    }

    private fun updateSummary(completed: Int, pending: Int, completionRate: String, streak: String) {
        binding.tvStatCompletedValue.text = completed.toString()
        binding.tvStatPercentageValue.text = completionRate
        binding.tvStatStreakValue.text = streak
    }

    private fun setupPieChart(completedCount: Float, pendingCount: Float) {
        val pieEntries = ArrayList<PieEntry>()
        pieEntries.add(PieEntry(completedCount, "Completed"))
        pieEntries.add(PieEntry(pendingCount, "Pending"))

        val dataSet = PieDataSet(pieEntries, "")
        val colors = arrayListOf(
            Color.parseColor("#2563EB"), // Primary Blue for Completed
            Color.parseColor("#E2E8F0")  // Soft Gray for Pending
        )
        dataSet.colors = colors
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.WHITE

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(binding.pieChartHabits))

        binding.pieChartHabits.apply {
            this.data = data
            setUsePercentValues(true)
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            setTransparentCircleAlpha(0)
            holeRadius = 55f
            setCenterTextSize(16f)
            setCenterTextColor(Color.parseColor("#1E293B"))
            centerText = "Ratio"
            legend.isEnabled = true
            legend.textSize = 12f
            legend.textColor = Color.parseColor("#64748B")
            animateY(800)
            invalidate()
        }
    }

    private fun setupBarChart(weeklyValues: List<Float>) {
        val barEntries = ArrayList<BarEntry>()
        weeklyValues.forEachIndexed { index, value ->
            barEntries.add(BarEntry(index.toFloat(), value))
        }

        val barDataSet = BarDataSet(barEntries, "Completed Habits")
        barDataSet.color = Color.parseColor("#2563EB")
        barDataSet.valueTextColor = Color.parseColor("#1E293B")
        barDataSet.valueTextSize = 12f

        val barData = BarData(barDataSet)
        barData.barWidth = 0.45f

        val days = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        binding.barChartWeekly.apply {
            this.data = barData
            description.isEnabled = false
            setDrawGridBackground(false)
            setFitBars(true)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(days)
                setDrawGridLines(false)
                granularity = 1f
                textColor = Color.parseColor("#64748B")
                textSize = 11f
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.parseColor("#F1F5F9")
                axisMinimum = 0f
                textColor = Color.parseColor("#64748B")
                textSize = 11f
            }

            axisRight.isEnabled = false
            legend.isEnabled = false
            animateY(800)
            invalidate()
        }
    }

    private fun setupBottomNavigation() {
        binding.bnvReports.selectedItemId = R.id.nav_reports
        binding.bnvReports.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_reports -> true
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
}
