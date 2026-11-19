package com.example.fitnesstracker_grp6.ui.meal

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.fitnesstracker_grp6.R
import com.example.fitnesstracker_grp6.data.local.Meal
import com.example.fitnesstracker_grp6.data.setup.FitnessTrackerApplication
import kotlinx.coroutines.launch

class MealEditActivity : AppCompatActivity() {

    private val viewModel: MealViewModel by viewModels {
        MealViewModelFactory((application as FitnessTrackerApplication).mealRepository)
    }

    private val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")
    private var existingMeal: Meal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_edit)

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val etName = findViewById<EditText>(R.id.etName)
        val spType = findViewById<Spinner>(R.id.spMealType)
        val etCalories = findViewById<EditText>(R.id.etCalories)
        val etProtein = findViewById<EditText>(R.id.etProtein)
        val etCarbs = findViewById<EditText>(R.id.etCarbs)
        val etFat = findViewById<EditText>(R.id.etFat)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        spType.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, mealTypes
        )

        // Edit mode: load the meal and fill the form
        val mealId = intent.getIntExtra(EXTRA_MEAL_ID, -1)
        if (mealId != -1) {
            tvTitle.text = "Edit Meal"
            btnSave.isEnabled = false          // wait until the meal has loaded
            lifecycleScope.launch {
                val meal = viewModel.getMeal(mealId)
                if (meal == null) {
                    Toast.makeText(this@MealEditActivity, "Meal not found", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    existingMeal = meal
                    etName.setText(meal.name)
                    spType.setSelection(mealTypes.indexOf(meal.mealType).coerceAtLeast(0))
                    etCalories.setText(meal.calories.toString())
                    etProtein.setText(if (meal.protein == 0.0) "" else meal.protein.toString())
                    etCarbs.setText(if (meal.carbs == 0.0) "" else meal.carbs.toString())
                    etFat.setText(if (meal.fat == 0.0) "" else meal.fat.toString())
                    btnSave.isEnabled = true
                }
            }
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val type = spType.selectedItem.toString()
            val calories = etCalories.text.toString()
            val protein = etProtein.text.toString()
            val carbs = etCarbs.text.toString()
            val fat = etFat.text.toString()

            val existing = existingMeal
            if (existing == null) {
                viewModel.addMeal(name, type, calories, protein, carbs, fat)
            } else {
                viewModel.updateMeal(existing, name, type, calories, protein, carbs, fat)
            }
        }

        btnCancel.setOnClickListener { finish() }

        // Listen for the result from the ViewModel
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is MealEvent.Saved -> {
                            Toast.makeText(this@MealEditActivity, "Meal saved", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        is MealEvent.Error ->
                            Toast.makeText(this@MealEditActivity, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_MEAL_ID = "extra_meal_id"
    }
}