package com.example.monito_theexpensemanager

import android.app.Application

class MonitoApp: Application() {
    val database by lazy { ExpenseDatabase.getDatabase(this) }
    val repository by lazy { ExpenseRepository(database.expenseDao())}
}