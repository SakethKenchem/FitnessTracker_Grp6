package com.example.fitnesstracker_grp6.ui.meal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnesstracker_grp6.R
import com.example.fitnesstracker_grp6.data.local.Meal

class MealAdapter(
    private val onEdit: (Meal) -> Unit,
    private val onDelete: (Meal) -> Unit
) : ListAdapter<Meal, MealAdapter.MealViewHolder>(DiffCallback) {

    class MealViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvMealName)
        val details: TextView = view.findViewById(R.id.tvMealDetails)
        val calories: TextView = view.findViewById(R.id.tvMealCalories)
        val delete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meal, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = getItem(position)
        holder.name.text = meal.name
        holder.details.text =
            "${meal.mealType} • P ${meal.protein}g  C ${meal.carbs}g  F ${meal.fat}g"
        holder.calories.text = "${meal.calories} kcal"
        holder.itemView.setOnClickListener { onEdit(meal) }
        holder.delete.setOnClickListener { onDelete(meal) }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Meal>() {
        override fun areItemsTheSame(old: Meal, new: Meal) = old.id == new.id
        override fun areContentsTheSame(old: Meal, new: Meal) = old == new
    }
}