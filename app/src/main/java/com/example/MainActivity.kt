package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Movie
import com.example.ui.components.CineStreamBottomNavBar
import com.example.ui.components.NavDestination
import com.example.ui.screens.DetailsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyListScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.CineStreamBackground
import com.example.ui.theme.CineStreamTheme
import com.example.ui.viewmodel.CineStreamViewModel
import com.example.util.LocaleHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initialLanguage = LocaleHelper.getSavedLanguage(this)
        LocaleHelper.applyLocale(this, initialLanguage)

        enableEdgeToEdge()
        setContent {
            val appViewModel: CineStreamViewModel = viewModel()
            val currentLanguage by appViewModel.currentLanguage.collectAsState()
            val isArabic by appViewModel.isArabic.collectAsState()

            val baseContext = LocalContext.current
            val localizedContext = remember(currentLanguage) {
                LocaleHelper.applyLocale(baseContext, currentLanguage)
            }

            val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(
                LocalLayoutDirection provides layoutDirection,
                LocalContext provides localizedContext
            ) {
                CineStreamTheme {
                    CineStreamApp(viewModel = appViewModel)
                }
            }
        }
    }
}

@Composable
fun CineStreamApp(
    viewModel: CineStreamViewModel
) {
    var currentDestination by remember { mutableStateOf(NavDestination.HOME) }
    var activeDetailMovie by remember { mutableStateOf<Movie?>(null) }
    var activePlayerMovie by remember { mutableStateOf<Movie?>(null) }
    val isArabic by viewModel.isArabic.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CineStreamBackground)
    ) {
        when {
            // Priority 1: Fullscreen Dedicated Original Video Player
            activePlayerMovie != null -> {
                PlayerScreen(
                    movie = activePlayerMovie!!,
                    onBack = { activePlayerMovie = null },
                    isArabic = isArabic
                )
            }

            // Priority 2: Native Movie Details Screen
            activeDetailMovie != null -> {
                DetailsScreen(
                    movie = activeDetailMovie!!,
                    viewModel = viewModel,
                    onBack = { activeDetailMovie = null },
                    onPlay = { movie -> activePlayerMovie = movie },
                    onSimilarMovieClick = { similar -> activeDetailMovie = similar }
                )
            }

            // Priority 3: Main Tabs with Netflix-style Bottom Navigation
            else -> {
                // If on secondary tab, back returns to HOME
                if (currentDestination != NavDestination.HOME) {
                    BackHandler {
                        currentDestination = NavDestination.HOME
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = CineStreamBackground,
                    bottomBar = {
                        CineStreamBottomNavBar(
                            currentDestination = currentDestination,
                            onNavigate = { destination ->
                                currentDestination = destination
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        when (currentDestination) {
                            NavDestination.HOME -> {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onMovieClick = { movie -> activeDetailMovie = movie },
                                    onPlayClick = { movie -> activePlayerMovie = movie },
                                    onSearchClick = { currentDestination = NavDestination.SEARCH },
                                    onProfileClick = { currentDestination = NavDestination.PROFILE }
                                )
                            }

                            NavDestination.SEARCH -> {
                                SearchScreen(
                                    viewModel = viewModel,
                                    onMovieClick = { movie -> activeDetailMovie = movie }
                                )
                            }

                            NavDestination.MY_LIST -> {
                                MyListScreen(
                                    viewModel = viewModel,
                                    onMovieClick = { movie -> activeDetailMovie = movie },
                                    onPlayClick = { movie -> activePlayerMovie = movie },
                                    onExploreClick = { currentDestination = NavDestination.HOME }
                                )
                            }

                            NavDestination.PROFILE -> {
                                ProfileScreen(
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
