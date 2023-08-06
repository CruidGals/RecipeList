package com.example.recipelist.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.buildAnnotatedString
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipelist.data.models.Recipe
import com.example.recipelist.data.repositories.RecipeRepository
import com.example.recipelist.ui.events.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditRecipeViewModel @Inject constructor(
    private val repository: RecipeRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    var recipe by mutableStateOf<Recipe?>(null)
        private set

    var title by mutableStateOf("")
        private set

    var creator by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set

    var difficulty by mutableStateOf("★")
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        savedStateHandle.get<Int>("recipeId")?.let { id ->
            if(id != -1) {
                viewModelScope.launch {
                    repository.getRecipeById(id)?.let { recipe ->
                        title = recipe.title
                        creator = recipe.creator ?: ""
                        description = recipe.description
                        difficulty = recipe.difficulty
                        this@AddEditRecipeViewModel.recipe = recipe
                    }
                }
            }
        }
    }

    fun onEvent(event: AddEditRecipeEvent) {
        when(event) {
            is AddEditRecipeEvent.OnTitleChange -> {
                title = event.title
            }
            is AddEditRecipeEvent.OnCreatorChange -> {
                creator = event.creator
            }
            is AddEditRecipeEvent.OnDescriptionChange -> {
                description = event.description
            }
            is AddEditRecipeEvent.OnDifficultyChange -> {
                difficulty = event.difficulty
            }
            is AddEditRecipeEvent.OnDeleteRecipeClick -> {
                recipe = event.recipe
                sendUiEvent(UiEvent.ShowSnackbar(
                    message = "Are you sure you want to delete this recipe?",
                    action = "Delete"
                ))
            }
            is AddEditRecipeEvent.OnConfirmDeleteClick -> {
                sendUiEvent(UiEvent.PopBackStack)
                viewModelScope.launch {
                    recipe?.let { repository.deleteRecipe(it) }
                }
            }
            is AddEditRecipeEvent.OnDoneRecipeClick -> {
                viewModelScope.launch {
                    if (title.isBlank() || description.isBlank()) {
                        sendUiEvent(UiEvent.ShowSnackbar(
                            message = "You must have a " +
                                    "${if(title.isBlank() && description.isBlank()) "title and description" 
                                       else if(title.isBlank()) "title" 
                                       else "description" }" +
                                    " for this recipe"
                        ))
                        return@launch
                    }

                    repository.insertRecipe(
                        Recipe(
                            title = title,
                            creator = creator,
                            description = description,
                            difficulty = difficulty,
                            isExpanded = false,
                            id = recipe?.id
                        )
                    )

                    sendUiEvent(UiEvent.PopBackStack)
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}

sealed class AddEditRecipeEvent {
    data class OnTitleChange(val title: String): AddEditRecipeEvent()
    data class OnCreatorChange(val creator: String): AddEditRecipeEvent()
    data class OnDescriptionChange(val description: String): AddEditRecipeEvent()
    data class OnDifficultyChange(val difficulty: String): AddEditRecipeEvent()
    data class OnDeleteRecipeClick(val recipe: Recipe?): AddEditRecipeEvent()
    object OnConfirmDeleteClick: AddEditRecipeEvent()
    object OnDoneRecipeClick: AddEditRecipeEvent()
}