package com.example.recipelist.ui.screens.components

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipelist.data.models.Recipe
import com.example.recipelist.ui.viewmodels.RecipeListEvent

@Composable
fun RecipeItem(
    recipe: Recipe,
    onEvent: (RecipeListEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(recipe.isExpanded) }
    val roundedCornerAnimation: Int by animateIntAsState(if (isExpanded) 5 else 20)

    Box(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(roundedCornerAnimation))
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
                Row {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = recipe.title,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        recipe.creator?.let {
                            Text(
                                text = "By: $it",
                                color = Color.Gray,
                                fontSize = 15.sp,
                                maxLines = 1,
                            )
                        }
                    }

                    Text(
                        text = recipe.difficulty,
                        fontSize = 30.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Button(onClick = {
                        onEvent(RecipeListEvent.OnExpandClick(recipe, !isExpanded))
                        isExpanded = !isExpanded
                    }) {
                        Text(text = if (!isExpanded) "+" else "-")
                    }
                }
                Text(
                    modifier = Modifier.animateContentSize(),
                    text = recipe.description,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}