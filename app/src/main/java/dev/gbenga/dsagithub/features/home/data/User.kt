package dev.gbenga.dsagithub.features.home.data

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerializedName("login")
    val login: String,

    @SerializedName("id")
    val id: Int,

    @SerializedName("node_id")
    val nodeId: String,

    @SerializedName("avatar_url")
    val avatarUrl: String,

    @SerializedName("gravatar_id")
    val gravatarId: String,

    @SerializedName("url")
    val url: String,

    @SerializedName("html_url")
    val htmlUrl: String,

    @SerializedName("followers_url")
    val followersUrl: String,

    @SerializedName("following_url")
    val followingUrl: String,

    @SerializedName("gists_url")
    val gistsUrl: String,

    @SerializedName("starred_url")
    val starredUrl: String,

    @SerializedName("subscriptions_url")
    val subscriptionsUrl: String,

    @SerializedName("organizations_url")
    val organizationsUrl: String,

    @SerializedName("repos_url")
    val reposUrl: String,

    @SerializedName("events_url")
    val eventsUrl: String,

    @SerializedName("received_events_url")
    val receivedEventsUrl: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("user_view_type")
    val userViewType: String,

    @SerializedName("site_admin")
    val siteAdmin: Boolean
)
