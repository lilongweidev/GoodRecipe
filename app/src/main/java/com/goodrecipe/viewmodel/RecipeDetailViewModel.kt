package com.goodrecipe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goodrecipe.data.repository.Recipe
import com.goodrecipe.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val recipe: Recipe? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadRecipe(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val recipe = repository.getRecipeById(id)
            _uiState.update { it.copy(recipe = recipe, isLoading = false) }
        }
    }

    fun toggleFavorite() {
        val recipe = _uiState.value.recipe ?: return
        viewModelScope.launch {
            repository.toggleFavorite(recipe.id, !recipe.isFavorite)
            _uiState.update { it.copy(recipe = recipe.copy(isFavorite = !recipe.isFavorite)) }
        }
    }

    fun deleteRecipe(onDeleted: () -> Unit) {
        val recipe = _uiState.value.recipe ?: return
        viewModelScope.launch {
            repository.deleteRecipe(recipe)
            onDeleted()
        }
    }
}
