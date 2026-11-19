package com.example.fitnesstracker_grp6.ui.workout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnesstracker_grp6.R
import com.example.fitnesstracker_grp6.data.local.WorkoutSession
import com.example.fitnesstracker_grp6.databinding.ItemWorkoutBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkoutAdapter(
    private val onDeleteClick: (WorkoutSession) -> Unit
) : ListAdapter<WorkoutSession, WorkoutAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWorkoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemWorkoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(session: WorkoutSession) = with(binding) {
            tvWorkoutType.text = session.type
            tvWorkoutDetails.text = root.context.getString(
                R.string.workout_stat_details,
                session.durationMinutes,
                session.caloriesBurned
            )

            if (session.notes.isNotBlank()) {
                tvNotes.visibility = View.VISIBLE
                tvNotes.text = session.notes
            } else {
                tvNotes.visibility = View.GONE
            }

            val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
            tvDate.text = dateFormat.format(Date(session.dateMillis))

            btnDelete.setOnClickListener { onDeleteClick(session) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WorkoutSession>() {
            override fun areItemsTheSame(oldItem: WorkoutSession, newItem: WorkoutSession): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: WorkoutSession, newItem: WorkoutSession): Boolean {
                return oldItem == newItem
            }
        }
    }
}
