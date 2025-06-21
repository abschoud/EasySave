package com.example.financeapp.data

import com.google.gson.annotations.SerializedName

data class AnalysisResponse(
    @SerializedName("analysis") val analysis: String
)