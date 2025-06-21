package com.example.financeapp.ui.components.pbs

import com.example.financeapp.utils.AddType

sealed interface BottomSheetActions {
    data class AddBudget(
        val budgetName: String,
        val budgetLimit: Double,
        val budgetType: String,
        val startDate: Long? = null,
        val endDate: Long? = null
    ) : BottomSheetActions

    data class AddTransaction(
        val transactionName: String,
        val transactionValue: Double,
        val budgetCategory: String?,
        val transactionType: AddType
    ) : BottomSheetActions
}