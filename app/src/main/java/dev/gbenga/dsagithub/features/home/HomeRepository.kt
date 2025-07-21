package dev.gbenga.dsagithub.features.home

import android.util.Log
import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsagithub.data.database.Favourite
import dev.gbenga.dsagithub.data.database.FavouriteDatabase
import dev.gbenga.dsagithub.features.home.data.User
import dev.gbenga.dsagithub.network.NetworkHandler
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

interface HomeRepository {
    suspend fun getUsers(): Result<LinkedList<User>>
    suspend fun getUserById(id: String): Result<LinkedList<User>>
    suspend fun addToFavourites(favourite: Favourite): Result<String>
    fun getFavourites(): Flow<LinkedList<Favourite>>
}

class HomeRepositoryImpl(private val network: NetworkHandler,
                         val favDatabase: FavouriteDatabase,
                         val ioScope: CoroutineScope
): HomeRepository{

    companion object{
        const val TAG = "HomeRepository"
    }

    override suspend fun getUsers(): Result<LinkedList<User>> {
        return network.get<User, LinkedList<User>>("users")
    }

    override suspend fun getUserById(id: String): Result<LinkedList<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun addToFavourites(favourite: Favourite): Result<String> {
        val deferred = CompletableDeferred<Result<String>>()
        ioScope.launch {
            favDatabase.setFavourite(favourite).let {
                deferred.complete(if((it ?: -1) > 0) Result.success("Successfully added to favourite")
                else Result.failure(Exception("Failed to add to favourite users")))
            }
        }
        return deferred.await()
    }

    override fun getFavourites(): Flow<LinkedList<Favourite>> {
        val channel = Channel<LinkedList<Favourite>>(Channel.CONFLATED)
        ioScope.launch {
            favDatabase.getFavourites().collect {
                Log.d("loadFavourite", "--> MenuItem: $it")
                channel.send(it)
            }
        }
        return channel.receiveAsFlow()
    }

}