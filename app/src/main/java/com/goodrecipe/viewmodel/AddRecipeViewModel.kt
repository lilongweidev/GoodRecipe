package com.goodrecipe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goodrecipe.data.repository.Recipe
import com.goodrecipe.data.repository.RecipeCategory
import com.goodrecipe.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddRecipeUiState(
    val title: String = "",
    val description: String = "",
    val ingredients: List<String> = listOf(""),
    val steps: List<String> = listOf(""),
    val category: RecipeCategory = RecipeCategory.DINNER,
    val cookTimeMinutes: String = "30",
    val servings: String = "2",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddRecipeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddRecipeUiState())
    val uiState: StateFlow<AddRecipeUiState> = _uiState.asStateFlow()

    private var editingRecipeId: Int? = null

    fun loadRecipeForEdit(id: Int) {
        viewModelScope.launch {
            val recipe = repository.getRecipeById(id) ?: return@launch
            editingRecipeId = id
            _uiState.update {
                it.copy(
                    title = recipe.title,
                    description = recipe.description,
                    ingredients = recipe.ingredients.ifEmpty { listOf("") },
                    steps = recipe.steps.ifEmpty { listOf("") },
                    category = RecipeCategory.entries.find { c -> c.displayName == recipe.category }
                        ?: RecipeCategory.DINNER,
                    cookTimeMinutes = recipe.cookTimeMinutes.toString(),
                    servings = recipe.servings.toString()
                )
            }
        }
    }

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value) }
    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value) }
    fun onCategoryChange(value: RecipeCategory) = _uiState.update { it.copy(category = value) }
    fun onCookTimeChange(value: String) = _uiState.update { it.copy(cookTimeMinutes = value) }
    fun onServingsChange(value: String) = _uiState.update { it.copy(servings = value) }

    fun onIngredientChange(index: Int, value: String) {
        _uiState.update {
            val updated = it.ingredients.toMutableList().also { list -> list[index] = value }
            it.copy(ingredients = updated)
        }
    }

    fun addIngredient() = _uiState.update { it.copy(ingredients = it.ingredients + "") }

    fun removeIngredient(index: Int) {
        if (_uiState.value.ingredients.size <= 1) return
        _uiState.update {
            it.copy(ingredients = it.ingredients.toMutableList().also { list -> list.removeAt(index) })
        }
    }

    fun onStepChange(index: Int, value: String) {
        _uiState.update {
            val updated = it.steps.toMutableList().also { list -> list[index] = value }
            it.copy(steps = updated)
        }
    }

    fun addStep() = _uiState.update { it.copy(steps = it.steps + "") }

    fun removeStep(index: Int) {
        if (_uiState.value.steps.size <= 1) return
        _uiState.update {
            it.copy(steps = it.steps.toMutableList().also { list -> list.removeAt(index) })
        }
    }

    fun saveRecipe() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(error = "请输入菜谱名称") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val recipe = Recipe(
                id = editingRecipeId ?: 0,
                title = state.title,
                description = state.description,
                ingredients = state.ingredients.filter { it.isNotBlank() },
                steps = state.steps.filter { it.isNotBlank() },
                category = state.category.displayName,
                cookTimeMinutes = state.cookTimeMinutes.toIntOrNull() ?: 30,
                servings = state.servings.toIntOrNull() ?: 2
            )
            if (editingRecipeId != null) repository.updateRecipe(recipe)
            else repository.insertRecipe(recipe)
            _uiState.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}
