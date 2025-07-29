package dev.gbenga.dsagithub.nav

import dev.gbenga.dsagithub.features.home.data.User
import kotlinx.serialization.Serializable


data object Home: Screen {
    private fun readResolve(): Any = Home
}


@Serializable
data class GithubDetails(val userName: String? = null,
                         val avatarUrl: String? = null,
    val isFavourite: Boolean= false,
    ): Screen{

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = userName?.hashCode() ?: 0
        result = 31 * result
        + (avatarUrl?.hashCode() ?: 0)
        + (isFavourite.hashCode())
        return result
    }
}
