package com.goodrecipe.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goodrecipe.data.model.UserProfile
import com.goodrecipe.data.repository.RecipeJsonCodec
import com.goodrecipe.data.repository.RecipeRepository
import com.goodrecipe.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class DataTransferUiState(
    val isLoading: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    val profile = userProfileRepository.profile
    private val _dataTransferUiState = MutableStateFlow(DataTransferUiState())
    val dataTransferUiState: StateFlow<DataTransferUiState> = _dataTransferUiState.asStateFlow()

    fun saveProfile(profile: UserProfile) {
        userProfileRepository.updateProfile(profile)
    }

    fun exportRecipes(contentResolver: ContentResolver, targetUri: Uri) {
        runDataTransfer {
            val recipes = recipeRepository.getAllRecipesSnapshot()
            val json = RecipeJsonCodec.encode(recipes)
            withContext(Dispatchers.IO) {
                contentResolver.openOutputStream(targetUri)?.bufferedWriter()?.use { writer ->
                    writer.write(json)
                } ?: error("无法打开导出文件")
            }
            "导出成功，共 ${recipes.size} 条菜谱"
        }
    }

    fun importRecipes(contentResolver: ContentResolver, sourceUri: Uri) {
        runDataTransfer {
            val json = withContext(Dispatchers.IO) {
                contentResolver.openInputStream(sourceUri)?.bufferedReader()?.use { reader ->
                    reader.readText()
                } ?: error("无法打开导入文件")
            }
            val recipes = RecipeJsonCodec.decode(json).filter { it.title.isNotBlank() }
            val inserted = recipeRepository.syncRecipesWithoutDuplicates(recipes)
            "导入完成，新增 $inserted 条（文件内 ${recipes.size} 条）"
        }
    }

    fun clearDataTransferMessage() {
        _dataTransferUiState.update { it.copy(message = null) }
    }

    private fun runDataTransfer(block: suspend () -> String) {
        viewModelScope.launch {
            _dataTransferUiState.update { it.copy(isLoading = true, message = null) }
            val message = runCatching { block() }
                .getOrElse { "操作失败：${it.message ?: "未知错误"}" }
            _dataTransferUiState.update { it.copy(isLoading = false, message = message) }
        }
    }
}
