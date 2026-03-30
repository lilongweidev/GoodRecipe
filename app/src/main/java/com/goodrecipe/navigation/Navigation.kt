package com.goodrecipe.navigation

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.goodrecipe.ui.screens.add.AddRecipeScreen
import com.goodrecipe.ui.screens.detail.RecipeDetailScreen
import com.goodrecipe.ui.screens.home.HomeTabsScreen
import com.goodrecipe.ui.screens.home.MineRecipeListScreen
import com.goodrecipe.ui.screens.settings.SettingsScreen
import com.goodrecipe.viewmodel.MineRecipeListTypes

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Detail : Screen("detail/{recipeId}") {
        fun createRoute(recipeId: Int) = "detail/$recipeId"
    }
    object AddRecipe : Screen("add_recipe")
    object EditRecipe : Screen("edit_recipe/{recipeId}") {
        fun createRoute(recipeId: Int) = "edit_recipe/$recipeId"
    }

    object Settings : Screen("settings")

    object MineRecipeList : Screen("mine/list/{listType}") {
        fun createRoute(listType: String) = "mine/list/$listType"
    }
}

@Composable
fun GoodRecipeNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth / 3 },
                animationSpec = tween(
                    durationMillis = NAV_ENTER_DURATION,
                    easing = LinearOutSlowInEasing
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = NAV_ENTER_DURATION,
                    easing = FastOutSlowInEasing
                )
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth / 3 },
                animationSpec = tween(
                    durationMillis = NAV_EXIT_DURATION,
                    easing = FastOutLinearInEasing
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = NAV_EXIT_DURATION,
                    easing = FastOutLinearInEasing
                )
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 3 },
                animationSpec = tween(
                    durationMillis = NAV_ENTER_DURATION,
                    easing = LinearOutSlowInEasing
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = NAV_ENTER_DURATION,
                    easing = FastOutSlowInEasing
                )
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth / 3 },
                animationSpec = tween(
                    durationMillis = NAV_EXIT_DURATION,
                    easing = FastOutLinearInEasing
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = NAV_EXIT_DURATION,
                    easing = FastOutLinearInEasing
                )
            )
        }
    ) {
        composable(route = Screen.Home.route) {
            HomeTabsScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.Detail.createRoute(recipeId))
                },
                onAddClick = {
                    navController.navigate(Screen.AddRecipe.route)
                },
                onOpenSettings = { navController.navigate(Screen.Settings.route) },
                onOpenMineFavorites = {
                    navController.navigate(Screen.MineRecipeList.createRoute(MineRecipeListTypes.FAVORITES))
                },
                onOpenMineMyRecipes = {
                    navController.navigate(Screen.MineRecipeList.createRoute(MineRecipeListTypes.MY_RECIPES))
                }
            )
        }

        composable(
            route = Screen.MineRecipeList.route,
            arguments = listOf(navArgument("listType") { type = NavType.StringType })
        ) {
            MineRecipeListScreen(
                onBack = { navController.popBackStack() },
                onRecipeClick = { id -> navController.navigate(Screen.Detail.createRoute(id)) },
                onAddClick = { navController.navigate(Screen.AddRecipe.route) }
            )
        }

        composable(route = Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: return@composable
            RecipeDetailScreen(
                recipeId = recipeId,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Screen.EditRecipe.createRoute(recipeId)) }
            )
        }

        composable(route = Screen.AddRecipe.route) {
            AddRecipeScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditRecipe.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: return@composable
            AddRecipeScreen(
                recipeId = recipeId,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}

private const val NAV_ENTER_DURATION = 220
private const val NAV_EXIT_DURATION = 180
