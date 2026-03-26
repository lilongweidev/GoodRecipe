package com.goodrecipe.data.seed

import android.content.Context
import com.goodrecipe.R
import com.goodrecipe.data.repository.Recipe
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object SeedDataProvider {

    @Throws(JSONException::class)
    fun loadRecipes(context: Context): List<Recipe> {
        val raw = context.resources.openRawResource(R.raw.good_recipe_seed)
        val json = raw.bufferedReader().use { it.readText() }
        val array = JSONArray(json)
        return List(array.length()) { index -> array.getJSONObject(index).toRecipe() }
    }

    private fun JSONObject.toRecipe() = Recipe(
        title = optString("title", "未命名菜谱"),
        description = optString("description", ""),
        ingredients = optJSONArray("ingredients").toStringList(),
        steps = optJSONArray("steps").toStringList(),
        category = optString("category", ""),
        cookTimeMinutes = optInt("cookTimeMinutes", 30),
        servings = optInt("servings", 2),
        imageUrl = optString("imageUrl", ""),
        isFavorite = optBoolean("isFavorite", false),
        tags = optJSONArray("tags").toStringList(),
        nutritionalNotes = optString("nutritionalNotes", ""),
        preparationTips = optString("preparationTips", "")
    )

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        val list = mutableListOf<String>()
        for (i in 0 until length()) {
            optString(i).takeIf { it.isNotBlank() }?.let(list::add)
        }
        return list
    }
}
