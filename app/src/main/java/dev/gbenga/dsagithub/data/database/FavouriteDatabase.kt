package dev.gbenga.dsagithub.data.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.util.Log
import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsa.collections.list.LinkedListImpl
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

interface FavouriteDatabase {
    suspend fun getFavourites(): SharedFlow<LinkedList<Favourite>>
    suspend fun setFavourite(favourite: Favourite): Long?
    suspend fun delete(username: String): Int?
}

class FavouriteDatabaseImpl(private val dbWrite: SQLiteDatabase?,
    private val dbRead : SQLiteDatabase): FavouriteDatabase{

    private val _state : MutableSharedFlow<LinkedList<Favourite>> = MutableSharedFlow(
        replay = 1,
    )
    //val state : SharedFlow<LinkedList<Favourite>> = _state.asSharedFlow()

    private val linkedList = LinkedListImpl<Favourite>()

    override suspend fun getFavourites(): SharedFlow<LinkedList<Favourite>> {
       // How you want the results sorted in the resulting Cursor
        val sortOrder = "${FavouriteContractReader.FavouriteEntry.COLUMN_USERNAME} DESC"

        val projection = arrayOf(BaseColumns._ID,
            FavouriteContractReader.FavouriteEntry.COLUMN_USERNAME,
            FavouriteContractReader.FavouriteEntry.COLUMN_AVATAR_URL)
        val cursor = dbRead.query(FavouriteContractReader.FavouriteEntry.TABLE_NAME,
            projection, null, null, null, null, sortOrder
            )

        linkedList.clear()
        with(cursor){
            while (moveToNext()){
                val favourite = Favourite(
                    id = getLong(getColumnIndexOrThrow(BaseColumns._ID)),
                    userName = getString(getColumnIndexOrThrow(FavouriteContractReader.FavouriteEntry.COLUMN_USERNAME)),
                    avatarUrl = getString(getColumnIndexOrThrow(FavouriteContractReader.FavouriteEntry.COLUMN_AVATAR_URL)),
                    )
                linkedList.append(favourite)
            }
        }
        cursor.close()

        Log.d("loadFavourite", "--A> MenuItem: $linkedList")
        _state.emit(linkedList)
        return _state.asSharedFlow()
    }

    override suspend fun setFavourite(favourite: Favourite): Long? {
        val values = ContentValues().apply {
            put(FavouriteContractReader.FavouriteEntry.COLUMN_USERNAME, favourite.userName)
            put(FavouriteContractReader.FavouriteEntry.COLUMN_AVATAR_URL, favourite.avatarUrl)
        }
        return dbWrite?.insert(FavouriteContractReader.FavouriteEntry.TABLE_NAME,
            null, values)?.also { rowId ->
            linkedList.append(favourite.copy(id = rowId))
            _state.tryEmit(linkedList)
        }
    }

    override suspend fun delete(username: String): Int? {
        val whereClause = "${FavouriteContractReader.FavouriteEntry.COLUMN_USERNAME} = ?"
        val whereArgs = arrayOf(username)
        return dbWrite?.delete(FavouriteContractReader.FavouriteEntry.TABLE_NAME, whereClause, whereArgs)
    }

}