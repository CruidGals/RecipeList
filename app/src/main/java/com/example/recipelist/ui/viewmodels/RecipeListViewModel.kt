package com.example.recipelist.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipelist.data.models.Recipe
import com.example.recipelist.data.repositories.RecipeRepository
import com.example.recipelist.ui.events.UiEvent
import com.example.recipelist.ui.viewmodels.order.OrderDirection
import com.example.recipelist.ui.viewmodels.order.OrderType
import com.example.recipelist.ui.viewmodels.order.RecipeOrder
import com.example.recipelist.util.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeListViewModel @Inject constructor(
    private val repository: RecipeRepository,
    private val recipeOrder: RecipeOrder
) : ViewModel() {
    private val _state = MutableStateFlow(RecipeState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var getRecipesJob: Job? = null

    init {
        getRecipes(OrderType.Title(OrderDirection.Ascending))
    }

    fun onEvent(event: RecipeListEvent) {
        when (event) {
            is RecipeListEvent.OnRecipeClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_RECIPE + "?recipeId=${event.recipe.id}"))
            }
            is RecipeListEvent.OnAddRecipeClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_RECIPE))
            }
            is RecipeListEvent.OnOrderClick -> {
                if (state.value.recipeOrder == event.orderType &&
                    state.value.recipeOrder.orderDirection == event.orderType.orderDirection) {
                    return
                }

                getRecipes(event.orderType)
            }
            is RecipeListEvent.OnExpandClick -> {
                viewModelScope.launch {
                    repository.insertRecipe(
                        event.recipe.copy(
                            isExpanded = event.isExpanded
                        )
                    )
                }
            }
            is RecipeListEvent.ToggleOrderSection -> {
                _state.value = state.value.copy(
                    isOrderSectionVisible = !state.value.isOrderSectionVisible
                )
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private fun getRecipes(orderType: OrderType) {
        getRecipesJob?.cancel()
        getRecipesJob = recipeOrder.getRecipes(orderType).onEach { recipes ->
            _state.value = _state.value.copy(
                recipes = recipes,
                recipeOrder = orderType
            )
        }.launchIn(viewModelScope)
    }
}

data class RecipeState(
    val recipes: List<Recipe> = emptyList(),
    val recipeOrder: OrderType = OrderType.Title(OrderDirection.Ascending),
    val isOrderSectionVisible: Boolean = false
)

sealed class RecipeListEvent {
    data class OnRecipeClick(val recipe: Recipe) : RecipeListEvent()
    data class OnExpandClick(val recipe: Recipe, val isExpanded: Boolean) : RecipeListEvent()
    data class OnOrderClick(val orderType: OrderType): RecipeListEvent()
    object ToggleOrderSection: RecipeListEvent()
    object OnAddRecipeClick : RecipeListEvent()
}