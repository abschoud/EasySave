package com.example.financeapp.db.customoutgoingtype

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.financeapp.MainApplication
import com.example.financeapp.utils.predeterminedOutgoingTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CustomOutgoingViewModel : ViewModel() {
    private val customDao: CustomOutgoingTypeNameDao = MainApplication.budgetDatabase.getCustomOutgoingDao()
    private val budgetDao = MainApplication.budgetDatabase.getBudgetDao()
    val customOutgoingTypeNames: LiveData<List<String>> = customDao.getAllCustomOutgoingTypeNameStrings()
    val predeterminedTypes: List<String> = predeterminedOutgoingTypes

    suspend fun addCustomOutgoingTypeName(name: String): Boolean {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) return false
        if (predeterminedTypes.any { it.equals(trimmedName, ignoreCase = true) } ||
            customDao.customOutgoingTypeNameExists(trimmedName)) {
            return false
        }
        return try {
            customDao.addCustomOutgoingTypeName(CustomOutgoingTypeName(trimmedName))
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteCustomOutgoingTypeName(name: String): Boolean {
        val trimmedName = name.trim()

        if (trimmedName.isNotBlank() && !predeterminedTypes.any { it.equals(trimmedName, ignoreCase = true) }) {
            val isTypeUsedInBudgets = withContext(Dispatchers.IO) {
                budgetDao.isBudgetNameUsed(trimmedName)
            }
            if (isTypeUsedInBudgets) {
                return false
            }

            customDao.deleteCustomOutgoingTypeNameByName(trimmedName)
            return true
        }
        return false
    }
}