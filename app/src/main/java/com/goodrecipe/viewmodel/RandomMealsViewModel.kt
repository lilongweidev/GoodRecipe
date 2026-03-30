package com.goodrecipe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goodrecipe.data.repository.Recipe
import com.goodrecipe.data.repository.RecipeCategory
import com.goodrecipe.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import kotlin.random.Random

data class RandomMealsUiState(
    val isLoading: Boolean = true,
    val selections: Map<RecipeCategory, Recipe> = emptyMap(),
    val emptyCategories: Set<RecipeCategory> = emptySet(),
    val notice: String? = null,
    /** 按本地日历日、从全集菜谱中稳定抽取的 3 道轮播推荐 */
    val dailySpotlightRecipes: List<Recipe> = emptyList()
)

@HiltViewModel
class RandomMealsViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val picker = RandomRecipePicker()
    private val categories = listOf(
        RecipeCategory.BREAKFAST,
        RecipeCategory.LUNCH,
        RecipeCategory.DINNER,
        RecipeCategory.SOUP
    )

    private val _uiState = MutableStateFlow(RandomMealsUiState())
    val uiState: StateFlow<RandomMealsUiState> = _uiState.asStateFlow()

    private var recipesByCategory: Map<RecipeCategory, List<Recipe>> = emptyMap()

    init {
        observeRecipes()
    }

    fun rerollAll() {
        val previousSelections = _uiState.value.selections
        val nextSelections = previousSelections.toMutableMap()
        var unchangedCount = 0

        for (category in categories) {
            val options = recipesByCategory[category].orEmpty()
            val result = picker.pickDifferent(options, previousSelections[category])
            if (result.selection != null) {
                nextSelections[category] = result.selection
            }
            if (!result.changed && previousSelections[category] != null) {
                unchangedCount++
            }
        }

        _uiState.update {
            it.copy(
                selections = nextSelections,
                notice = if (unchangedCount > 0) "部分分类菜谱不足，无法换新" else null
            )
        }
    }

    fun rerollCategory(category: RecipeCategory) {
        val options = recipesByCategory[category].orEmpty()
        val current = _uiState.value.selections[category]
        val result = picker.pickDifferent(options, current)

        if (result.selection == null) {
            _uiState.update { it.copy(notice = "${category.displayName}暂无菜谱") }
            return
        }

        _uiState.update { state ->
            state.copy(
                selections = state.selections + (category to result.selection),
                notice = if (!result.changed) "${category.displayName}可选菜谱不足，无法换新" else null
            )
        }
    }

    private fun observeRecipes() {
        viewModelScope.launch {
            repository.getAllRecipes().collect { recipes ->
                recipesByCategory = categories.associateWith { category ->
                    recipes.filter { it.category == category.displayName }
                }

                val currentSelections = _uiState.value.selections
                val nextSelections = currentSelections.toMutableMap()
                val emptyCategories = mutableSetOf<RecipeCategory>()

                for (category in categories) {
                    val options = recipesByCategory[category].orEmpty()
                    if (options.isEmpty()) {
                        emptyCategories += category
                        nextSelections.remove(category)
                        continue
                    }

                    val current = currentSelections[category]
                    val existsInOptions = current?.let { selected ->
                        options.any { it.id == selected.id }
                    } ?: false
                    if (!existsInOptions) {
                        nextSelections[category] = options.random()
                    }
                }

                val today = LocalDate.now(ZoneId.systemDefault())
                val spotlight = pickDailySpotlightRecipes(recipes, today)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        selections = nextSelections,
                        emptyCategories = emptyCategories,
                        dailySpotlightRecipes = spotlight
                    )
                }
            }
        }
    }
}

private fun pickDailySpotlightRecipes(allRecipes: List<Recipe>, date: LocalDate): List<Recipe> {
    if (allRecipes.isEmpty()) return emptyList()
    val rng = Random(date.toEpochDay())
    return allRecipes.shuffled(rng).take(3)
}
