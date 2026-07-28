package com.example.habittracker

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.habittracker.databinding.ActivityReportsBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        setupTimeframeTabs()

        // Initial load for Daily timeframe
        loadStatistics(0)
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
        when (timeframePosition) {
            0 -> { // Daily
                updateSummary(completed = 3, pending = 1, completionRate = "75%", streak = "5 Days")
                setupPieChart(completedCount = 3f, pendingCount = 1f)
                setupBarChart(listOf(2f, 3f, 4f, 3f, 4f, 2f, 3f))
            }
            1 -> { // Weekly
                updateSummary(completed = 21, pending = 7, completionRate = "75%", streak = "5 Days")
                setupPieChart(completedCount = 21f, pendingCount = 7f)
                setupBarChart(listOf(3f, 4f, 2f, 4f, 3f, 2f, 3f))
            }
            2 -> { // Monthly
                updateSummary(completed = 84, pending = 28, completionRate = "75%", streak = "14 Days")
                setupPieChart(completedCount = 84f, pendingCount = 28f)
                setupBarChart(listOf(18f, 22f, 20f, 24f, 19f, 21f, 20f))
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
