package com.goodrecipe.ui.screens.random

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.goodrecipe.data.repository.Recipe
import com.goodrecipe.data.repository.RecipeCategory
import com.goodrecipe.viewmodel.RandomMealsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomMealsScreen(
    modifier: Modifier = Modifier,
    onRecipeClick: (Int) -> Unit,
    viewModel: RandomMealsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val categories = listOf(
        RecipeCategory.BREAKFAST,
        RecipeCategory.LUNCH,
        RecipeCategory.DINNER,
        RecipeCategory.SOUP
    )
    val scope = rememberCoroutineScope()
    var globalRerollToken by rememberSaveable { mutableIntStateOf(0) }
    var isBatchRerolling by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "今日推荐",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (uiState.dailySpotlightRecipes.isNotEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            DailySpotlightCarousel(
                                recipes = uiState.dailySpotlightRecipes,
                                onRecipeClick = onRecipeClick
                            )
                        }
                    }

                    if (uiState.notice != null) {
                        item(span = { GridItemSpan(2) }) {
                            Text(
                                text = uiState.notice,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    items(categories, key = { it.name }) { category ->
                        val selected = uiState.selections[category]
                        RandomMealCard(
                            category = category,
                            recipe = selected,
                            isEmpty = uiState.emptyCategories.contains(category),
                            sequenceIndex = categories.indexOf(category).coerceAtLeast(0),
                            globalRerollToken = globalRerollToken,
                            cardEnabled = !isBatchRerolling,
                            onViewRecipe = {
                                selected?.let { onRecipeClick(it.id) }
                            },
                            onReroll = { viewModel.rerollCategory(category) }
                        )
                    }

                    item(span = { GridItemSpan(2) }) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun RandomMealCard(
    category: RecipeCategory,
    recipe: Recipe?,
    isEmpty: Boolean,
    sequenceIndex: Int,
    globalRerollToken: Int,
    cardEnabled: Boolean,
    onViewRecipe: () -> Unit,
    onReroll: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isFlipping by remember { mutableStateOf(false) }
    val flipRotationY by animateFloatAsState(
        targetValue = if (isFlipping) 180f else 0f,
        animationSpec = tween(durationMillis = FLIP_TOTAL_DURATION_MS),
        label = "flipRotation"
    )

    fun launchFlipReroll() {
        if (isFlipping || isEmpty || !cardEnabled) return
        scope.launch {
            isFlipping = true
            delay((FLIP_TOTAL_DURATION_MS / 2).toLong())
            onReroll()
            delay((FLIP_TOTAL_DURATION_MS / 2).toLong())
            isFlipping = false
        }
    }

    LaunchedEffect(globalRerollToken) {
        if (globalRerollToken == 0 || isEmpty) return@LaunchedEffect
        delay((sequenceIndex * STAGGER_DELAY_MS).toLong())
        launchFlipReroll()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                rotationY = flipRotationY
                cameraDistance = 12f * density
            },
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = category.displayName,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Text(
                text = when {
                    isEmpty -> "该分类暂无菜谱"
                    recipe != null -> recipe.title
                    else -> "点「换一个」随机推荐"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = when {
                    isEmpty -> MaterialTheme.colorScheme.onSurfaceVariant
                    recipe != null -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (recipe != null && !isEmpty) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    RandomMetaItem(emoji = "⏱", text = "${recipe.cookTimeMinutes} 分钟")
                    RandomMetaItem(emoji = "👥", text = "${recipe.servings} 人份")
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { launchFlipReroll() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isEmpty && cardEnabled,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("换一个")
                }
                Button(
                    onClick = onViewRecipe,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = recipe != null && cardEnabled,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("查看菜谱")
                }
            }
        }
    }
}

@Composable
private fun RandomMetaItem(emoji: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private const val FLIP_TOTAL_DURATION_MS = 420
private const val STAGGER_DELAY_MS = 120
