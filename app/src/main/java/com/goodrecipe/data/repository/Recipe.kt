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
    val createdAt: Long = System.currentTimeMillis()
)

enum class RecipeCategory(val displayName: String) {
    ALL("全部"),
    BREAKFAST("早餐"),
    LUNCH("午餐"),
    DINNER("晚餐"),
    DESSERT("甜点"),
    SNACK("小吃"),
    SOUP("汤品"),
    SALAD("沙拉")
}
