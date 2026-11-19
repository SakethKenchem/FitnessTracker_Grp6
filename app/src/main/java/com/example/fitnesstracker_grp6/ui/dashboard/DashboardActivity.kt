package com.example.fitnesstracker_grp6.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.fitnesstracker_grp6.R
import com.example.fitnesstracker_grp6.databinding.ActivityDashboardBinding
import com.example.fitnesstracker_grp6.databinding.DialogBmiCalculatorBinding
import com.example.fitnesstracker_grp6.ui.account.AccountActivity
import com.example.fitnesstracker_grp6.ui.meal.MealListActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            view.setPadding(padding.left, padding.top, padding.right, padding.bottom)
            insets
        }

        val userId = intent.getLongExtra(EXTRA_USER_ID, -1L)
        val username = intent.getStringExtra(EXTRA_USERNAME).orEmpty().ifEmpty { "Athlete" }
        val email = intent.getStringExtra(EXTRA_EMAIL).orEmpty()

        viewModel.initUser(userId, username, email)

        setupUI(username, email)
        setupModuleNavigation()
        setupTools()
        observeState()
    }

    private fun setupUI(username: String, email: String) {
        val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        binding.dateText.text = dateFormat.format(Date())
        binding.greetingText.text = getString(R.string.dashboard_greeting, username)
        binding.userEmailText.text = email.ifEmpty { "Signed in user" }
        binding.avatarInitialText.text = username.firstOrNull()?.uppercase() ?: "A"
    }

    private fun setupModuleNavigation() {
        // Step Tracking Module (Teammate's section)
        binding.btnOpenSteps.setOnClickListener {
            // Once your teammate creates StepTrackerActivity, replace with:
            // startActivity(Intent(this, StepTrackerActivity::class.java))
            Toast.makeText(this, R.string.module_steps_toast, Toast.LENGTH_SHORT).show()
        }

        // Workout Tracking Module (Teammate's section)
        binding.btnOpenWorkout.setOnClickListener {
            // Once your teammate creates WorkoutTrackerActivity, replace with:
            // startActivity(Intent(this, WorkoutTrackerActivity::class.java))
            Toast.makeText(this, R.string.module_workout_toast, Toast.LENGTH_SHORT).show()
        }

        // Meal Tracking Module (Teammate's section)
        // Meal Tracking Module
        binding.btnOpenMeal.setOnClickListener {
            startActivity(Intent(this, MealListActivity::class.java))
        }
    }

    private fun setupTools() {
        binding.addWaterButton.setOnClickListener {
            viewModel.addWater(250)
            Toast.makeText(this, R.string.dashboard_water_added, Toast.LENGTH_SHORT).show()
        }

        binding.bmiCard.setOnClickListener {
            showBmiDialog()
        }

        binding.signOutButton.setOnClickListener {
            showSignOutDialog()
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: DashboardUiState) = with(binding) {
        val waterProgress = ((state.waterMl.toFloat() / state.waterGoalMl) * 100).toInt().coerceAtMost(100)
        waterValueText.text = "${state.waterMl} / ${state.waterGoalMl} ml"
        waterProgressBar.progress = waterProgress
    }

    private fun showBmiDialog() {
        val dialogBinding = DialogBmiCalculatorBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.btn_calculate_bmi, null)
            .setNegativeButton(R.string.dashboard_cancel, null)
            .create()

        dialog.setOnShowListener {
            val calcBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            calcBtn.setOnClickListener {
                val heightStr = dialogBinding.heightInput.text.toString().trim()
                val weightStr = dialogBinding.weightInput.text.toString().trim()

                val heightCm = heightStr.toDoubleOrNull()
                val weightKg = weightStr.toDoubleOrNull()

                if (heightCm == null || heightCm <= 0 || weightKg == null || weightKg <= 0) {
                    Toast.makeText(this, "Please enter valid height and weight", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val heightM = heightCm / 100.0
                val bmi = weightKg / (heightM * heightM)

                val category = when {
                    bmi < 18.5 -> getString(R.string.bmi_underweight)
                    bmi < 25.0 -> getString(R.string.bmi_normal)
                    bmi < 30.0 -> getString(R.string.bmi_overweight)
                    else -> getString(R.string.bmi_obese)
                }

                dialogBinding.bmiScoreText.text = String.format(Locale.getDefault(), "%.1f", bmi)
                dialogBinding.bmiCategoryText.text = category
                dialogBinding.bmiResultCard.isVisible = true

                binding.bmiValuePreview.text = String.format(Locale.getDefault(), "%.1f (%s)", bmi, category)
            }
        }

        dialog.show()
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
