package com.example.financeapp.data

import com.google.gson.annotations.SerializedName
import java.util.Date

data class SpendingEntry(
    @SerializedName("category") val category: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("date") val date: Date,
    @SerializedName("type") val type: String
)

data class BudgetEntry(
    @SerializedName("category") val category: String,
    @SerializedName("limit") val limit: Double,
    @SerializedName("spent") val spent: Double,
    @SerializedName("period") val period: String,
    @SerializedName("startDate") val startDate: Date,
    @SerializedName("endDate") val endDate: Date
)

data class DataRequest(
    @SerializedName("spendingData") val spendingData: List<SpendingEntry>,
    @SerializedName("budgetData") val budgetData: List<BudgetEntry>? = null
)