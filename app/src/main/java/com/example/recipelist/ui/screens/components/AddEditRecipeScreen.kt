package com.example.recipelist.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recipelist.ui.events.UiEvent
import com.example.recipelist.ui.viewmodels.AddEditRecipeEvent
import com.example.recipelist.ui.viewmodels.AddEditRecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecipeScreen(
    onPopBackStack: () -> Unit,
    viewModel: AddEditRecipeViewModel = hiltViewModel()
) {
    val difficultyValues = listOf("★", "★★", "★★★", "★★★★", "★★★★★")

    val snackbarHostState = remember { SnackbarHostState() }
    var dropdownMenuExpanded by remember { mutableStateOf(false) }
    var selectedDifficulty by remember { mutableStateOf(viewModel.difficulty) }

    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.PopBackStack -> onPopBackStack()
                is UiEvent.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(AddEditRecipeEvent.OnConfirmDeleteClick)
                    }
                }

                else -> Unit
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(modifier = Modifier.padding(3.dp), onClick = {
                    viewModel.onEvent(AddEditRecipeEvent.OnDoneRecipeClick)
                }) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "save_recipe")
                }

                Button(modifier = Modifier.padding(3.dp), onClick = {
                    viewModel.onEvent(AddEditRecipeEvent.OnDeleteRecipeClick(viewModel.recipe))
                }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "save_recipe")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = viewModel.title,
                    onValueChange = { viewModel.onEvent(AddEditRecipeEvent.OnTitleChange(it)) },
                    placeholder = { Text(text = "Title") },
                    maxLines = 1,
                    modifier = Modifier.weight(0.5f)
                )
                Spacer(modifier = Modifier.size(8.dp))
                TextField(
                    value = viewModel.creator,
                    onValueChange = { viewModel.onEvent(AddEditRecipeEvent.OnCreatorChange(it)) },
                    placeholder = { Text(text = "Creator") },
                    maxLines = 1,
                    modifier = Modifier.weight(0.5f)
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            ExposedDropdownMenuBox(
                expanded = dropdownMenuExpanded,
                onExpandedChange = { dropdownMenuExpanded = !dropdownMenuExpanded },
            ) {
                TextField(
                    modifier = Modifier.menuAnchor(),
                    readOnly = true,
                    value = selectedDifficulty,
                    onValueChange = {},
                    label = { Text(text = "Difficulty") }
                )
                ExposedDropdownMenu(
                    expanded = dropdownMenuExpanded,
                    onDismissRequest = { dropdownMenuExpanded = false }
                ) {
                    difficultyValues.forEach { difficulty ->
                        DropdownMenuItem(
                            text = { Text(difficulty) },
                            onClick = {
                                selectedDifficulty = difficulty
                                dropdownMenuExpanded = false
                                viewModel.onEvent(AddEditRecipeEvent.OnDifficultyChange(difficulty))
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
            TextField(
                value = viewModel.description,
                onValueChange = { viewModel.onEvent(AddEditRecipeEvent.OnDescriptionChange(it)) },
                placeholder = { Text(text = "Type in your recipe") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}