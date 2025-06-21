package com.example.financeapp.db.budget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.MainApplication
import com.example.financeapp.utils.BudgetCalculationUtil
import com.example.financeapp.ui.theme.ColorPalette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Calendar
import java.util.Date

class BudgetViewModel : ViewModel() {
    val budgetDao = MainApplication.budgetDatabase.getBudgetDao()
    private val transactionDao = MainApplication.budgetDatabase.getTransactionDao()
    val budgetlist: LiveData<List<Budgetlist>> = budgetDao.getAllBudget()
    val monthlyBudgets: LiveData<List<Budgetlist>> = budgetDao.getBudgetsByType("Monthly")
    val yearlyBudgets: LiveData<List<Budgetlist>> = budgetDao.getBudgetsByType("Yearly")
    val customBudgets: LiveData<List<Budgetlist>> = budgetDao.getBudgetsByType("Custom")
    val reversedBudgetList: MediatorLiveData<List<Budgetlist>> = MediatorLiveData<List<Budgetlist>>().apply {
        addSource(budgetlist) { originalList ->
            value = originalList?.reversed()
        }
    }

    init {
        recalculateAllBudgetSpentAmounts()
    }

    fun recalculateAllBudgetSpentAmounts() {
        viewModelScope.launch {
            val allBudgetsFromDb = budgetDao.getAllBudgetsList()
            if (allBudgetsFromDb.isEmpty()) return@launch

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
    }

    fun addBudget(
        budgetName: String,
        budgetLimit: Double,
        budgetType: String,
        customStartDate: Long? = null,
        customEndDate: Long? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val color = ColorPalette.getOutgoingColor(budgetName)
            val calendar = Calendar.getInstance()
            var effectiveStartDate: Long? = customStartDate
            var effectiveEndDate: Long? = customEndDate

            when (budgetType) {
                "Monthly" -> {
                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    effectiveStartDate = calendar.timeInMillis
                    calendar.add(Calendar.MONTH, 1)
                    calendar.add(Calendar.MILLISECOND, -1)
                    effectiveEndDate = calendar.timeInMillis
                }
                "Yearly" -> {
                    calendar.set(Calendar.DAY_OF_YEAR, 1)
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    effectiveStartDate = calendar.timeInMillis
                    calendar.add(Calendar.YEAR, 1)
                    calendar.add(Calendar.MILLISECOND, -1)
                    effectiveEndDate = calendar.timeInMillis
                }
            }

            val newBudget = Budgetlist(
                budgetName = budgetName,
                budgetLimit = budgetLimit,
                budgetCreatedAt = Date.from(Instant.now()),
                budgetColor = color,
                budgetType = budgetType,
                startDate = effectiveStartDate,
                endDate = effectiveEndDate,
                budgetSpent = 0.0,
                budgetRemaining = budgetLimit
            )

            budgetDao.addBudget(newBudget)
            recalculateAllBudgetSpentAmounts()
        }
    }

    fun deleteBudget(budgetId: Int) {
        viewModelScope.launch {
            budgetDao.deleteBudget(budgetId)
            recalculateAllBudgetSpentAmounts()
        }
    }

    fun updateBudget(budgetFromDialog: Budgetlist) {

        viewModelScope.launch(Dispatchers.IO) {
            val updatedColor = ColorPalette.getOutgoingColor(budgetFromDialog.budgetName)
            var effectiveStartDate = budgetFromDialog.startDate
            var effectiveEndDate = budgetFromDialog.endDate
            val calendar = Calendar.getInstance()

            when (budgetFromDialog.budgetType) {
                "Monthly" -> {
                    calendar.timeInMillis = budgetFromDialog.budgetCreatedAt.time
                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                    effectiveStartDate = calendar.timeInMillis
                    calendar.add(Calendar.MONTH, 1)
                    calendar.add(Calendar.MILLISECOND, -1)
                    effectiveEndDate = calendar.timeInMillis
                }
                "Yearly" -> {
                    calendar.timeInMillis = budgetFromDialog.budgetCreatedAt.time
                    calendar.set(Calendar.DAY_OF_YEAR, 1)
                    effectiveStartDate = calendar.timeInMillis
                    calendar.add(Calendar.YEAR, 1)
                    calendar.add(Calendar.MILLISECOND, -1)
                    effectiveEndDate = calendar.timeInMillis
                }
                "Custom" -> {
                    effectiveStartDate = budgetFromDialog.startDate
                    effectiveEndDate = budgetFromDialog.endDate
                }
            }

            val budgetForCalculation = budgetFromDialog.copy(
                budgetName = budgetFromDialog.budgetName,
                budgetLimit = budgetFromDialog.budgetLimit,
                budgetType = budgetFromDialog.budgetType,
                startDate = effectiveStartDate,
                endDate = effectiveEndDate
            )
            val allTransactionsFromDb = transactionDao.getAllTransactionsList()
            val totalSpentForBudget = BudgetCalculationUtil.calculateSpentForSingleBudget(
                budgetForCalculation,
                allTransactionsFromDb
            )
            val budgetToActuallyPersist = budgetForCalculation.copy(
                budgetColor = updatedColor,
                budgetSpent = if (budgetFromDialog.budgetName.equals("Savings", ignoreCase = true)) {
                    budgetFromDialog.budgetSpent
                } else {
                    totalSpentForBudget
                },
                budgetRemaining = if (budgetFromDialog.budgetName.equals("Savings", ignoreCase = true)) {
                    budgetFromDialog.budgetLimit - budgetFromDialog.budgetSpent
                } else {
                    budgetFromDialog.budgetLimit - totalSpentForBudget
                }
            )

            budgetDao.updateBudget(budgetToActuallyPersist)
        }
    }
}