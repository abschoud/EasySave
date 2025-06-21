package com.example.financeapp

import android.app.Application
import androidx.room.Room
import com.example.financeapp.db.BudgetDatabase

class MainApplication : Application() {

    companion object {
        lateinit var budgetDatabase: BudgetDatabase
    }

    override fun onCreate() {
        super.onCreate()
        budgetDatabase = Room.databaseBuilder(
            applicationContext,
            BudgetDatabase::class.java,
            BudgetDatabase.NAME
        ).build()
    }
}