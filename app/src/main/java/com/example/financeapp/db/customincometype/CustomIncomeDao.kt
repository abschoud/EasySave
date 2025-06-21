package com.example.financeapp.db.customincometype

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CustomIncomeTypeNameDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCustomIncomeTypeName(customIncomeTypeName: CustomIncomeTypeName)

    @Query("SELECT * FROM CustomIncomeTypeName ORDER BY name ASC")
    fun getAllCustomIncomeTypeNames(): LiveData<List<CustomIncomeTypeName>>

    @Query("SELECT name FROM CustomIncomeTypeName ORDER BY name ASC")
    fun getAllCustomIncomeTypeNameStrings(): LiveData<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM CustomIncomeTypeName WHERE name = :name LIMIT 1)")
    suspend fun customIncomeTypeNameExists(name: String): Boolean

    @Delete
    suspend fun deleteCustomIncomeTypeName(customTypeName: CustomIncomeTypeName)

    @Query("DELETE FROM CustomIncomeTypeName WHERE name = :name")
    suspend fun deleteCustomIncomeTypeNameByName(name: String)
}