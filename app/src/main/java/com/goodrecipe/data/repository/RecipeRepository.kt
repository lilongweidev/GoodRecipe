package com.goodrecipe.data.repository

import com.goodrecipe.data.local.dao.RecipeDao
import com.goodrecipe.data.local.entity.RecipeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface RecipeRepository {
    fun getAllRecipes(): Flow<List<Recipe>>
    fun queryRecipes(
        query: String,
        category: String?,
        tag: String?,
        favoritesOnly: Boolean,
        sortType: RecipeSortType
    ): Flow<List<Recipe>>
    suspend fun getAllRecipesSnapshot(): List<Recipe>
    fun getFavoriteRecipes(): Flow<List<Recipe>>
    fun getUserRecipes(): Flow<List<Recipe>>
    fun getRecipesByCategory(category: String): Flow<List<Recipe>>
    fun searchRecipes(query: String): Flow<List<Recipe>>
    suspend fun getRecipeById(id: Int): Recipe?
    suspend fun getRecipeByTitle(title: String): Recipe?
    suspend fun insertRecipe(recipe: Recipe): Long
    suspend fun updateRecipe(recipe: Recipe)
    suspend fun deleteRecipe(recipe: Recipe)
    suspend fun toggleFavorite(id: Int, isFavorite: Boolean)
    suspend fun syncRecipesWithoutDuplicates(recipes: List<Recipe>): Int
}

@Singleton
class RecipeRepositoryImpl @Inject constructor(
    private val dao: RecipeDao
) : RecipeRepository {

    override fun getAllRecipes(): Flow<List<Recipe>> =
        dao.getAllRecipes().map { list -> list.map { it.toDomain() } }

    override fun queryRecipes(
        query: String,
        category: String?,
        tag: String?,
        favoritesOnly: Boolean,
        sortType: RecipeSortType
    ): Flow<List<Recipe>> =
        dao.queryRecipes(
            query = query.trim(),
            category = category?.trim().orEmpty(),
            tag = tag?.trim().orEmpty(),
            favoritesOnly = favoritesOnly,
            sortType = sortType.name
        ).map { list -> list.map { it.toDomain() } }

    override suspend fun getAllRecipesSnapshot(): List<Recipe> =
        dao.getAllRecipesSnapshot().map { it.toDomain() }

    override fun getFavoriteRecipes(): Flow<List<Recipe>> =
        dao.getFavoriteRecipes().map { list -> list.map { it.toDomain() } }

    override fun getUserRecipes(): Flow<List<Recipe>> =
        dao.getUserRecipes().map { list -> list.map { it.toDomain() } }

    override fun getRecipesByCategory(category: String): Flow<List<Recipe>> =
        dao.getRecipesByCategory(category).map { list -> list.map { it.toDomain() } }

    override fun searchRecipes(query: String): Flow<List<Recipe>> =
        dao.searchRecipes(query).map { list -> list.map { it.toDomain() } }

    override suspend fun getRecipeById(id: Int): Recipe? =
        dao.getRecipeById(id)?.toDomain()

    override suspend fun getRecipeByTitle(title: String): Recipe? =
        dao.getRecipeByTitle(title.trim())?.toDomain()

    override suspend fun insertRecipe(recipe: Recipe): Long =
        dao.insertRecipe(recipe.toEntity())

    override suspend fun updateRecipe(recipe: Recipe) =
        dao.updateRecipe(recipe.toEntity())

    override suspend fun deleteRecipe(recipe: Recipe) =
        dao.deleteRecipe(recipe.toEntity())

    override suspend fun toggleFavorite(id: Int, isFavorite: Boolean) =
        dao.toggleFavorite(id, isFavorite)

    override suspend fun syncRecipesWithoutDuplicates(recipes: List<Recipe>): Int {
        val existingKeys = dao.getAllRecipesSnapshot()
            .map { "${it.category}|${it.title.trim()}" }
            .toHashSet()

        var insertedCount = 0
        for (recipe in recipes) {
            val key = "${recipe.category}|${recipe.title.trim()}"
            if (!existingKeys.contains(key)) {
                dao.insertRecipe(recipe.copy(id = 0).toEntity())
                existingKeys.add(key)
                insertedCount++
            }
        }
        return insertedCount
    }
}

// --- Mappers ---

private fun RecipeEntity.toDomain() = Recipe(
    id = id,
    title = title,
    description = description,
    ingredients = ingredients.split("||").filter { it.isNotBlank() },
    steps = steps.split("||").filter { it.isNotBlank() },
    category = category,
    cookTimeMinutes = cookTimeMinutes,
    servings = servings,
    imageUrl = imageUrl,
    isFavorite = isFavorite,
    tags = tags.split("||").filter { it.isNotBlank() },
    nutritionalNotes = nutritionalNotes,
    preparationTips = preparationTips,
    createdAt = createdAt,
    isUserRecipe = isUserRecipe
)

private fun Recipe.toEntity() = RecipeEntity(
    id = id,
    title = title,
    description = description,
    ingredients = ingredients.joinToString("||"),
    steps = steps.joinToString("||"),
    category = category,
    cookTimeMinutes = cookTimeMinutes,
    servings = servings,
    imageUrl = imageUrl,
    isFavorite = isFavorite,
    tags = tags.joinToString("||"),
    nutritionalNotes = nutritionalNotes,
    preparationTips = preparationTips,
    createdAt = createdAt,
    isUserRecipe = isUserRecipe
)
