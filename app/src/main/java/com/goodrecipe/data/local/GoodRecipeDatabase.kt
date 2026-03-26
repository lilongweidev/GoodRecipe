package com.goodrecipe.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.goodrecipe.data.local.dao.RecipeDao
import com.goodrecipe.data.local.entity.RecipeEntity

@Database(
    entities = [RecipeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GoodRecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    companion object {
        const val DATABASE_NAME = "good_recipe_db"
    }
}
