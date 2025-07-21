package dev.gbenga.dsagithub.data.database

import org.koin.dsl.module

val databaseModule = module {
    single { AppDbHelper(get()) }
    single <FavouriteDatabase> {
        val dbHelper = get<AppDbHelper>()
        FavouriteDatabaseImpl(dbHelper.writableDatabase, dbHelper.readableDatabase)
    }
}