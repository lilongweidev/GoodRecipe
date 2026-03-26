package com.goodrecipe.di

import android.content.Context
import androidx.room.Room
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

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GoodRecipeDatabase =
        Room.databaseBuilder(
            context,
            GoodRecipeDatabase::class.java,
            GoodRecipeDatabase.DATABASE_NAME
        ).build()

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
