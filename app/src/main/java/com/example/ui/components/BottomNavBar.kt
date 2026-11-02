package com.example.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CineStreamBackground
import com.example.ui.theme.CineStreamBorder
import com.example.ui.theme.CineStreamRed
import com.example.ui.theme.CineStreamTextMuted

enum class NavDestination(
    val route: String,
    @StringRes val titleResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("home", R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home, "nav_tab_home"),
    SEARCH("search", R.string.nav_search, Icons.Filled.Search, Icons.Outlined.Search, "nav_tab_search"),
    MY_LIST("my_list", R.string.nav_my_list, Icons.Filled.VideoLibrary, Icons.Outlined.VideoLibrary, "nav_tab_mylist"),
    PROFILE("profile", R.string.nav_profile, Icons.Filled.Person, Icons.Outlined.Person, "nav_tab_profile")
}

@Composable
fun CineStreamBottomNavBar(
    currentDestination: NavDestination,
    onNavigate: (NavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 0.5.dp, color = CineStreamBorder)
            .testTag("cinestream_bottom_nav"),
        containerColor = CineStreamBackground,
        windowInsets = WindowInsets.navigationBars
    ) {
        NavDestination.values().forEach { destination ->
            val isSelected = currentDestination == destination
            val labelText = stringResource(destination.titleResId)

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = labelText,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = labelText,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = CineStreamRed,
                    indicatorColor = CineStreamRed,
                    unselectedIconColor = CineStreamTextMuted,
                    unselectedTextColor = CineStreamTextMuted
                ),
                modifier = Modifier.testTag(destination.testTag)
            )
        }
    }
}
