package com.example.fitnesstracker_grp6.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fitnesstracker_grp6.R
import com.example.fitnesstracker_grp6.databinding.ActivityDashboardBinding
import com.example.fitnesstracker_grp6.ui.account.AccountActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()

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

        val userId = intent.getLongExtra(EXTRA_USER_ID, -1L)
        val username = intent.getStringExtra(EXTRA_USERNAME).orEmpty().ifEmpty { "Athlete" }
        val email = intent.getStringExtra(EXTRA_EMAIL).orEmpty()

        viewModel.initUser(userId, username, email)

        setupUI(username)
        setupModuleNavigation()
        setupBottomNav()
    }

    private fun setupUI(username: String) {
        // User initial (e.g., "AS" or "J")
        val initials = username.trim().split(" ")
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .take(2)
            .joinToString("")
            .ifEmpty { "AS" }
        binding.avatarInitialText.text = initials

        binding.searchButton.setOnClickListener {
            Toast.makeText(this, "Search fitness programs", Toast.LENGTH_SHORT).show()
        }

        binding.calendarButton.setOnClickListener {
            Toast.makeText(this, "Activity schedule", Toast.LENGTH_SHORT).show()
        }

        binding.signOutButton.setOnClickListener {
            showSignOutDialog()
        }
    }

    private fun setupModuleNavigation() {
        // Step Tracking Module (Teammate's section)
        binding.cardStepTracking.setOnClickListener {
            // Once your teammate creates StepTrackerActivity, replace with:
            // startActivity(Intent(this, StepTrackerActivity::class.java))
            Toast.makeText(this, R.string.module_steps_toast, Toast.LENGTH_SHORT).show()
        }

        // Workout Tracking Module (Teammate's section)
        binding.cardWorkoutTracking.setOnClickListener {
            // Once your teammate creates WorkoutTrackerActivity, replace with:
            // startActivity(Intent(this, WorkoutTrackerActivity::class.java))
            Toast.makeText(this, R.string.module_workout_toast, Toast.LENGTH_SHORT).show()
        }

        // Meal & Nutrition Tracking Module (Teammate's section)
        binding.cardMealTracking.setOnClickListener {
            // Once your teammate creates MealTrackerActivity, replace with:
            // startActivity(Intent(this, MealTrackerActivity::class.java))
            Toast.makeText(this, R.string.module_meal_toast, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNav() {
        binding.navHome.setOnClickListener {
            // Currently on Home
        }
        binding.navExplore.setOnClickListener {
            Toast.makeText(this, "Explore all fitness categories", Toast.LENGTH_SHORT).show()
        }
        binding.navActivity.setOnClickListener {
            Toast.makeText(this, "Activity check-ins and history", Toast.LENGTH_SHORT).show()
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
