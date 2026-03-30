package com.goodrecipe.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.goodrecipe.ui.screens.random.RandomMealsScreen

private enum class HomeTab(val label: String) {
    HOME("首页"),
    RANDOM("随机"),
    MINE("我的")
}

@Composable
fun HomeTabsScreen(
    onRecipeClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenMineFavorites: () -> Unit,
    onOpenMineMyRecipes: () -> Unit
) {
    var selectedTab by rememberSaveable { androidx.compose.runtime.mutableStateOf(HomeTab.HOME) }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing.exclude(WindowInsets.statusBars),
        bottomBar = {
            val itemColors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer
            )
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == HomeTab.HOME,
                    onClick = { selectedTab = HomeTab.HOME },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "首页") },
                    label = { Text(HomeTab.HOME.label) },
                    colors = itemColors
                )
                NavigationBarItem(
                    selected = selectedTab == HomeTab.RANDOM,
                    onClick = { selectedTab = HomeTab.RANDOM },
                    icon = { Icon(Icons.Filled.Restaurant, contentDescription = "随机") },
                    label = { Text(HomeTab.RANDOM.label) },
                    colors = itemColors
                )
                NavigationBarItem(
                    selected = selectedTab == HomeTab.MINE,
                    onClick = { selectedTab = HomeTab.MINE },
                    icon = { Icon(Icons.Filled.Person, contentDescription = "我的") },
                    label = { Text(HomeTab.MINE.label) },
                    colors = itemColors
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTab) {
                HomeTab.HOME -> HomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    onRecipeClick = onRecipeClick,
                    onAddClick = onAddClick
                )

                HomeTab.RANDOM -> RandomMealsScreen(
                    modifier = Modifier.fillMaxSize(),
                    onRecipeClick = onRecipeClick
                )

                HomeTab.MINE -> MineScreen(
                    modifier = Modifier.fillMaxSize(),
                    onOpenMineFavorites = onOpenMineFavorites,
                    onOpenMineMyRecipes = onOpenMineMyRecipes,
                    onOpenSettings = onOpenSettings
                )
            }
        }
    }
}
