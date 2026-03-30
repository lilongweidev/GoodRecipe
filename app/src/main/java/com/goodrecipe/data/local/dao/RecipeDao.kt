package com.goodrecipe.data.local.dao

import androidx.room.*
import com.goodrecipe.data.local.entity.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes ORDER BY createdAt DESC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE isUserRecipe = 1 ORDER BY createdAt DESC")
    fun getUserRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE category = :category ORDER BY createdAt DESC")
    fun getRecipesByCategory(category: String): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchRecipes(query: String): Flow<List<RecipeEntity>>

    @Query(
        """
        SELECT * FROM recipes
        WHERE (:query = '' OR title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%')
          AND (:category = '' OR category = :category)
          AND (:tag = '' OR tags LIKE '%' || :tag || '%')
          AND (:favoritesOnly = 0 OR isFavorite = 1)
        ORDER BY
          CASE WHEN :sortType = 'COOK_TIME_ASC' THEN cookTimeMinutes END ASC,
          CASE WHEN :sortType = 'COOK_TIME_DESC' THEN cookTimeMinutes END DESC,
          createdAt DESC
        """
    )
    fun queryRecipes(
        query: String,
        category: String,
        tag: String,
        favoritesOnly: Boolean,
        sortType: String
    ): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: Int): RecipeEntity?

    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipesSnapshot(): List<RecipeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity): Long

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Int, isFavorite: Boolean)
}
