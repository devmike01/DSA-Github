package dev.gbenga.dsagithub.nav

import dev.gbenga.dsagithub.features.home.data.User
import kotlinx.serialization.Serializable


@Serializable
data object Home: Screen


@Serializable
data class GithubDetails(val userData: User? = null): Screen
