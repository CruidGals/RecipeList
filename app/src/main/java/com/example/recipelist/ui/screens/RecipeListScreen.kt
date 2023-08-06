package com.example.recipelist.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recipelist.ui.events.UiEvent
import com.example.recipelist.ui.screens.components.OrderSection
import com.example.recipelist.ui.screens.components.RecipeItem
import com.example.recipelist.ui.viewmodels.RecipeListEvent
import com.example.recipelist.ui.viewmodels.RecipeListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: RecipeListViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event)
                else -> Unit
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onEvent(RecipeListEvent.OnAddRecipeClick)
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "add_recipe")
            }
        },
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row (horizontalArrangement = Arrangement.Center){
                    Text(
                        text = "Your Recipes",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f)
                    )
                    IconButton(onClick = {
                        viewModel.onEvent(RecipeListEvent.ToggleOrderSection)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "order_section",
                        )
                    }
                }
                AnimatedVisibility(
                    visible = state.value.isOrderSectionVisible,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    OrderSection(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        orderType = state.value.recipeOrder,
                        onOrderChange = { viewModel.onEvent(RecipeListEvent.OnOrderClick(it)) }
                    )
                }
            }
        },
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) { padding ->
        // TODO Make a sorting section
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(state.value.recipes) { recipe ->
                RecipeItem(
                    recipe = recipe,
                    onEvent = viewModel::onEvent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable {
                            viewModel.onEvent(RecipeListEvent.OnRecipeClick(recipe))
                        }
                        .padding(16.dp)
                )
            }
        }
    }

}