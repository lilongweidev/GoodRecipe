package com.goodrecipe.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.goodrecipe.data.local.GoodRecipeDatabase
import com.goodrecipe.data.local.dao.RecipeDao
import com.goodrecipe.data.repository.RecipeRepository
import com.goodrecipe.data.repository.RecipeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE recipes ADD COLUMN tags TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE recipes ADD COLUMN nutritionalNotes TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE recipes ADD COLUMN preparationTips TEXT NOT NULL DEFAULT ''")
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "ALTER TABLE recipes ADD COLUMN isUserRecipe INTEGER NOT NULL DEFAULT 0"
            )
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GoodRecipeDatabase =
        Room.databaseBuilder(
            context,
            GoodRecipeDatabase::class.java,
            GoodRecipeDatabase.DATABASE_NAME
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()

    @Provides
    @Singleton
    fun provideRecipeDao(database: GoodRecipeDatabase): RecipeDao =
        database.recipeDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRecipeRepository(
        impl: RecipeRepositoryImpl
    ): RecipeRepository
}
