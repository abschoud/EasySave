package com.example.financeapp.db.budget

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Budgetlist(
    @PrimaryKey(autoGenerate = true)
    var budgetId : Int = 0,
    var budgetCreatedAt : Date,
    var budgetName : String,
    var budgetLimit : Double = 0.0,
    var budgetSpent : Double = 0.0,
    var budgetRemaining : Double = 0.0,
    var budgetComplete : Boolean = false,
    var budgetColor : Int,
    var budgetType: String,
    val startDate: Long? = null,
    val endDate: Long? = null
)