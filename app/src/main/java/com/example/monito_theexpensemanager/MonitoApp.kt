package com.example.monito_theexpensemanager

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MonitoApp: Application()  {
    val database by lazy {  AppDatabase.getAppDatabase(this) }
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        applicationScope.launch {
            seedDefaultCategoriesIfEmpty()
        }
    }

    private suspend fun seedDefaultCategoriesIfEmpty() {
        val categoryDao = database.categoryDao()
        val existingCategories = categoryDao.getCategoryList().first()

        if(existingCategories.isEmpty()) {
            val defaultCategoryList = listOf<CategoryEntity>(
                CategoryEntity(type = TransactionType.EXPENSE , color = "#E57373", name = "Food"),
                CategoryEntity(type = TransactionType.EXPENSE, color = "#64B5F6", name = "Groceries"),
                CategoryEntity(type = TransactionType.EXPENSE, color = "#81C784", name = "Rent"),
                CategoryEntity(type = TransactionType.INCOME, color = "#FFB74D", name = "Salary")
            )

            defaultCategoryList.forEach { categoryDao.createCategory(it) }
        }
    }

}