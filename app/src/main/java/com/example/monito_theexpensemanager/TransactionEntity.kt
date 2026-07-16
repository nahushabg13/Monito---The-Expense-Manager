package com.example.monito_theexpensemanager

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT

        )
    ],
    indices = [Index("categoryId")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    @ColumnInfo(name = "categoryId")
    val categoryId:Int,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "note")
    val note:String,
    @ColumnInfo(name = "date")
    val date: String
)
