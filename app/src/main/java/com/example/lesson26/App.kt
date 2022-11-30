package com.example.lesson26

import android.app.Application
import com.example.lesson26.databases.DatabaseHelper
import com.example.lesson26.repositories.DataRepository
import java.text.SimpleDateFormat
import java.util.*

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        self = this

        val dbOpenHelper = DatabaseHelper(self)
        dataRepository = DataRepository(dbOpenHelper.writableDatabase)
    }

    companion object {
        private lateinit var self: App

        private val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

        private lateinit var dataRepository: DataRepository

        fun getInstanceApp(): App {
            return self
        }

        fun getDateFormat(): SimpleDateFormat {
            return dateFormat
        }

        fun getDataRepository(): DataRepository {
            return dataRepository
        }
    }
}