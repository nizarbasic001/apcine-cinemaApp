package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserProfile(
    @Json(name = "id") val id: String,
    @Json(name = "email") val email: String? = null,
    @Json(name = "display_name") val displayName: String? = null,
    @Json(name = "role") val role: String? = "user",
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class FavoriteItem(
    @Json(name = "id") val id: String? = null,
    @Json(name = "user_id") val userId: String? = null,
    @Json(name = "movie_id") val movieId: String,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "movie") val movie: Movie? = null
)

@JsonClass(generateAdapter = true)
data class FavoriteInsert(
    @Json(name = "movie_id") val movieId: String
)

@JsonClass(generateAdapter = true)
data class WatchHistoryItem(
    @Json(name = "movie_id") val movieId: String,
    @Json(name = "progress") val progress: Double = 0.0,
    @Json(name = "last_watched_at") val lastWatchedAt: String? = null,
    @Json(name = "movie") val movie: Movie? = null
)

@JsonClass(generateAdapter = true)
data class AuthResponse(
    @Json(name = "access_token") val accessToken: String? = null,
    @Json(name = "token_type") val tokenType: String? = null,
    @Json(name = "expires_in") val expiresIn: Long? = null,
    @Json(name = "refresh_token") val refreshToken: String? = null,
    @Json(name = "user") val user: AuthUser? = null
)

@JsonClass(generateAdapter = true)
data class AuthUser(
    @Json(name = "id") val id: String,
    @Json(name = "email") val email: String? = null,
    @Json(name = "user_metadata") val userMetadata: Map<String, String>? = null
)
