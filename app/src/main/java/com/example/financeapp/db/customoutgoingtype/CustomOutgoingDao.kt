package com.example.financeapp.db.customoutgoingtype

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CustomOutgoingTypeNameDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCustomOutgoingTypeName(customOutgoingTypeName: CustomOutgoingTypeName)

    @Query("SELECT * FROM CustomOutgoingTypeName ORDER BY name ASC")
    fun getAllCustomOutgoingTypeNames(): LiveData<List<CustomOutgoingTypeName>>

    @Query("SELECT name FROM CustomOutgoingTypeName ORDER BY name ASC")
    fun getAllCustomOutgoingTypeNameStrings(): LiveData<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM CustomOutgoingTypeName WHERE name = :name LIMIT 1)")
    suspend fun customOutgoingTypeNameExists(name: String): Boolean

    @Delete
    suspend fun deleteCustomOutgoingTypeName(customTypeName: CustomOutgoingTypeName)

    @Query("DELETE FROM CustomOutgoingTypeName WHERE name = :name")
    suspend fun deleteCustomOutgoingTypeNameByName(name: String)
}