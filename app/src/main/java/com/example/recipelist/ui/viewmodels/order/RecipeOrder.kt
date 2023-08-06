package com.example.recipelist.ui.viewmodels.order

import com.example.recipelist.data.models.Recipe
import com.example.recipelist.data.repositories.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.map

class RecipeOrder(
    private val repository: RecipeRepository
) {
    fun getRecipes(
        orderType: OrderType = OrderType.Title(OrderDirection.Ascending)
    ): Flow<List<Recipe>> {
        return repository.getRecipes().map { recipes ->
            when (orderType.orderDirection) {
                is OrderDirection.Ascending -> {
                    when (orderType) {
                        is OrderType.Title -> recipes.sortedBy { it.title.lowercase() }
                        is OrderType.Difficulty -> recipes.sortedBy { it.difficulty.length }
                    }
                }

                is OrderDirection.Descending -> {
                    when (orderType) {
                        is OrderType.Title -> recipes.sortedByDescending { it.title.lowercase() }
                        is OrderType.Difficulty -> recipes.sortedByDescending { it.difficulty.length }
                    }
                }
            }
        }
    }
}

sealed class OrderType(val orderDirection: OrderDirection) {
    class Title(orderDirection: OrderDirection) : OrderType(orderDirection)
    class Difficulty(orderDirection: OrderDirection) : OrderType(orderDirection)

    fun copy(orderDirection: OrderDirection): OrderType  {
        return when(this) {
            is Title -> Title(orderDirection)
            is Difficulty -> Difficulty(orderDirection)
        }
    }
}

sealed class OrderDirection {
    object Ascending : OrderDirection()
    object Descending : OrderDirection()
}