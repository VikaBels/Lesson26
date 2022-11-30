package com.example.lesson26.databases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(
    context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DB_VERSION) {
    companion object {
        private const val DATABASE_NAME = "Lesson_26.db"
        const val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        for (i in 1..DB_VERSION) {
            migrate(db, i)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        var oldVersionHelper = oldVersion

        while (oldVersionHelper < newVersion) {
            oldVersionHelper += 1
            migrate(db, oldVersionHelper)
        }
    }

    private fun migrate(db: SQLiteDatabase, dbVersion: Int) {
        when (dbVersion) {
            1 -> {
                createTableUser(db)
                createTableNotification(db)
                createTableTrack(db)
                createTablePoint(db)
            }
        }
    }

    private fun createTableUser(db: SQLiteDatabase) {
        executeMethod(
            db, "CREATE TABLE user" +
                    "(_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL, " +
                    "last_name TEXT NOT NULL," +
                    "email TEXT NOT NULL," +
                    "password TEXT NOT NULL," +
                    "token TEXT NOT NULL );"
        )
    }

    private fun createTableNotification(db: SQLiteDatabase) {
        executeMethod(
            db, "CREATE TABLE notification" +
                    "(_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "time DOUBLE NOT NULL, " +
                    "text TEXT NOT NULL," +
                    "user_token TEXT NOT NULL );"
        )
    }

    private fun createTableTrack(db: SQLiteDatabase) {
        executeMethod(
            db, "CREATE TABLE track" +
                    "(_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "begins_at LONG NOT NULL, " +
                    "time DOUBLE NOT NULL," +
                    "distance DOUBLE NOT NULL," +
                    "user_token TEXT NOT NULL );"
        )
    }

    private fun createTablePoint(db: SQLiteDatabase) {
        executeMethod(
            db, "CREATE TABLE point" +
                    "(_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "lng DOUBLE NOT NULL," +
                    "lat DOUBLE NOT NULL, " +
                    "track_id INTEGER NOT NULL," +
                    "user_token TEXT NOT NULL );"
        )
    }

    private fun executeMethod(db: SQLiteDatabase, sqlCreate: String) {
        db.execSQL(sqlCreate)
    }
}