package com.example.recipelist.data.repositories

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipelist.data.models.Recipe
import com.example.recipelist.data.models.RecipeDao
import kotlinx.coroutines.flow.Flow

class RecipeRepository(
    private val dao: RecipeDao
) {
    suspend fun insertRecipe(recipe: Recipe) {
        dao.insertRecipe(recipe)
    }

    suspend fun deleteRecipe(recipe: Recipe){
        dao.deleteRecipe(recipe)
    }

    suspend fun getRecipeById(id: Int): Recipe? {
        return dao.getRecipeById(id)
    }

    fun getRecipes(): Flow<List<Recipe>> {
        return dao.getRecipes()
    }
}