package dev.gbenga.dsagithub.features.home

import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsagithub.features.home.data.User
import dev.gbenga.dsagithub.network.NetworkHandler

interface HomeRepository {
    suspend fun getUsers(): Result<LinkedList<User>>
    suspend fun getUserById(id: String): Result<LinkedList<User>>
}

class HomeRepositoryImpl(private val network: NetworkHandler): HomeRepository{
    override suspend fun getUsers(): Result<LinkedList<User>> {
        return network.get<User, LinkedList<User>>("users")
    }

    override suspend fun getUserById(id: String): Result<LinkedList<User>> {
        TODO("Not yet implemented")
    }

}