package com.novatech.terratech.inventory.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.novatech.terratech.R
import com.novatech.terratech.core.presentation.component.EmptyCard
import com.novatech.terratech.core.presentation.component.PageTitle
import com.novatech.terratech.core.presentation.component.PrimaryButton
import com.novatech.terratech.inventory.domain.entity.InventoryItem
import com.novatech.terratech.inventory.presentation.component.InventoryErrorText

@Composable
fun EditInventoryScreen(
    item: InventoryItem?,
    busy: Boolean,
    error: String?,
    onSave: (Int) -> Unit,
    onCancel: () -> Unit,
) {
    var quantity by rememberSaveable(item?.id) { mutableStateOf(item?.stockQuantity?.toString().orEmpty()) }
    var validationError by rememberSaveable { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize().padding(PaddingValues(16.dp)),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PageTitle(stringResource(R.string.inventory_update), item?.name)
        if (item == null) {
            EmptyCard(stringResource(R.string.inventory_item_not_found))
        } else {
            Text(stringResource(R.string.inventory_current_stock, item.stockQuantity))
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text(stringResource(R.string.inventory_new_quantity)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            if (validationError) Text(stringResource(R.string.inventory_invalid_form))
            InventoryErrorText(error)
            if (busy) CircularProgressIndicator()
            PrimaryButton(stringResource(R.string.save), !busy) {
                val parsed = quantity.toIntOrNull()
                if (parsed == null || parsed < 0) validationError = true
                else {
                    validationError = false
                    onSave(parsed)
                }
            }
        }
        OutlinedButton(onClick = onCancel, enabled = !busy) { Text(stringResource(R.string.cancel)) }
    }
}
