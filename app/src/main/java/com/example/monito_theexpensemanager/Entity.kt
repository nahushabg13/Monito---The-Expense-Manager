package com.example.monito_theexpensemanager

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int =0,
    @ColumnInfo(name = "date")
    val date:String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "category")
    val category:String,
    @ColumnInfo(name = "amount")
    val amount: Double
)