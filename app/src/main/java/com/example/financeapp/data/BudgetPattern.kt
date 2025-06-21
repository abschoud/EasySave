package com.example.financeapp.data

data class BudgetPattern(
    val name: String,
    val limit: Double,
    val spent: Double,
    val remaining: Double,
    val period: BudgetPeriod
)

enum class BudgetPeriod {
    MONTHLY,
    YEARLY
}