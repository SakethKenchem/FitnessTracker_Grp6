package com.example.fitnesstracker_grp6.ui.steps

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.fitnesstracker_grp6.R
import com.example.fitnesstracker_grp6.data.repository.DailySteps
import com.example.fitnesstracker_grp6.databinding.ActivityStepTrackerBinding
import com.example.fitnesstracker_grp6.ui.dashboard.DashboardActivity

class StepTrackerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStepTrackerBinding
    private val dailySteps by lazy {
        DailySteps(this, intent.getLongExtra(DashboardActivity.EXTRA_USER_ID, -1L))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStepTrackerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val padding = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            view.setPadding(padding.left, padding.top, padding.right, padding.bottom)
            insets
        }

        binding.backButton.setOnClickListener { finish() }
        binding.addStepsButton.setOnClickListener {
            val amount = binding.stepsInput.text.toString().trim().toLongOrNull()
            val total = amount?.let(dailySteps::add)
            if (total == null) {
                binding.stepsInputLayout.error = getString(R.string.module_steps_invalid)
                return@setOnClickListener
            }

            showTotal(total)
            binding.stepsInputLayout.error = null
            binding.stepsInput.text?.clear()
            binding.stepsInput.clearFocus()
            WindowInsetsControllerCompat(window, binding.root).hide(WindowInsetsCompat.Type.ime())
        }
    }

    override fun onResume() {
        super.onResume()
        showTotal(dailySteps.total())
    }

    private fun showTotal(total: Long) {
        binding.stepsTodayText.text = getString(R.string.module_steps_total, total)
    }
}
