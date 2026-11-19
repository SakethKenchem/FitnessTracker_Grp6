package com.example.fitnesstracker_grp6.ui.meal

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnesstracker_grp6.R
import com.example.fitnesstracker_grp6.data.setup.FitnessTrackerApplication
import kotlinx.coroutines.launch

class MealListActivity : AppCompatActivity() {

    private val viewModel: MealViewModel by viewModels {
        MealViewModelFactory((application as FitnessTrackerApplication).mealRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_list)

        val rvMeals = findViewById<RecyclerView>(R.id.rvMeals)
        val tvTotal = findViewById<TextView>(R.id.tvTotalCalories)
        val tvEmpty = findViewById<TextView>(R.id.tvEmpty)
        val btnAdd = findViewById<Button>(R.id.btnAddMeal)

        val adapter = MealAdapter(
            onEdit = { meal ->
                startActivity(
                    Intent(this, MealEditActivity::class.java)
                        .putExtra(MealEditActivity.EXTRA_MEAL_ID, meal.id)
                )
            },
            onDelete = { meal ->
                AlertDialog.Builder(this)
                    .setTitle("Delete meal")
                    .setMessage("Delete \"${meal.name}\"?")
                    .setPositiveButton("Delete") { _, _ -> viewModel.deleteMeal(meal) }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )
        rvMeals.layoutManager = LinearLayoutManager(this)
        rvMeals.adapter = adapter

        btnAdd.setOnClickListener {
            startActivity(Intent(this, MealEditActivity::class.java))
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.todaysMeals.collect { meals ->
                        adapter.submitList(meals)
                        tvEmpty.visibility = if (meals.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    viewModel.totalCalories.collect { total ->
                        tvTotal.text = "Total: $total kcal"
                    }
                }
                launch {
                    viewModel.events.collect { event ->
                        if (event is MealEvent.Error) {
                            Toast.makeText(this@MealListActivity, event.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}