package com.example.recipelist.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.recipelist.ui.theme.*

@Entity
data class Recipe(
    val title: String,
    val creator: String?,
    val description: String,
    val difficulty: String,
    val isExpanded: Boolean = false,
    @PrimaryKey(true) val id: Int? = null
)
