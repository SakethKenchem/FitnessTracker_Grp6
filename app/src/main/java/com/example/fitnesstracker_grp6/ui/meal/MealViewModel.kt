package com.example.fitnesstracker_grp6.ui.meal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker_grp6.data.local.Meal
import com.example.fitnesstracker_grp6.data.local.MealRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

sealed class MealEvent {
    object Saved : MealEvent()
    data class Error(val message: String) : MealEvent()
}

class MealViewModel(private val repository: MealRepository) : ViewModel() {

    private val _events = MutableSharedFlow<MealEvent>()
    val events: SharedFlow<MealEvent> = _events

    private val dayStart: Long
    private val dayEnd: Long

    init {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        dayStart = cal.timeInMillis
        dayEnd = dayStart + 24 * 60 * 60 * 1000 - 1
    }

    // Today's meals, updates automatically when the database changes
    val todaysMeals: StateFlow<List<Meal>> = repository.mealsBetween(dayStart, dayEnd)
        .catch { emit(emptyList()); _events.emit(MealEvent.Error("Could not load meals")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCalories: StateFlow<Int> = todaysMeals
        .map { list -> list.sumOf { it.calories } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun addMeal(
        name: String, mealType: String,
        caloriesText: String, proteinText: String, carbsText: String, fatText: String
    ) {
        viewModelScope.launch {
            val meal = buildMeal(null, name, mealType, caloriesText, proteinText, carbsText, fatText)
                ?: return@launch
            try {
                repository.addMeal(meal)
                _events.emit(MealEvent.Saved)
            } catch (e: Exception) {
                _events.emit(MealEvent.Error("Could not save meal"))
            }
        }
    }

    fun updateMeal(
        existing: Meal, name: String, mealType: String,
        caloriesText: String, proteinText: String, carbsText: String, fatText: String
    ) {
        viewModelScope.launch {
            val meal = buildMeal(existing, name, mealType, caloriesText, proteinText, carbsText, fatText)
                ?: return@launch
            try {
                repository.updateMeal(meal)
                _events.emit(MealEvent.Saved)
            } catch (e: Exception) {
                _events.emit(MealEvent.Error("Could not update meal"))
            }
        }
    }

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            try {
                repository.deleteMeal(meal)
            } catch (e: Exception) {
                _events.emit(MealEvent.Error("Could not delete meal"))
            }
        }
    }

    suspend fun getMeal(id: Int): Meal? = repository.getMeal(id)

    // Validates the input; returns null (and emits an error) if something is invalid
    private suspend fun buildMeal(
        base: Meal?, name: String, mealType: String,
        caloriesText: String, proteinText: String, carbsText: String, fatText: String
    ): Meal? {
        val cleanName = name.trim()
        if (cleanName.isEmpty()) {
            _events.emit(MealEvent.Error("Meal name is required")); return null
        }
        val calories = caloriesText.trim().toIntOrNull()
        if (calories == null || calories <= 0 || calories > 10000) {
            _events.emit(MealEvent.Error("Calories must be between 1 and 10000")); return null
        }
        val protein = parseMacro(proteinText)
        val carbs = parseMacro(carbsText)
        val fat = parseMacro(fatText)
        if (protein == null || carbs == null || fat == null) {
            _events.emit(MealEvent.Error("Protein, carbs and fat must be numbers from 0 to 1000"))
            return null
        }
        return (base ?: Meal(name = "", mealType = "", calories = 0)).copy(
            name = cleanName, mealType = mealType,
            calories = calories, protein = protein, carbs = carbs, fat = fat
        )
    }

    // Blank means 0; returns null if invalid
    private fun parseMacro(text: String): Double? {
        if (text.isBlank()) return 0.0
        val value = text.trim().toDoubleOrNull() ?: return null
        return if (value in 0.0..1000.0) value else null
    }
}