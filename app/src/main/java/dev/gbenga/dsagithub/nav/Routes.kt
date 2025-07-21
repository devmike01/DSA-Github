package dev.gbenga.dsagithub.nav

import dev.gbenga.dsagithub.features.home.data.User
import kotlinx.serialization.Serializable


@Serializable
data object Home: Screen


@Serializable
data class GithubDetails(val userName: String? = null, val avatarUrl: String? = null): Screen{

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = userName?.hashCode() ?: 0
        result = 31 * result + (avatarUrl?.hashCode() ?: 0)
        return result
    }
}
