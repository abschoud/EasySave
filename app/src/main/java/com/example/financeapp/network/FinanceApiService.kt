package com.example.financeapp.network

import com.example.financeapp.data.AnalysisResponse
import com.example.financeapp.data.DataRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface FinanceApiService {
    @POST("/api/analysis")
    suspend fun analyzeSpending(
        @Query("model") modelName: String,
        @Body dataRequest: DataRequest
    ): Response<AnalysisResponse>
}