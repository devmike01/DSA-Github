package dev.gbenga.dsagithub.nav

import kotlinx.serialization.Serializable


@Serializable
data object Home: Screen


@Serializable
data class GithubDetails(val accountId: String=""): Screen