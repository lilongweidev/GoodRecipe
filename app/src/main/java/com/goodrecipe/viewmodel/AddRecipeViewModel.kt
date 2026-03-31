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
    val tags: List<String> = listOf(""),
    val ingredients: List<String> = listOf(""),
    val steps: List<String> = listOf(""),
    val category: RecipeCategory = RecipeCategory.DINNER,
    val cookTimeMinutes: String = "30",
    val servings: String = "2",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val titleError: String? = null,
    val descriptionError: String? = null,
    val ingredientsError: String? = null,
    val stepsError: String? = null,
    val duplicateRecipeId: Int? = null
)

@HiltViewModel
class AddRecipeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddRecipeUiState())
    val uiState: StateFlow<AddRecipeUiState> = _uiState.asStateFlow()

    private var editingRecipeId: Int? = null
    private var editingIsUserRecipe: Boolean = true

    fun loadRecipeForEdit(id: Int) {
        viewModelScope.launch {
            val recipe = repository.getRecipeById(id) ?: return@launch
            editingRecipeId = id
            editingIsUserRecipe = recipe.isUserRecipe
            _uiState.update {
                it.copy(
                    title = recipe.title,
                    description = recipe.description,
                    tags = recipe.tags.ifEmpty { listOf("") },
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

    fun onTagChange(index: Int, value: String) {
        _uiState.update {
            val updated = it.tags.toMutableList().also { list -> list[index] = value }
            it.copy(tags = updated)
        }
    }

    fun addTag() = _uiState.update { it.copy(tags = it.tags + "") }

    fun removeTag(index: Int) {
        if (_uiState.value.tags.size <= 1) return
        _uiState.update {
            it.copy(tags = it.tags.toMutableList().also { list -> list.removeAt(index) })
        }
    }

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
        // 先清空字段级错误
        _uiState.update {
            it.copy(
                titleError = null,
                descriptionError = null,
                ingredientsError = null,
                stepsError = null
            )
        }

        val hasTitle = state.title.isNotBlank()
        val hasDescription = state.description.isNotBlank()
        val hasIngredient = state.ingredients.any { it.isNotBlank() }
        val hasStep = state.steps.any { it.isNotBlank() }

        if (!hasTitle || !hasDescription || !hasIngredient || !hasStep) {
            _uiState.update {
                it.copy(
                    titleError = if (!hasTitle) "菜谱名称不能为空" else null,
                    descriptionError = if (!hasDescription) "简介不能为空" else null,
                    ingredientsError = if (!hasIngredient) "请至少填写一个食材" else null,
                    stepsError = if (!hasStep) "请至少填写一个步骤" else null
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // 在添加模式下检查是否存在同名菜谱
            if (editingRecipeId == null) {
                val existing = repository.getRecipeByTitle(state.title)
                if (existing != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            duplicateRecipeId = existing.id
                        )
                    }
                    return@launch
                }
            }

            val recipe = Recipe(
                id = editingRecipeId ?: 0,
                title = state.title,
                description = state.description,
                tags = state.tags.map { it.trim() }.filter { it.isNotBlank() },
                ingredients = state.ingredients.filter { it.isNotBlank() },
                steps = state.steps.filter { it.isNotBlank() },
                category = state.category.displayName,
                cookTimeMinutes = state.cookTimeMinutes.toIntOrNull() ?: 30,
                servings = state.servings.toIntOrNull() ?: 2,
                isUserRecipe = if (editingRecipeId != null) editingIsUserRecipe else true
            )
            if (editingRecipeId != null) repository.updateRecipe(recipe)
            else repository.insertRecipe(recipe)
            _uiState.update { it.copy(isLoading = false, isSaved = true) }
        }
    }

    fun dismissDuplicateDialog() {
        _uiState.update { it.copy(duplicateRecipeId = null) }
    }
}
