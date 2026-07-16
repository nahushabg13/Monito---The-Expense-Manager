package com.example.monito_theexpensemanager

import android.app.Application

class MonitoApp: Application()  {
    val database by lazy {  AppDatabase.getAppDatabase(this) }
}