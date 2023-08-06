package com.example.recipelist.data.models

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Recipe::class],
    version = 1
)
abstract class RecipeDatabase: RoomDatabase() {

    abstract val dao: RecipeDao

    companion object {
        const val DATABASE_NAME = "recipe_db"
    }
}