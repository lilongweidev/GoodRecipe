package com.goodrecipe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goodrecipe.data.repository.Recipe
import com.goodrecipe.data.repository.RecipeCategory
import com.goodrecipe.data.repository.RecipeRepository
import com.goodrecipe.data.seed.SeedDataProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedCategory: RecipeCategory = RecipeCategory.ALL,
    val showFavoritesOnly: Boolean = false,
    val syncVersion: Int = 0,
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RecipeRepository,
    private val seedDataProvider: SeedDataProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeRecipes()
    }

    private fun observeRecipes() {
        viewModelScope.launch {
            _uiState.flatMapLatest { state ->
                when {
                    state.searchQuery.isNotBlank() ->
                        repository.searchRecipes(state.searchQuery)
                    state.showFavoritesOnly ->
                        repository.getFavoriteRecipes()
                    state.selectedCategory != RecipeCategory.ALL ->
                        repository.getRecipesByCategory(state.selectedCategory.displayName)
                    else ->
                        repository.getAllRecipes()
                }
            }.collect { recipes ->
                _uiState.update { it.copy(recipes = recipes, isLoading = false) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCategorySelected(category: RecipeCategory) {
        _uiState.update { it.copy(selectedCategory = category, showFavoritesOnly = false) }
    }

    fun onToggleFavorites() {
        _uiState.update { it.copy(showFavoritesOnly = !it.showFavoritesOnly) }
    }

    fun onSync() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                val seeds = seedDataProvider.loadRecipes()
                repository.syncRecipesWithoutDuplicates(seeds)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        searchQuery = "",
                        selectedCategory = RecipeCategory.ALL,
                        showFavoritesOnly = false,
                        syncVersion = it.syncVersion + 1
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = throwable.message ?: "同步失败"
                    )
                }
            }
        }
    }

    fun onToggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            repository.toggleFavorite(recipe.id, !recipe.isFavorite)
        }
    }

    fun onDeleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.deleteRecipe(recipe)
        }
    }
}
