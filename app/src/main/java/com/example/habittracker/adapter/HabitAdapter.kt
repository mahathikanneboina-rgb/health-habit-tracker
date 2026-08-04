package com.example.habittracker.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R
import com.example.habittracker.databinding.ItemHabitBinding
import com.example.habittracker.data.Habit

class HabitAdapter(
    private val onToggleComplete: (Habit) -> Unit,
    private val onEditClick: (Habit) -> Unit,
    private val onDeleteClick: (Habit) -> Unit
) : ListAdapter<Habit, HabitAdapter.HabitViewHolder>(HabitDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(getItem(position))

        // Fade-in slide-up animation for each item
        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_in_slide_up)
        animation.startOffset = (position * 100L)
        holder.itemView.startAnimation(animation)
    }

    override fun onViewDetachedFromWindow(holder: HabitViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    inner class HabitViewHolder(private val binding: ItemHabitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(habit: Habit) {
            binding.tvHabitName.text = habit.name

            // Category badge styling & emoji
            val categoryEmoji = when (habit.category) {
                "Water" -> "💧"
                "Exercise" -> "🏃‍♂️"
                "Sleep" -> "😴"
                "Study" -> "📚"
                "Meditation" -> "🧘‍♂️"
                else -> "🎨"
            }
            binding.tvHabitCategory.text = "${habit.category} $categoryEmoji"

            // Set category icon in the circle
            binding.tvCategoryIcon.text = categoryEmoji

            // Goal & Time
            val goalText = if (habit.dailyGoal.isNotBlank()) "Goal: ${habit.dailyGoal}" else ""
            val timeText = if (habit.reminderTime.isNotBlank()) "⏰ ${habit.reminderTime}" else ""
            binding.tvHabitGoalAndTime.text = listOf(goalText, timeText).filter { it.isNotBlank() }.joinToString(" • ")

            // Checkbox state & strikethrough styling
            binding.cbHabitComplete.setOnCheckedChangeListener(null)
            binding.cbHabitComplete.isChecked = habit.isCompleted

            if (habit.isCompleted) {
                binding.tvHabitName.paintFlags = binding.tvHabitName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvHabitName.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.text_light)
                )
            } else {
                binding.tvHabitName.paintFlags = binding.tvHabitName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.tvHabitName.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.text_primary)
                )
            }

            // Click Handlers
            binding.cbHabitComplete.setOnClickListener {
                onToggleComplete(habit)
            }

            binding.btnEditHabit.setOnClickListener {
                onEditClick(habit)
            }

            binding.btnDeleteHabit.setOnClickListener {
                onDeleteClick(habit)
            }
        }
    }

    class HabitDiffCallback : DiffUtil.ItemCallback<Habit>() {
        override fun areItemsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem == newItem
        }
    }
}
