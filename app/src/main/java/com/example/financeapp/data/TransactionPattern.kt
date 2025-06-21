package com.example.financeapp.data

import com.example.financeapp.utils.AddType

data class TransactionPattern(
    val name: String,
    val type: AddType,
    val frequency: Frequency,
    val valueRange: Pair<Double, Double>,
    val dayOfMonth: Int? = null,
    val dayOfWeek: Int? = null,
    val irregularityDays: Int? = null
)

enum class Frequency {
    MONTHLY,
    WEEKLY,
    DAILY_WEEKDAYS,
    IRREGULAR
}