package com.example.financeapp.db.customoutgoingtype

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CustomOutgoingTypeName(
    @PrimaryKey
    val name: String
)