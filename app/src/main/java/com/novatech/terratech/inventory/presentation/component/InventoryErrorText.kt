package com.novatech.terratech.inventory.presentation.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.novatech.terratech.R

@Composable
fun InventoryErrorText(error: String?) {
    if (error == null) return
    val message =
        when (error) {
            "UNAUTHENTICATED" -> R.string.inventory_session_expired
            "OFFLINE" -> R.string.inventory_offline
            else -> R.string.inventory_save_error
        }
    Text(stringResource(message), color = Color(0xFFB3261E), style = MaterialTheme.typography.bodyMedium)
}
