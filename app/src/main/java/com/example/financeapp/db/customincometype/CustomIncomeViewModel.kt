package com.example.financeapp.db.customincometype

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.financeapp.MainApplication
import com.example.financeapp.utils.predeterminedIncomeTypes

class CustomIncomeViewModel : ViewModel() {
    private val customIncomeDao: CustomIncomeTypeNameDao = MainApplication.budgetDatabase.getCustomIncomeDao()
    val customIncomeTypeNames: LiveData<List<String>> = customIncomeDao.getAllCustomIncomeTypeNameStrings()
    val predeterminedTypes: List<String> = predeterminedIncomeTypes

    suspend fun addCustomIncomeTypeName(name: String): Boolean {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) return false
        if (predeterminedTypes.any { it.equals(trimmedName, ignoreCase = true) } ||
            customIncomeDao.customIncomeTypeNameExists(trimmedName)) {
            return false
        }
        return try {
            customIncomeDao.addCustomIncomeTypeName(CustomIncomeTypeName(trimmedName))
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteCustomIncomeTypeName(name: String): Boolean {
        val trimmedName = name.trim()

        if (trimmedName.isNotBlank() && !predeterminedTypes.any { it.equals(trimmedName, ignoreCase = true) }) {
            customIncomeDao.deleteCustomIncomeTypeNameByName(trimmedName)
            return true
        }
        return false
    }
}