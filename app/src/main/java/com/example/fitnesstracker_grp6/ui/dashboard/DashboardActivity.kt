package com.example.fitnesstracker_grp6.ui.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fitnesstracker_grp6.R
import com.example.fitnesstracker_grp6.data.repository.DailySteps
import com.example.fitnesstracker_grp6.databinding.ActivityDashboardBinding
import com.example.fitnesstracker_grp6.ui.account.AccountActivity
import com.example.fitnesstracker_grp6.ui.meal.MealListActivity
import com.example.fitnesstracker_grp6.ui.steps.StepTrackerActivity
import com.example.fitnesstracker_grp6.ui.workout.WorkoutTrackerActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()
    private var userId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val padding = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(padding.left, padding.top, padding.right, 0)
            binding.bottomNavBar.setPadding(0, 0, 0, padding.bottom)
            insets
        }

        userId = intent.getLongExtra(EXTRA_USER_ID, -1L)
        val username = intent.getStringExtra(EXTRA_USERNAME).orEmpty().ifEmpty { "Athlete" }
        val email = intent.getStringExtra(EXTRA_EMAIL).orEmpty()

        viewModel.initUser(userId, username, email)

        setupUI(username)
        setupModuleNavigation()
        setupBottomNav()
    }

    override fun onResume() {
        super.onResume()
        updateStepsDisplay()
    }

    private fun updateStepsDisplay() {
        if (userId != -1L) {
            val totalSteps = DailySteps(this, userId).total()
            binding.tvStepCount.text = getString(R.string.module_steps_total, totalSteps)
        }
    }

    private fun setupUI(username: String) {
        val initials = username.trim().split(" ")
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .take(2)
            .joinToString("")
            .ifEmpty { "AS" }
        binding.avatarInitialText.text = initials
        binding.greetingTitleText.text = getString(R.string.auth_welcome, username)

        binding.signOutButton.setOnClickListener {
            showSignOutDialog()
        }
    }

    private fun setupModuleNavigation() {
        // Step Tracking Module
        val openSteps = {
            val intent = Intent(this, StepTrackerActivity::class.java).apply {
                putExtra(EXTRA_USER_ID, userId)
            }
            startActivity(intent)
        }
        binding.cardStepTracking.setOnClickListener { openSteps() }
        binding.btnOpenSteps.setOnClickListener { openSteps() }

        // Workout Tracking Module
        val openWorkouts = {
            val intent = Intent(this, WorkoutTrackerActivity::class.java).apply {
                putExtra(WorkoutTrackerActivity.EXTRA_USER_ID, userId)
            }
            startActivity(intent)
        }
        binding.cardWorkoutTracking.setOnClickListener { openWorkouts() }
        binding.btnOpenWorkouts.setOnClickListener { openWorkouts() }

        // Meal Tracking Module
        val openMeals = {
            startActivity(Intent(this, MealListActivity::class.java))
        }
        binding.cardMealTracking.setOnClickListener { openMeals() }
        binding.btnOpenMeals.setOnClickListener { openMeals() }
    }

    private fun setupBottomNav() {
        binding.navHome.setOnClickListener {
            // Currently on Home
        }
        binding.navMore.setOnClickListener {
            showSignOutDialog()
        }
    }

    private fun showSignOutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.auth_sign_out)
            .setMessage(R.string.dashboard_sign_out_confirm)
            .setPositiveButton(R.string.dashboard_confirm) { _, _ ->
                val intent = Intent(this, AccountActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
            .setNegativeButton(R.string.dashboard_cancel, null)
            .show()
    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
        const val EXTRA_USERNAME = "extra_username"
        const val EXTRA_EMAIL = "extra_email"
    }
}
