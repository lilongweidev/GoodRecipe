package com.goodrecipe.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goodrecipe.data.repository.Recipe
import com.goodrecipe.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

object MineRecipeListTypes {
    const val FAVORITES = "favorites"
    const val MY_RECIPES = "my_recipes"
}

@HiltViewModel
class MineRecipeListViewModel @Inject constructor(
    private val repository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val listType: String = checkNotNull(savedStateHandle["listType"]) {
        "mine/list requires listType argument"
    }

    val screenTitle: String = when (listType) {
        MineRecipeListTypes.FAVORITES -> "我的收藏"
        MineRecipeListTypes.MY_RECIPES -> "我的菜谱"
        else -> "菜谱"
    }

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    init {
        val flow = when (listType) {
            MineRecipeListTypes.FAVORITES -> repository.getFavoriteRecipes()
            MineRecipeListTypes.MY_RECIPES -> repository.getUserRecipes()
            else -> flowOf(emptyList())
        }
        flow.onEach { _recipes.value = it }.launchIn(viewModelScope)
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

    fun isMyRecipesList(): Boolean = listType == MineRecipeListTypes.MY_RECIPES
}
