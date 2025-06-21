package com.example.financeapp.utils

import com.example.financeapp.db.budget.Budgetlist
import com.example.financeapp.db.transaction.Transactionlist

object BudgetCalculationUtil {
    fun calculateSpentForSingleBudget(
        budget: Budgetlist,
        allTransactions: List<Transactionlist>
    ): Double {
        if (budget.budgetLimit <= 0 || budget.startDate == null || budget.endDate == null) {
            return 0.0
        }

        return allTransactions.filter { transaction ->
            if (transaction.transactionType != AddType.OUTGOING) {
                return@filter false
            }

            val categoryMatches = transaction.budgetCategory != null &&
                    transaction.budgetCategory.equals(budget.budgetName, ignoreCase = true)

            if (!categoryMatches) {
                return@filter false
            }

            val transactionTime = transaction.transactionCreatedAt.time
            val isWithinPeriod = transactionTime >= budget.startDate!! && transactionTime <= budget.endDate!!

            isWithinPeriod
        }.sumOf { it.transactionValue }
    }
}