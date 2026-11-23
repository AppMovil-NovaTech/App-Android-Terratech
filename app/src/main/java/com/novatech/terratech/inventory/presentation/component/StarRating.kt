package com.novatech.terratech.inventory.presentation.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.novatech.terratech.ui.theme.Amber

@Composable
fun StarRating(rating: Int, modifier: Modifier = Modifier) {
    val filled = rating.coerceIn(0, 5)
    Text(
        text = "★".repeat(filled) + "☆".repeat(5 - filled),
        color = Amber,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier,
    )
}
