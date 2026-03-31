package com.goodrecipe.ui.screens.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.goodrecipe.data.repository.RecipeCategory
import com.goodrecipe.viewmodel.AddRecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(
    recipeId: Int? = null,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onEditExisting: (Int) -> Unit,
    viewModel: AddRecipeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(recipeId) {
        recipeId?.let { viewModel.loadRecipeForEdit(it) }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onSaved()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                title = { Text(if (recipeId == null) "添加菜谱" else "编辑菜谱") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveRecipe() },
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Text("保存", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                ,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        if (uiState.duplicateRecipeId != null) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissDuplicateDialog() },
                title = { Text("提示") },
                text = { Text("已存在相同菜谱，是否进行编辑？") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val id = uiState.duplicateRecipeId ?: return@TextButton
                            viewModel.dismissDuplicateDialog()
                            onEditExisting(id)
                        }
                    ) {
                        Text("确定")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissDuplicateDialog() }) {
                        Text("取消")
                    }
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surface)
                    )
                )
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            item {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = viewModel::onTitleChange,
                    label = { Text("菜谱名称 *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.titleError != null,
                    supportingText = {
                        uiState.titleError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    colors = deepBlueTextFieldColors()
                )
            }

            // Description
            item {
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = viewModel::onDescriptionChange,
                    label = { Text("简介") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    isError = uiState.descriptionError != null,
                    supportingText = {
                        uiState.descriptionError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    colors = deepBlueTextFieldColors()
                )
            }

            // Category + cook time + servings row
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryDropdown(
                        selected = uiState.category,
                        onSelected = viewModel::onCategoryChange,
                        modifier = Modifier.weight(1f)
                    )
                    NumberSelectorDropdown(
                        label = "时间(分钟)",
                        value = uiState.cookTimeMinutes,
                        options = COOK_TIME_OPTIONS,
                        onSelected = viewModel::onCookTimeChange,
                        modifier = Modifier.weight(1f)
                    )
                    NumberSelectorDropdown(
                        label = "人份",
                        value = uiState.servings,
                        options = SERVINGS_OPTIONS,
                        onSelected = viewModel::onServingsChange,
                        modifier = Modifier.weight(0.7f)
                    )
                }
            }

            // Tags
            item {
                Spacer(Modifier.height(4.dp))
                SectionHeader(
                    title = "🏷 标签",
                    onAdd = { viewModel.addTag() }
                )
            }
            itemsIndexed(uiState.tags) { index, tag ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = tag,
                        onValueChange = { viewModel.onTagChange(index, it) },
                        label = { Text("标签 ${index + 1}") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = deepBlueTextFieldColors()
                    )
                    IconButton(
                        onClick = { viewModel.removeTag(index) },
                        enabled = uiState.tags.size > 1
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Ingredients
            item {
                SectionHeader(
                    title = "🥦 食材",
                    onAdd = { viewModel.addIngredient() }
                )
                uiState.ingredientsError?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            itemsIndexed(uiState.ingredients) { index, ingredient ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = ingredient,
                        onValueChange = { viewModel.onIngredientChange(index, it) },
                        label = { Text("食材 ${index + 1}") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = deepBlueTextFieldColors()
                    )
                    IconButton(
                        onClick = { viewModel.removeIngredient(index) },
                        enabled = uiState.ingredients.size > 1
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Steps
            item {
                Spacer(Modifier.height(4.dp))
                SectionHeader(
                    title = "📋 步骤",
                    onAdd = { viewModel.addStep() }
                )
                uiState.stepsError?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            itemsIndexed(uiState.steps) { index, step ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = step,
                        onValueChange = { viewModel.onStepChange(index, it) },
                        label = { Text("步骤 ${index + 1}") },
                        modifier = Modifier.weight(1f),
                        minLines = 2,
                        maxLines = 5,
                        colors = deepBlueTextFieldColors()
                    )
                    IconButton(
                        onClick = { viewModel.removeStep(index) },
                        enabled = uiState.steps.size > 1,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(60.dp)) }
        }
    }
}

@Composable
private fun SectionHeader(title: String, onAdd: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        TextButton(onClick = onAdd) {
            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text("添加")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    selected: RecipeCategory,
    onSelected: (RecipeCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text("分类") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
            singleLine = true,
            colors = deepBlueTextFieldColors()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            RecipeCategory.entries
                .filter { it != RecipeCategory.ALL }
                .forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.displayName) },
                        onClick = {
                            onSelected(category)
                            expanded = false
                        }
                    )
                }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NumberSelectorDropdown(
    label: String,
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
            singleLine = true,
            colors = deepBlueTextFieldColors()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun deepBlueTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface
)

private val COOK_TIME_OPTIONS = listOf("5", "10", "15", "20", "30", "45", "60", "90", "120")
private val SERVINGS_OPTIONS = listOf("1", "2", "3", "4", "6", "8", "10")
