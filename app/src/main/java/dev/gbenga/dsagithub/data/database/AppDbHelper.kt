package dev.gbenga.dsagithub.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDbHelper(context: Context) : SQLiteOpenHelper(context, DatabaseConfig.DATABASE_NAME, null, DatabaseConfig.DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(Queries.SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int,
    ) {
        db?.execSQL(Queries.SQL_DELETE_ENTRIES)
        onCreate(db)
    }

}