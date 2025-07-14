package dev.gbenga.dsagithub.features.details

import dev.gbenga.dsa.collections.list.LinkedList
import dev.gbenga.dsagithub.network.NetworkHandler


interface DetailRepository{
    suspend fun getUserRepos(user: String): Result<LinkedList<UserRepositories>>

    suspend fun getUserRepoById(user: String, nodeId: String): Result<UserRepositories>
}

class DetailRepositoryImpl(val networkHandler: NetworkHandler): DetailRepository {

    private var repositories : LinkedList<UserRepositories>? = null

    override suspend fun getUserRepos(user: String): Result<LinkedList<UserRepositories>> {
        // users/devmike01/repos
        return networkHandler.get<UserRepositories,
                LinkedList<UserRepositories>>("users/${user}/repos").also {
            it.getOrNull().let { repos ->
                repositories = repos
            }
        }
    }

    override suspend fun getUserRepoById(
        user: String,
        nodeId: String,
    ): Result<UserRepositories> {
        return Result.failure(Exception(""))// repositories?.search()
    }


}