package com.example.financeapp.db.customincometype

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CustomIncomeTypeName(
    @PrimaryKey
    val name: String
)