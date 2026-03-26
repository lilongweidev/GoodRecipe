package com.goodrecipe.data.seed

import android.content.Context
import android.util.Log
import com.goodrecipe.data.repository.RecipeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeedDataInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: RecipeRepository
) {

    companion object {
        private const val TAG = "SeedDataInitializer"
    }

    suspend fun seedIfNeeded() {
        try {
            val hasData = repository.getAllRecipes().firstOrNull()?.isNotEmpty() == true
            if (hasData) return
            val recipes = SeedDataProvider.loadRecipes(context)
            if (recipes.isEmpty()) return
            recipes.forEach { repository.insertRecipe(it) }
            Log.i(TAG, "Seeded ${recipes.size} recipes from bundled data")
        } catch (error: Exception) {
            Log.e(TAG, "Failed to seed recipes", error)
        }
    }
}
