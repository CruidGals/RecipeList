package com.example.recipelist.di

import android.app.Application
import androidx.room.Room
import com.example.recipelist.data.models.RecipeDao
import com.example.recipelist.data.models.RecipeDatabase
import com.example.recipelist.data.repositories.RecipeRepository
import com.example.recipelist.ui.viewmodels.order.RecipeOrder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRecipeDatabase(app: Application): RecipeDatabase {
        return Room.databaseBuilder(
            context = app,
            klass = RecipeDatabase::class.java,
            name = RecipeDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(db: RecipeDatabase): RecipeRepository {
        return RecipeRepository(db.dao)
    }

    @Provides
    @Singleton
    fun provideRecipeOrder(repository: RecipeRepository): RecipeOrder {
        return RecipeOrder(repository)
    }
}