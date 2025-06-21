package com.example.financeapp.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

object ColorPalette {
    val incomeColors = mapOf(
        "Salary" to Color(0xFF2196F3).toArgb(),
        "Freelance" to Color(0xFF4CAF50).toArgb(),
        "Bonus" to Color(0xFF007CA2).toArgb(),
        "Gift" to Color(0xFF94A200).toArgb()
    )

    val outgoingColors = mapOf(
        "Bills" to Color(0xFF673AB7).toArgb(),
        "Rent" to Color(0xFF009688).toArgb(),
        "Food" to Color(0xFF9CCC65).toArgb(),
        "Transportation" to Color(0xFFF53C02).toArgb(),
        "Entertainment" to Color(0xFF9C27B0).toArgb(),
        "Shopping" to Color(0xFF03A9F4).toArgb(),
        "Health" to Color(0xFFE91E63).toArgb(),
        "Education" to Color(0xFF607D8B).toArgb(),
        "Car" to Color(0xFF4CAF50).toArgb(),
        "Clothing" to Color(0xFF8D5708).toArgb(),
        "Restaurant" to Color(0xFFCC170A).toArgb(),
        "Savings" to Color(0xFF795548).toArgb()
    )

    fun getIncomeColor(budgetName: String): Int {
        return incomeColors[budgetName] ?: Color(0xFFFF4500).toArgb()
    }

    fun getOutgoingColor(transactionName: String): Int {
        return outgoingColors[transactionName] ?: Color(0xFFFF4500).toArgb()
    }
}