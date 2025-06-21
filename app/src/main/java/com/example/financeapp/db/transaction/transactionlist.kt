package com.example.financeapp.db.transaction

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.financeapp.utils.AddType
import java.util.Date

@Entity
data class Transactionlist(
    @PrimaryKey(autoGenerate = true)
    var transactionId: Int = 0,
    var transactionCreatedAt: Date,
    var transactionName: String,
    var transactionValue: Double = 0.0,
    var totalTransactionValue: Double = 0.0,
    var transactionType: AddType,
    val transactionColor: Int,
    val budgetCategory: String? = null
)

class AddTypeConverter {
    @TypeConverter
    fun fromAddType(type: AddType): String {
        return type.name
    }

    @TypeConverter
    fun toAddType(name: String): AddType {
        return AddType.valueOf(name)
    }
}
