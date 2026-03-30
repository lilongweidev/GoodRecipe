package com.goodrecipe.data.repository

import org.json.JSONArray
import org.json.JSONObject

object RecipeJsonCodec {
    fun encode(recipes: List<Recipe>): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("exportedAt", System.currentTimeMillis())
        root.put("recipes", JSONArray().apply {
            recipes.forEach { put(it.toJson()) }
        })
        return root.toString()
    }

    fun decode(json: String): List<Recipe> {
        val root = JSONObject(json)
        val recipesArray = root.optJSONArray("recipes") ?: JSONArray()
        return buildList {
            for (index in 0 until recipesArray.length()) {
                val recipeObj = recipesArray.optJSONObject(index) ?: continue
                add(recipeObj.toRecipe())
            }
        }
    }
}

private fun Recipe.toJson(): JSONObject {
    val obj = JSONObject()
    obj.put("id", id)
    obj.put("title", title)
    obj.put("description", description)
    obj.put("ingredients", JSONArray(ingredients))
    obj.put("steps", JSONArray(steps))
    obj.put("category", category)
    obj.put("cookTimeMinutes", cookTimeMinutes)
    obj.put("servings", servings)
    obj.put("imageUrl", imageUrl)
    obj.put("isFavorite", isFavorite)
    obj.put("tags", JSONArray(tags))
    obj.put("nutritionalNotes", nutritionalNotes)
    obj.put("preparationTips", preparationTips)
    obj.put("createdAt", createdAt)
    obj.put("isUserRecipe", isUserRecipe)
    return obj
}

private fun JSONObject.toRecipe(): Recipe {
    return Recipe(
        id = optInt("id", 0),
        title = optString("title"),
        description = optString("description"),
        ingredients = optJsonStringList("ingredients"),
        steps = optJsonStringList("steps"),
        category = optString("category"),
        cookTimeMinutes = optInt("cookTimeMinutes", 0),
        servings = optInt("servings", 1),
        imageUrl = optString("imageUrl"),
        isFavorite = optBoolean("isFavorite", false),
        tags = optJsonStringList("tags"),
        nutritionalNotes = optString("nutritionalNotes"),
        preparationTips = optString("preparationTips"),
        createdAt = optLong("createdAt", System.currentTimeMillis()),
        isUserRecipe = optBoolean("isUserRecipe", false)
    )
}

private fun JSONObject.optJsonStringList(key: String): List<String> {
    val array = optJSONArray(key) ?: return emptyList()
    return buildList {
        for (i in 0 until array.length()) {
            add(array.optString(i))
        }
    }.filter { it.isNotBlank() }
}
