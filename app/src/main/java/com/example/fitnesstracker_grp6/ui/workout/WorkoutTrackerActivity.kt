package com.example.fitnesstracker_grp6.ui.workout

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitnesstracker_grp6.R
import com.example.fitnesstracker_grp6.data.setup.FitnessTrackerApplication
import com.example.fitnesstracker_grp6.databinding.ActivityWorkoutTrackerBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class WorkoutTrackerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutTrackerBinding
    private var userId: Long = -1L

    private val viewModel: WorkoutViewModel by viewModels {
        viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    as FitnessTrackerApplication
                WorkoutViewModel(app.workoutRepository)
            }
        }
    }

    private lateinit var adapter: WorkoutAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWorkoutTrackerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val padding = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            view.setPadding(padding.left, padding.top, padding.right, padding.bottom)
            insets
        }

        userId = intent.getLongExtra(EXTRA_USER_ID, -1L)

        binding.backButton.setOnClickListener { finish() }

        setupRecyclerView()
        setupListeners()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = WorkoutAdapter(onDeleteClick = { session ->
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.workout_delete_confirm_title)
                .setMessage(R.string.workout_delete_confirm_msg)
                .setPositiveButton(R.string.dashboard_confirm) { _, _ ->
                    viewModel.deleteWorkout(session)
                    Toast.makeText(this, R.string.workout_deleted_toast, Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(R.string.dashboard_cancel, null)
                .show()
        })
        binding.rvWorkouts.layoutManager = LinearLayoutManager(this)
        binding.rvWorkouts.adapter = adapter
    }

    private fun setupListeners() = with(binding) {
        btnSaveWorkout.setOnClickListener {
            val type = etWorkoutType.text.toString().trim()
            val duration = etDuration.text.toString().trim().toIntOrNull()
            val calories = etCalories.text.toString().trim().toIntOrNull()
            val notes = etNotes.text.toString().trim()

            if (duration == null || duration <= 0 || calories == null || calories < 0) {
                Toast.makeText(
                    this@WorkoutTrackerActivity,
                    R.string.workout_invalid_inputs,
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            viewModel.addWorkout(
                userId = userId,
                type = type.ifBlank { "General Workout" },
                durationMinutes = duration,
                caloriesBurned = calories,
                notes = notes
            )

            Toast.makeText(
                this@WorkoutTrackerActivity,
                R.string.workout_added_toast,
                Toast.LENGTH_SHORT
            ).show()

            etDuration.text?.clear()
            etCalories.text?.clear()
            etNotes.text?.clear()
            etWorkoutType.clearFocus()
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.getSessions(userId).collect { sessions ->
                        adapter.submitList(sessions)
                        binding.tvEmptyHistory.visibility =
                            if (sessions.isEmpty()) View.VISIBLE else View.GONE
                        binding.rvWorkouts.visibility =
                            if (sessions.isEmpty()) View.GONE else View.VISIBLE
                    }
                }

                launch {
                    viewModel.getSessionCount(userId).collect { count ->
                        binding.tvStatSessions.text = count.toString()
                    }
                }

                launch {
                    viewModel.getTotalDuration(userId).collect { totalDuration ->
                        val duration = totalDuration ?: 0
                        binding.tvStatDuration.text =
                            getString(R.string.workout_duration_min, duration)
                    }
                }

                launch {
                    viewModel.getTotalCalories(userId).collect { totalCalories ->
                        val calories = totalCalories ?: 0
                        binding.tvStatCalories.text =
                            getString(R.string.workout_calories_kcal, calories)
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}
