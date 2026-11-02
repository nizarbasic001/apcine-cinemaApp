package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.Movie
import com.example.ui.components.MovieSectionRow
import com.example.ui.theme.CineStreamBackground
import com.example.ui.theme.CineStreamBorder
import com.example.ui.theme.CineStreamCard
import com.example.ui.theme.CineStreamGold
import com.example.ui.theme.CineStreamRed
import com.example.ui.theme.CineStreamTextMuted
import com.example.ui.viewmodel.CineStreamViewModel

@Composable
fun DetailsScreen(
    movie: Movie,
    viewModel: CineStreamViewModel,
    onBack: () -> Unit,
    onPlay: (Movie) -> Unit,
    onSimilarMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val isArabic by viewModel.isArabic.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val isFav = favoriteIds.contains(movie.id)
    val similarMovies = viewModel.movieRepo.getSimilarMovies(movie)

    val displayTitle = movie.getLocalizedTitle(isArabic)
    val displayDescription = movie.getLocalizedDescription(isArabic)
    val displayGenres = movie.getLocalizedGenresLabel(isArabic)
    val displayDirector = movie.getLocalizedDirector(isArabic)
    val displayCast = movie.getLocalizedCastLabel(isArabic)
    val displayRuntime = movie.getDisplayRuntime(isArabic)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CineStreamBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("movie_details_scroll")
        ) {
            // Backdrop Header with Gradient
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    val backdrop = movie.backdropUrl ?: movie.posterUrl
                    if (!backdrop.isNullOrBlank()) {
                        AsyncImage(
                            model = backdrop,
                            contentDescription = displayTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Gradients
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.6f),
                                        Color.Transparent,
                                        CineStreamBackground
                                    )
                                )
                            )
                    )
                }
            }

            // Info Section with Poster + Meta
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Poster Thumbnail
                        CardPoster(movie = movie, isArabic = isArabic)

                        // Meta details
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.Bottom)
                        ) {
                            Text(
                                text = displayTitle,
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 26.sp
                            )

                            if (!movie.originalTitle.isNullOrBlank() && movie.originalTitle != displayTitle) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = movie.originalTitle,
                                    color = CineStreamTextMuted,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Metadata Chips (Year, Runtime, Rating)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (movie.rating != null && movie.rating > 0) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                            .border(0.5.dp, CineStreamBorder, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = CineStreamGold,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = movie.displayRating,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                if (movie.year != null && movie.year > 0) {
                                    Text(
                                        text = movie.year.toString(),
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                if (movie.runtime != null && movie.runtime > 0) {
                                    Text(
                                        text = displayRuntime,
                                        color = CineStreamTextMuted,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Genres
                            if (!displayGenres.isBlank()) {
                                Text(
                                    text = displayGenres,
                                    color = CineStreamRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons (Play Now & Add to My List & Share)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onPlay(movie) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CineStreamRed,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(2f)
                                .height(46.dp)
                                .testTag("details_play_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = stringResource(R.string.action_play_movie),
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.action_play_movie),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        val inListText = stringResource(R.string.action_in_list)
                        val addToListText = stringResource(R.string.action_add_to_list)

                        OutlinedButton(
                            onClick = { viewModel.toggleFavorite(movie) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isFav) CineStreamRed.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.08f),
                                contentColor = if (isFav) CineStreamRed else Color.White
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isFav) CineStreamRed else CineStreamBorder
                            ),
                            modifier = Modifier
                                .weight(1.5f)
                                .height(46.dp)
                                .testTag("details_favorite_button")
                        ) {
                            Icon(
                                imageVector = if (isFav) Icons.Default.Check else Icons.Default.Add,
                                contentDescription = null,
                                tint = if (isFav) CineStreamRed else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isFav) inListText else addToListText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        IconButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Watch $displayTitle on CineStream: ${movie.resolvePlayerUrl()}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Movie"))
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                .border(1.dp, CineStreamBorder, RoundedCornerShape(8.dp))
                                .testTag("details_share_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = stringResource(R.string.action_share),
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Description / Storyline
                    if (!displayDescription.isNullOrBlank()) {
                        Text(
                            text = stringResource(R.string.section_storyline),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = displayDescription,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Cast & Crew
                    if (!displayDirector.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${stringResource(R.string.label_director)} ",
                                color = CineStreamTextMuted,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = displayDirector,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (!displayCast.isBlank()) {
                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                text = "${stringResource(R.string.label_cast)} ",
                                color = CineStreamTextMuted,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = displayCast,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // Similar Movies Section
            if (similarMovies.isNotEmpty()) {
                item {
                    MovieSectionRow(
                        title = stringResource(R.string.section_similar),
                        movies = similarMovies,
                        onMovieClick = onSimilarMovieClick,
                        isArabic = isArabic
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // Top Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(16.dp)
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                .border(0.5.dp, CineStreamBorder, CircleShape)
                .testTag("details_back_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.player_back),
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun CardPoster(movie: Movie, isArabic: Boolean) {
    val displayTitle = movie.getLocalizedTitle(isArabic)
    Box(
        modifier = Modifier
            .width(105.dp)
            .height(155.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(CineStreamCard)
            .border(1.dp, CineStreamBorder, RoundedCornerShape(8.dp))
    ) {
        val poster = movie.posterUrl ?: movie.backdropUrl
        if (!poster.isNullOrBlank()) {
            AsyncImage(
                model = poster,
                contentDescription = displayTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = displayTitle.take(1),
                    color = CineStreamRed,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
