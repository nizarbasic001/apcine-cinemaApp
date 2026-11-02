package com.example.data.remote

import com.example.data.model.AuthResponse
import com.example.data.model.FavoriteInsert
import com.example.data.model.FavoriteItem
import com.example.data.model.Movie
import com.example.data.model.UserProfile
import com.example.data.model.WatchHistoryItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApi {

    @GET("rest/v1/movies")
    suspend fun getPublishedMovies(
        @Query("select") select: String = "*",
        @Query("published") published: String = "eq.true",
        @Query("order") order: String = "created_at.desc",
        @Query("limit") limit: Int = 50
    ): List<Movie>

    @GET("rest/v1/movies")
    suspend fun getFeaturedMovies(
        @Query("select") select: String = "*",
        @Query("published") published: String = "eq.true",
        @Query("featured") featured: String = "eq.true",
        @Query("order") order: String = "created_at.desc",
        @Query("limit") limit: Int = 10
    ): List<Movie>

    @GET("rest/v1/movies")
    suspend fun getMovieById(
        @Query("id") idFilter: String,
        @Query("select") select: String = "*"
    ): List<Movie>

    @GET("rest/v1/movies")
    suspend fun searchMovies(
        @Query("select") select: String = "*",
        @Query("published") published: String = "eq.true",
        @Query("title") titleFilter: String,
        @Query("limit") limit: Int = 30
    ): List<Movie>

    @GET("rest/v1/favorites")
    suspend fun getFavorites(
        @Header("Authorization") userAuthHeader: String,
        @Query("select") select: String = "*,movie:movies(*)",
        @Query("order") order: String = "created_at.desc"
    ): List<FavoriteItem>

    @Headers("Prefer: return=minimal")
    @POST("rest/v1/favorites")
    suspend fun addFavorite(
        @Header("Authorization") userAuthHeader: String,
        @Body item: FavoriteInsert
    ): Response<Unit>

    @Headers("Prefer: return=minimal")
    @DELETE("rest/v1/favorites")
    suspend fun removeFavorite(
        @Header("Authorization") userAuthHeader: String,
        @Query("movie_id") movieIdFilter: String
    ): Response<Unit>

    @GET("rest/v1/watch_history")
    suspend fun getWatchHistory(
        @Header("Authorization") userAuthHeader: String,
        @Query("select") select: String = "*,movie:movies(*)",
        @Query("order") order: String = "last_watched_at.desc"
    ): List<WatchHistoryItem>

    @POST("auth/v1/token?grant_type=password")
    suspend fun loginWithPassword(
        @Body credentials: Map<String, String>
    ): Response<AuthResponse>

    @POST("auth/v1/signup")
    suspend fun signUpWithPassword(
        @Body payload: Map<String, Any>
    ): Response<AuthResponse>

    @GET("rest/v1/profiles")
    suspend fun getProfile(
        @Header("Authorization") userAuthHeader: String,
        @Query("id") idFilter: String,
        @Query("select") select: String = "*"
    ): List<UserProfile>
}
