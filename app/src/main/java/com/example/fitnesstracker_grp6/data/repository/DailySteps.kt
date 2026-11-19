package com.example.fitnesstracker_grp6.data.repository

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailySteps(context: Context, private val userId: Long) {
    private val totals = context.getSharedPreferences("daily_steps", Context.MODE_PRIVATE)

    fun total(): Long = totals.getLong(todayKey(), 0)

    fun add(amount: Long): Long? {
        val key = todayKey()
        val current = totals.getLong(key, 0)
        if (amount <= 0 || amount > Long.MAX_VALUE - current) return null

        val updated = current + amount
        totals.edit().putLong(key, updated).apply()
        return updated
    }

    private fun todayKey(): String {
        val day = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date())
        return "${userId}_$day"
    }
}
