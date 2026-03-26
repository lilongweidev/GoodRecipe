package com.goodrecipe.data.repository

data class Recipe(
    val id: Int = 0,
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val category: String,
    val cookTimeMinutes: Int,
    val servings: Int,
    val imageUrl: String = "",
    val isFavorite: Boolean = false,
    val tags: List<String> = emptyList(),
    val nutritionalNotes: String = "",
    val preparationTips: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class RecipeCategory(val displayName: String) {
    ALL("全部"),
    BREAKFAST("早餐"),
    LUNCH("中餐"),
    DINNER("晚餐"),
    SOUP("汤品")
}
