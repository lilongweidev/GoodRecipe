package com.goodrecipe.data.seed

import com.goodrecipe.data.repository.RecipeRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeedDataInitializer @Inject constructor(
    private val seedDataProvider: SeedDataProvider,
    private val repository: RecipeRepository
) {
    suspend fun seedIfNeeded() {
        val existing = repository.getAllRecipes().firstOrNull()
        if (existing.isNullOrEmpty()) {
            val seeds = seedDataProvider.loadRecipes()
            for (recipe in seeds) {
                repository.insertRecipe(recipe)
            }
        }
    }
}
