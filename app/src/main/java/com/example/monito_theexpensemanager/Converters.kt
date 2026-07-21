package com.example.monito_theexpensemanager

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun convertTransactionTypeToRoomString(transactionType: TransactionType): String {
        return transactionType.name
    }


    @TypeConverter
    fun convertStringToTransactionType(transactionType: String): TransactionType {
        return TransactionType.valueOf(transactionType)
    }
}