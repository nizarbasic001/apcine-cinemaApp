package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.R
import com.example.data.model.Movie
import com.example.ui.components.FeaturedMovieBanner
import com.example.ui.components.MovieSectionRow
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.CineStreamBackground
import com.example.ui.theme.CineStreamBorder
import com.example.ui.theme.CineStreamRed
import com.example.ui.theme.CineStreamSurfaceVariant
import com.example.ui.viewmodel.CineStreamViewModel
import com.example.ui.viewmodel.MovieUiState

@Composable
fun HomeScreen(
    viewModel: CineStreamViewModel,
    onMovieClick: (Movie) -> Unit,
    onPlayClick: (Movie) -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val featuredMovie = viewModel.movieRepo.getFeatured()
    val trending = viewModel.movieRepo.getTrending()
    val latest = viewModel.movieRepo.getLatest()
    val popular = viewModel.movieRepo.getPopular()
    val series = viewModel.movieRepo.getSeriesOrShows()
    val recommended = viewModel.movieRepo.getRecommended()
    val genres = listOf("All") + viewModel.movieRepo.getAllGenres()

    val listState = rememberLazyListState()
    val isScrolled by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 30 }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CineStreamBackground)
    ) {
        when (val state = uiState) {
            is MovieUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = CineStreamRed,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = stringResource(R.string.loading_catalog),
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            fontFamily = if (isArabic) CairoFontFamily else FontFamily.Default
                        )
                    }
                }
            }

            is MovieUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.error_connection_title),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = if (isArabic) CairoFontFamily else FontFamily.Default
                        )
                        Text(
                            text = state.message,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        OutlinedButton(
                            onClick = { viewModel.refreshMovies() },
                            modifier = Modifier.testTag("home_retry_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = CineStreamRed)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.action_retry_connection),
                                color = Color.White,
                                fontFamily = if (isArabic) CairoFontFamily else FontFamily.Default
                            )
                        }
                    }
                }
            }

            is MovieUiState.Success -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("home_movie_list"),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    // Hero Banner
                    if (featuredMovie != null) {
                        item {
                            FeaturedMovieBanner(
                                movie = featuredMovie,
                                onPlayClick = { onPlayClick(featuredMovie) },
                                onInfoClick = { onMovieClick(featuredMovie) },
                                isFavorite = favoriteIds.contains(featuredMovie.id),
                                onToggleFavorite = { viewModel.toggleFavorite(featuredMovie) },
                                isArabic = isArabic
                            )
                        }
                    }

                    // Genre Quick Pills
                    if (genres.isNotEmpty()) {
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(genres) { rawGenre ->
                                    val isSelected = selectedGenre == rawGenre || (selectedGenre == null && rawGenre == "All")
                                    val displayGenre = Movie.translateGenre(rawGenre, isArabic)

                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = if (isSelected) CineStreamRed else CineStreamSurfaceVariant,
                                        border = androidx.compose.foundation.BorderStroke(
                                            0.5.dp,
                                            if (isSelected) CineStreamRed else CineStreamBorder
                                        ),
                                        modifier = Modifier
                                            .clickable { viewModel.onGenreSelected(if (rawGenre == "All") null else rawGenre) }
                                            .testTag("genre_chip_$rawGenre")
                                    ) {
                                        Text(
                                            text = displayGenre,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontFamily = if (isArabic) CairoFontFamily else FontFamily.Default,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Section Rows
                    item {
                        MovieSectionRow(
                            title = stringResource(R.string.section_trending),
                            movies = trending,
                            onMovieClick = onMovieClick,
                            isArabic = isArabic
                        )
                    }

                    item {
                        MovieSectionRow(
                            title = stringResource(R.string.section_latest),
                            movies = latest,
                            onMovieClick = onMovieClick,
                            isArabic = isArabic
                        )
                    }

                    item {
                        MovieSectionRow(
                            title = stringResource(R.string.section_popular),
                            movies = popular,
                            onMovieClick = onMovieClick,
                            isArabic = isArabic
                        )
                    }

                    item {
                        MovieSectionRow(
                            title = stringResource(R.string.section_tv_series),
                            movies = series,
                            onMovieClick = onMovieClick,
                            isArabic = isArabic
                        )
                    }

                    item {
                        MovieSectionRow(
                            title = stringResource(R.string.section_recommended),
                            movies = recommended,
                            onMovieClick = onMovieClick,
                            isArabic = isArabic
                        )
                    }
                }
            }
        }

        // Top Translucent Netflix-style Header Bar
        val headerModifier = Modifier
            .align(Alignment.TopCenter)
            .zIndex(10f)
            .fillMaxWidth()
            .let { mod ->
                if (isScrolled) {
                    mod
                        .background(CineStreamBackground.copy(alpha = 0.96f))
                        .border(0.5.dp, CineStreamBorder.copy(alpha = 0.5f))
                } else {
                    mod.background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.85f),
                                Color.Black.copy(alpha = 0.40f),
                                Color.Transparent
                            )
                        )
                    )
                }
            }
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 8.dp)

        Row(
            modifier = headerModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            // Aligned Action Icons: Search & Profile
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color.White.copy(alpha = 0.12f), CircleShape)
                        .testTag("home_header_search")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.nav_search),
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            if (currentUser != null) CineStreamRed else Color.White.copy(alpha = 0.12f),
                            CircleShape
                        )
                        .testTag("home_header_profile")
                ) {
                    if (currentUser != null) {
                        Text(
                            text = (currentUser?.displayName ?: currentUser?.email ?: "U").take(1).uppercase(),
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = if (isArabic) CairoFontFamily else FontFamily.Default
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = stringResource(R.string.nav_profile),
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
