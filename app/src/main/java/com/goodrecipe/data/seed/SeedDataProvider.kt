package com.goodrecipe.data.seed

import android.content.Context
import com.goodrecipe.R
import com.goodrecipe.data.repository.Recipe
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeedDataProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun loadRecipes(): List<Recipe> {
        val raw = context.resources.openRawResource(R.raw.good_recipe_seed)
        val content = raw.bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(content)
        val recipes = mutableListOf<Recipe>()
        for (index in 0 until jsonArray.length()) {
            val recipeJson = jsonArray.getJSONObject(index)
            recipes.add(parseRecipe(recipeJson))
        }
        return recipes
    }

    private fun parseRecipe(json: JSONObject): Recipe {
        return Recipe(
            title = json.optString("title"),
            description = json.optString("description"),
            ingredients = jsonArrayToList(json.optJSONArray("ingredients")),
            steps = jsonArrayToList(json.optJSONArray("steps")),
            category = json.optString("category"),
            cookTimeMinutes = json.optInt("cookTimeMinutes"),
            servings = json.optInt("servings"),
            imageUrl = json.optString("imageUrl"),
            isFavorite = json.optBoolean("isFavorite", false),
            tags = jsonArrayToList(json.optJSONArray("tags")),
            nutritionalNotes = json.optString("nutritionalNotes"),
            preparationTips = json.optString("preparationTips")
        )
    }

    private fun jsonArrayToList(array: JSONArray?): List<String> {
        if (array == null) return emptyList()
        val list = mutableListOf<String>()
        for (i in 0 until array.length()) {
            list.add(array.optString(i))
        }
        return list
    }
}
