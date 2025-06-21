package com.example.financeapp.db.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.MainApplication
import com.example.financeapp.utils.AddType
import com.example.financeapp.utils.BudgetCalculationUtil
import com.example.financeapp.ui.theme.ColorPalette
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Calendar
import java.util.Date

class TransactionViewModel : ViewModel() {
    val transactionDao = MainApplication.budgetDatabase.getTransactionDao()
    private val budgetDao = MainApplication.budgetDatabase.getBudgetDao()
    val transactionlist: LiveData<List<Transactionlist>> = transactionDao.getAllTransaction()
    val reversedTransactionlist: MediatorLiveData<List<Transactionlist>> = MediatorLiveData<List<Transactionlist>>().apply {
        addSource(transactionlist) { originalList ->
            value = originalList?.reversed()
        }
    }

    private suspend fun recalculateAffectedBudgets() {
        val allBudgetsFromDb = budgetDao.getAllBudgetsList()
        if (allBudgetsFromDb.isEmpty()) return

        val allTransactionsFromDb = transactionDao.getAllTransactionsList()
        val updatedBudgets = allBudgetsFromDb.map { budget ->
            if (budget.budgetName.equals("Savings", ignoreCase = true)) {
                budget.copy(
                    budgetRemaining = budget.budgetLimit - budget.budgetSpent
                )
            } else {
                val totalSpentForBudget = BudgetCalculationUtil.calculateSpentForSingleBudget(
                    budget,
                    allTransactionsFromDb
                )
                budget.copy(
                    budgetSpent = totalSpentForBudget,
                    budgetRemaining = budget.budgetLimit - totalSpentForBudget
                )
            }
        }

        if (updatedBudgets.isNotEmpty()) {
            budgetDao.updateAllBudgets(updatedBudgets)
        }
    }

    fun addTransaction(transactionName: String, transactionValue: Double, type: AddType, budgetCategoryForTransaction: String?) {
        viewModelScope.launch {
            val color = ColorPalette.getOutgoingColor(transactionName)
            val newTransaction = Transactionlist(
                transactionName = transactionName,
                transactionValue = transactionValue,
                transactionCreatedAt = Date.from(Instant.now()),
                transactionType = type,
                transactionColor = color,
                budgetCategory = budgetCategoryForTransaction
            )

            transactionDao.addTransaction(newTransaction)

            if (type == AddType.OUTGOING && budgetCategoryForTransaction != null) {
                recalculateAffectedBudgets()
            }
        }
    }

    fun deleteTransaction(transactionId: Int) {
        viewModelScope.launch {
            transactionDao.deleteTransaction(transactionId)
            recalculateAffectedBudgets()
        }
    }

    fun updateTransaction(transactionFromDialog: Transactionlist) {
        viewModelScope.launch {
            val transactionToPersist = transactionFromDialog.copy(
                transactionColor = ColorPalette.getOutgoingColor(transactionFromDialog.transactionName)
            )

            transactionDao.updateTransaction(transactionToPersist)
            recalculateAffectedBudgets()
        }
    }

    fun getTransactionsForDate(date: Date): LiveData<List<Transactionlist>> {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startDate = calendar.time

        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endDate = calendar.time

        return transactionDao.getTransactionsForDate(startDate, endDate)
    }
}