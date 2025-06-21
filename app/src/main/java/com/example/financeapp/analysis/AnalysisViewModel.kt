package com.example.financeapp.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.network.FinanceApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.financeapp.data.DataRequest
import retrofit2.HttpException
import java.io.IOException

class AnalysisViewModel(
    private val financeApiService: FinanceApiService
) : ViewModel() {

    private val _aiAnalysis = MutableStateFlow<String?>(null)
    val aiAnalysis: StateFlow<String?> = _aiAnalysis

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun analyzeSpending(modelName: String, dataRequest: DataRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _aiAnalysis.value = null

            try {
                val response = financeApiService.analyzeSpending(modelName, dataRequest)

                if (response.isSuccessful) {
                    _aiAnalysis.value = response.body()?.analysis
                } else {
                    _errorMessage.value = "API Error: ${response.code()} - ${response.message()}"
                }
            } catch (e: HttpException) {
                _errorMessage.value = "Network Request Error: ${e.code()} - ${e.message()}"
            } catch (e: IOException) {
                _errorMessage.value = "Network Connection Error: ${e.message}"
            } catch (e: Exception) {
                _errorMessage.value = "An Unexpected Error Occurred: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}