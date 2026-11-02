package com.example.data.repository

import android.content.Context
import com.example.data.model.FavoriteInsert
import com.example.data.model.Movie
import com.example.data.remote.SupabaseApi
import com.example.data.remote.SupabaseClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class MovieRepository(
    private val context: Context,
    private val api: SupabaseApi = SupabaseClient.api
) {
    private val sharedPrefs = context.getSharedPreferences("cinestream_prefs", Context.MODE_PRIVATE)

    private val _allMovies = MutableStateFlow<List<Movie>>(emptyList())
    val allMovies: StateFlow<List<Movie>> = _allMovies.asStateFlow()

    private val _localFavoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val localFavoriteIds: StateFlow<Set<String>> = _localFavoriteIds.asStateFlow()

    init {
        val saved = sharedPrefs.getStringSet("favorite_ids", emptySet()) ?: emptySet()
        _localFavoriteIds.value = saved
    }

    suspend fun loadMovies(): Result<List<Movie>> = withContext(Dispatchers.IO) {
        try {
            val movies = api.getPublishedMovies(limit = 60)
            _allMovies.value = movies
            Result.success(movies)
        } catch (e: Exception) {
            e.printStackTrace()
            // If already cached, return existing
            if (_allMovies.value.isNotEmpty()) {
                Result.success(_allMovies.value)
            } else {
                Result.failure(e)
            }
        }
    }

    fun getFeatured(): Movie? {
        val list = _allMovies.value
        return list.firstOrNull { it.featured == true }
            ?: list.maxByOrNull { it.rating ?: 0.0 }
            ?: list.firstOrNull()
    }

    fun getTrending(): List<Movie> {
        val list = _allMovies.value
        return list.sortedByDescending { it.rating ?: 0.0 }.take(15)
    }

    fun getLatest(): List<Movie> {
        val list = _allMovies.value
        return list.sortedByDescending { it.year ?: 0 }.take(15)
    }

    fun getPopular(): List<Movie> {
        val list = _allMovies.value
        return list.filter { (it.rating ?: 0.0) >= 6.0 }.take(15)
    }

    fun getSeriesOrShows(): List<Movie> {
        val list = _allMovies.value
        val series = list.filter { movie ->
            val g = movie.genres?.map { it.lowercase() } ?: emptyList()
            g.any { it.contains("tv") || it.contains("series") || it.contains("animation") || it.contains("sci-fi") }
        }
        return if (series.isNotEmpty()) series.take(15) else list.takeLast(10)
    }

    fun getRecommended(): List<Movie> {
        val list = _allMovies.value
        return list.shuffled().take(15)
    }

    fun getAllGenres(): List<String> {
        val genresSet = mutableSetOf<String>()
        _allMovies.value.forEach { movie ->
            movie.genres?.forEach { g ->
                if (g.isNotBlank()) genresSet.add(g.trim())
            }
        }
        return genresSet.sorted()
    }

    fun getMoviesByGenre(genre: String): List<Movie> {
        return _allMovies.value.filter { movie ->
            movie.genres?.any { it.equals(genre, ignoreCase = true) } == true
        }
    }

    fun getMovieById(id: String): Movie? {
        return _allMovies.value.firstOrNull { it.id == id }
    }

    fun getSimilarMovies(movie: Movie): List<Movie> {
        val movieGenres = movie.genres?.toSet() ?: emptySet()
        return _allMovies.value
            .filter { it.id != movie.id }
            .sortedByDescending { candidate ->
                candidate.genres?.count { movieGenres.contains(it) } ?: 0
            }
            .take(10)
    }

    fun search(query: String, genreFilter: String? = null): List<Movie> {
        val q = query.trim().lowercase()
        return _allMovies.value.filter { movie ->
            val matchesQuery = q.isEmpty() ||
                movie.title.lowercase().contains(q) ||
                (movie.originalTitle?.lowercase()?.contains(q) == true) ||
                (movie.director?.lowercase()?.contains(q) == true) ||
                (movie.castMembers?.any { it.lowercase().contains(q) } == true) ||
                (movie.titleAr?.contains(q) == true)

            val matchesGenre = genreFilter.isNullOrBlank() ||
                genreFilter.equals("All", ignoreCase = true) ||
                movie.genres?.any { it.equals(genreFilter, ignoreCase = true) } == true

            matchesQuery && matchesGenre
        }
    }

    fun isFavorite(movieId: String): Boolean {
        return _localFavoriteIds.value.contains(movieId)
    }

    suspend fun toggleFavorite(movie: Movie, token: String?): Boolean = withContext(Dispatchers.IO) {
        val current = _localFavoriteIds.value.toMutableSet()
        val willBeFavorite = !current.contains(movie.id)

        if (willBeFavorite) {
            current.add(movie.id)
            if (!token.isNullOrBlank()) {
                try {
                    api.addFavorite("Bearer $token", FavoriteInsert(movieId = movie.id))
                } catch (e: Exception) {
                    // Suppress remote error, local state remains consistent
                }
            }
        } else {
            current.remove(movie.id)
            if (!token.isNullOrBlank()) {
                try {
                    api.removeFavorite("Bearer $token", "eq.${movie.id}")
                } catch (e: Exception) {
                    // Suppress remote error
                }
            }
        }

        _localFavoriteIds.value = current
        sharedPrefs.edit().putStringSet("favorite_ids", current).apply()
        willBeFavorite
    }

    fun getFavoriteMovies(): List<Movie> {
        val ids = _localFavoriteIds.value
        return _allMovies.value.filter { ids.contains(it.id) }
    }
}
