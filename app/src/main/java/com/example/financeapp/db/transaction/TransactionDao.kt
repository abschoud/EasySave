package com.example.financeapp.db.transaction

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.financeapp.utils.AddType
import java.util.Date

@Dao
interface TransactionDao {
    @Query("SELECT * FROM Transactionlist")
    fun getAllTransaction(): LiveData<List<Transactionlist>>

    @Query("SELECT * FROM Transactionlist")
    suspend fun getAllTransactionsList(): List<Transactionlist>

    @Insert
    suspend fun addTransaction(transactionValue: Transactionlist)

    @Query("DELETE FROM Transactionlist where transactionId = :id")
    suspend fun deleteTransaction(id: Int)

    @Query("SELECT * FROM Transactionlist WHERE transactionCreatedAt BETWEEN :startDate AND :endDate ORDER BY transactionCreatedAt DESC")
    fun getTransactionsForDate(startDate: Date, endDate: Date): LiveData<List<Transactionlist>>

    @Query("SELECT * FROM Transactionlist WHERE transactionType = :type ORDER BY transactionCreatedAt ASC")
    fun getTransactionsByType(type: AddType): LiveData<List<Transactionlist>>

    @Query("SELECT * FROM Transactionlist WHERE transactionType = :type AND DATE(transactionCreatedAt / 1000, 'unixepoch') = DATE(:date / 1000, 'unixepoch') ORDER BY transactionCreatedAt DESC")
    fun getTransactionsForDateAndType(date: Date, type: AddType): LiveData<List<Transactionlist>>

    @Update
    suspend fun updateTransaction(transactionlist: Transactionlist)

    @Query("SELECT EXISTS(SELECT 1 FROM Transactionlist WHERE transactionName = :transactionTypeName LIMIT 1)")
    suspend fun isTransactionTypeUsed(transactionTypeName: String): Boolean
}