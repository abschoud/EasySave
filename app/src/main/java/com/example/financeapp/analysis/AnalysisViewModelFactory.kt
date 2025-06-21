package com.example.financeapp.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.financeapp.network.FinanceApiService

class AnalysisViewModelFactory(
    private val financeApiService: FinanceApiService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnalysisViewModel::class.java)) {
            return AnalysisViewModel(financeApiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}