package dev.gbenga.dsagithub.data.database;

import android.provider.BaseColumns

object FavouriteContractReader {

    object FavouriteEntry : BaseColumns{
        const val TABLE_NAME = "favorites"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_AVATAR_URL = "avatar_url"
    }
}
