package com.example.financeapp.db.budget

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface BudgetDao {
    @Query("SELECT * FROM Budgetlist")
    fun getAllBudget(): LiveData<List<Budgetlist>>

    @Query("SELECT * FROM Budgetlist")
    suspend fun getAllBudgetsList(): List<Budgetlist>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addBudget(budget: Budgetlist)

    @Query("DELETE FROM Budgetlist where budgetId = :id")
    suspend fun deleteBudget(id: Int)

    @Update
    suspend fun updateBudget(budget: Budgetlist)

    @Update
    suspend fun updateAllBudgets(budgets: List<Budgetlist>)

    @Query("SELECT * FROM Budgetlist WHERE budgetType = :type")
    fun getBudgetsByType(type: String): LiveData<List<Budgetlist>>

    @Query("SELECT EXISTS(SELECT 1 FROM Budgetlist WHERE budgetName = :budgetName LIMIT 1)")
    suspend fun isBudgetNameUsed(budgetName: String): Boolean
}