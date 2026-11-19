package com.example.fitnesstracker_grp6.ui.workout

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fitnesstracker_grp6.databinding.ActivityWorkoutTrackerBinding

class WorkoutTrackerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutTrackerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWorkoutTrackerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val padding = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(padding.left, padding.top, padding.right, padding.bottom)
            insets
        }

        userId = intent.getLongExtra(EXTRA_USER_ID, -1L)
        binding.backButton.setOnClickListener { finish() }
    }

    private var userId: Long = -1L

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}
