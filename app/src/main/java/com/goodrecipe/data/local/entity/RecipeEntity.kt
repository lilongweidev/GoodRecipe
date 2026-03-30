package com.goodrecipe.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val ingredients: String,   // JSON-serialized list
    val steps: String,         // JSON-serialized list
    val category: String,
    val cookTimeMinutes: Int,
    val servings: Int,
    val imageUrl: String = "",
    val isFavorite: Boolean = false,
    val tags: String = "",
    val nutritionalNotes: String = "",
    val preparationTips: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isUserRecipe: Boolean = false
)
