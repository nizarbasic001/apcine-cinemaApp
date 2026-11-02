package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Movie
import com.example.data.model.UserProfile
import com.example.data.repository.AuthRepository
import com.example.data.repository.MovieRepository
import com.example.util.LocaleHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface MovieUiState {
    object Loading : MovieUiState
    data class Success(val movies: List<Movie>) : MovieUiState
    data class Error(val message: String) : MovieUiState
}

class CineStreamViewModel(application: Application) : AndroidViewModel(application) {

    val movieRepo = MovieRepository(application.applicationContext)
    val authRepo = AuthRepository(application.applicationContext)

    private val _currentLanguage = MutableStateFlow(LocaleHelper.getSavedLanguage(application))
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    val isArabic: StateFlow<Boolean> = _currentLanguage.map { it == LocaleHelper.LANGUAGE_ARABIC }
        .stateIn(viewModelScope, SharingStarted.Eagerly, _currentLanguage.value == LocaleHelper.LANGUAGE_ARABIC)

    private val _uiState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

    private val _selectedMovie = MutableStateFlow<Movie?>(null)
    val selectedMovie: StateFlow<Movie?> = _selectedMovie.asStateFlow()

    private val _playerMovie = MutableStateFlow<Movie?>(null)
    val playerMovie: StateFlow<Movie?> = _playerMovie.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGenre = MutableStateFlow<String?>("All")
    val selectedGenre: StateFlow<String?> = _selectedGenre.asStateFlow()

    val currentUser: StateFlow<UserProfile?> = authRepo.currentUser
    val favoriteIds: StateFlow<Set<String>> = movieRepo.localFavoriteIds

    val filteredSearchMovies: StateFlow<List<Movie>> = combine(
        movieRepo.allMovies,
        _searchQuery,
        _selectedGenre
    ) { movies, query, genre ->
        val q = query.trim().lowercase()
        movies.filter { movie ->
            val matchesQuery = q.isEmpty() ||
                movie.title.lowercase().contains(q) ||
                (movie.titleAr?.contains(q) == true) ||
                (movie.originalTitle?.lowercase()?.contains(q) == true) ||
                (movie.director?.lowercase()?.contains(q) == true) ||
                (movie.directorAr?.contains(q) == true) ||
                (movie.castMembers?.any { it.lowercase().contains(q) } == true) ||
                (movie.castAr?.any { it.contains(q) } == true)

            val matchesGenre = genre.isNullOrBlank() ||
                genre.equals("All", ignoreCase = true) ||
                genre.equals("الكل", ignoreCase = true) ||
                movie.genres?.any { it.equals(genre, ignoreCase = true) } == true ||
                movie.genresAr?.any { it.equals(genre, ignoreCase = true) } == true

            matchesQuery && matchesGenre
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        LocaleHelper.applyLocale(application, _currentLanguage.value)
        refreshMovies()
    }

    fun setLanguage(langCode: String) {
        if (_currentLanguage.value != langCode) {
            LocaleHelper.saveLanguage(getApplication(), langCode)
            LocaleHelper.applyLocale(getApplication(), langCode)
            _currentLanguage.value = langCode
        }
    }

    fun refreshMovies() {
        viewModelScope.launch {
            _uiState.value = MovieUiState.Loading
            val result = movieRepo.loadMovies()
            result.onSuccess { list ->
                _uiState.value = MovieUiState.Success(list)
            }.onFailure { err ->
                _uiState.value = MovieUiState.Error(err.message ?: "Failed to connect to Supabase")
            }
        }
    }

    fun selectMovie(movie: Movie) {
        _selectedMovie.value = movie
    }

    fun selectMovieById(id: String) {
        _selectedMovie.value = movieRepo.getMovieById(id)
    }

    fun openPlayer(movie: Movie) {
        _playerMovie.value = movie
    }

    fun closePlayer() {
        _playerMovie.value = null
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onGenreSelected(genre: String?) {
        _selectedGenre.value = genre
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            movieRepo.toggleFavorite(movie, authRepo.accessToken.value)
        }
    }

    fun isFavorite(movieId: String): Boolean {
        return movieRepo.isFavorite(movieId)
    }

    fun getFavoriteMovies(): List<Movie> {
        return movieRepo.getFavoriteMovies()
    }

    fun login(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = authRepo.login(email, pass)
            res.onSuccess {
                onResult(true, null)
            }.onFailure {
                onResult(false, it.message)
            }
        }
    }

    fun signUp(email: String, pass: String, name: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = authRepo.signUp(email, pass, name)
            res.onSuccess {
                onResult(true, null)
            }.onFailure {
                onResult(false, it.message)
            }
        }
    }

    fun logout() {
        authRepo.logout()
    }
}
