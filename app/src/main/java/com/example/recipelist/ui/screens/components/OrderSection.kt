package com.example.recipelist.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.recipelist.ui.viewmodels.order.OrderDirection
import com.example.recipelist.ui.viewmodels.order.OrderType
import com.example.recipelist.ui.viewmodels.order.RecipeOrder

@Composable
fun OrderSection(
    modifier: Modifier = Modifier,
    orderType: OrderType = OrderType.Title(OrderDirection.Ascending),
    onOrderChange: (OrderType) -> Unit
) {
    Box(modifier = modifier) {
        Column(verticalArrangement = Arrangement.Top) {
            Row {
                OrderRadioButton(
                    text = "Title",
                    selected = orderType is OrderType.Title,
                    onSelect = {onOrderChange(OrderType.Title(orderType.orderDirection))}
                )
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                OrderRadioButton(
                    text = "Difficulty",
                    selected = orderType is OrderType.Difficulty,
                    onSelect = {onOrderChange(OrderType.Difficulty(orderType.orderDirection))}
                )
            }
            Row {
                OrderRadioButton(
                    text = "Ascending",
                    selected = orderType.orderDirection is OrderDirection.Ascending,
                    onSelect = {onOrderChange(orderType.copy(OrderDirection.Ascending))}
                )
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                OrderRadioButton(
                    text = "Descending",
                    selected = orderType.orderDirection is OrderDirection.Descending,
                    onSelect = {onOrderChange(orderType.copy(OrderDirection.Descending))}
                )
            }
        }
    }
}

@Composable
fun OrderRadioButton(
    text: String,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(text = text)
    }
}