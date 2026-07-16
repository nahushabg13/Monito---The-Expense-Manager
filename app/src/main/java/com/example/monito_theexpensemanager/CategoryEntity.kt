    package com.example.monito_theexpensemanager

    import androidx.room.ColumnInfo
    import androidx.room.Entity
    import androidx.room.PrimaryKey

    @Entity(
        tableName = "categories",

    )
    data class CategoryEntity(
        @PrimaryKey(autoGenerate = true)
        val id:Int = 0,
        @ColumnInfo(name= "categoryType")
        val type: String,
        @ColumnInfo(name = "color")
        val color: String = "#9E9E9E",
        @ColumnInfo(name = "categoryName")
        val name: String

    )
