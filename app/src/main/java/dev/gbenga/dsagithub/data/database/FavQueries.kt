package dev.gbenga.dsagithub.data.database

import android.provider.BaseColumns

object FavQueries {
    const val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${FavouriteContractReader.FavouriteEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${FavouriteContractReader.FavouriteEntry.COLUMN_USERNAME} TEXT," +
                "${FavouriteContractReader.FavouriteEntry.COLUMN_AVATAR_URL} TEXT)"

    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${FavouriteContractReader.FavouriteEntry.TABLE_NAME}"

   const val CHECK_EXISTENCE = "SELECT ${FavouriteContractReader.FavouriteEntry.COLUMN_USERNAME} " +
            "FROM ${FavouriteContractReader.FavouriteEntry.TABLE_NAME} WHERE ${FavouriteContractReader.FavouriteEntry.COLUMN_USERNAME} = ?"
}

object DatabaseConfig{
    const val DATABASE_NAME = "app_database"
    const val DATABASE_VERSION = 1
}