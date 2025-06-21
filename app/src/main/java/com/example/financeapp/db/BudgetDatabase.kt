package com.example.financeapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.financeapp.db.budget.BudgetDao
import com.example.financeapp.db.budget.Budgetlist
import com.example.financeapp.db.customincometype.CustomIncomeTypeName
import com.example.financeapp.db.customincometype.CustomIncomeTypeNameDao
import com.example.financeapp.db.customoutgoingtype.CustomOutgoingTypeName
import com.example.financeapp.db.customoutgoingtype.CustomOutgoingTypeNameDao
import com.example.financeapp.db.transaction.AddTypeConverter
import com.example.financeapp.db.transaction.TransactionDao
import com.example.financeapp.db.transaction.Transactionlist


@Database(entities = [Budgetlist::class, Transactionlist::class, CustomOutgoingTypeName::class, CustomIncomeTypeName::class], version = 24, exportSchema = false)
@TypeConverters(Converters::class, AddTypeConverter::class)
abstract class BudgetDatabase : RoomDatabase(){

    companion object {
        const val NAME = "Budget_DB"
    }

    abstract fun getBudgetDao() : BudgetDao
    abstract fun getTransactionDao() : TransactionDao
    abstract fun getCustomOutgoingDao() : CustomOutgoingTypeNameDao
    abstract fun getCustomIncomeDao() : CustomIncomeTypeNameDao
}