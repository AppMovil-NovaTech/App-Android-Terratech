package com.novatech.terratech.inventory.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.novatech.terratech.R
import com.novatech.terratech.core.presentation.component.PageTitle
import com.novatech.terratech.core.presentation.component.PrimaryButton
import com.novatech.terratech.inventory.domain.entity.InventoryDraft
import com.novatech.terratech.inventory.presentation.component.InventoryErrorText
import com.novatech.terratech.inventory.presentation.state.InventoryState
import com.novatech.terratech.monitoring.domain.entity.Field

@Composable
fun CreateInventoryScreen(
    state: InventoryState,
    profileId: Int?,
    fields: List<Field>,
    onRegister: (InventoryDraft) -> Unit,
    onCancel: () -> Unit,
) {
    val ownFields = fields.filter { it.profileId == profileId && it.id > 0 }
    var fieldId by rememberSaveable { mutableIntStateOf(0) }
    var name by rememberSaveable { mutableStateOf("") }
    var unitType by rememberSaveable { mutableStateOf("Sacks") }
    var quantity by rememberSaveable { mutableStateOf("") }
    var averageWeight by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var imageUrl by rememberSaveable { mutableStateOf("") }
    var validationError by rememberSaveable { mutableStateOf(false) }
    val parsedQuantity = quantity.toIntOrNull()
    val parsedWeight = averageWeight.replace(',', '.').toDoubleOrNull()
    val parsedPrice = price.replace(',', '.').toDoubleOrNull()
    val valid =
        profileId != null && profileId > 0 && ownFields.any { it.id == fieldId } &&
            name.isNotBlank() && unitType.isNotBlank() &&
            parsedQuantity != null && parsedQuantity > 0 &&
            parsedWeight != null && parsedWeight.isFinite() && parsedWeight > 0.0 &&
            parsedPrice != null && parsedPrice.isFinite() && parsedPrice > 0.0 &&
            (imageUrl.startsWith("https://", ignoreCase = true) ||
                imageUrl.startsWith("http://", ignoreCase = true))

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { PageTitle(stringResource(R.string.inventory_add), stringResource(R.string.inventory_add_subtitle)) }
        if (profileId == null || profileId <= 0) {
            item { Text(stringResource(R.string.inventory_profile_required)) }
        } else if (ownFields.isEmpty()) {
            item { Text(stringResource(R.string.inventory_field_required)) }
        } else {
            item { Text(stringResource(R.string.inventory_select_field)) }
            items(ownFields, key = { it.id }) { field ->
                Row(
                    Modifier.fillMaxWidth().clickable { fieldId = field.id },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(selected = fieldId == field.id, onClick = { fieldId = field.id })
                    Text(field.name)
                }
            }
        }
        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.inventory_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            OutlinedTextField(
                value = unitType,
                onValueChange = { unitType = it },
                label = { Text(stringResource(R.string.inventory_type)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text(stringResource(R.string.inventory_quantity)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            OutlinedTextField(
                value = averageWeight,
                onValueChange = { averageWeight = it },
                label = { Text(stringResource(R.string.inventory_weight)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text(stringResource(R.string.inventory_price)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text(stringResource(R.string.inventory_image_url)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (validationError) item { Text(stringResource(R.string.inventory_invalid_form)) }
        if (state.error != null) item { InventoryErrorText(state.error) }
        if (state.busy) item { CircularProgressIndicator() }
        item {
            PrimaryButton(stringResource(R.string.save), !state.busy && profileId != null && ownFields.isNotEmpty()) {
                if (!valid) {
                    validationError = true
                } else {
                    validationError = false
                    onRegister(
                        InventoryDraft(
                            name = name.trim(),
                            fieldId = fieldId,
                            profileId = profileId ?: return@PrimaryButton,
                            quantity = parsedQuantity ?: return@PrimaryButton,
                            unitType = unitType.trim(),
                            averageWeightPerUnitKg = parsedWeight ?: return@PrimaryButton,
                            price = parsedPrice ?: return@PrimaryButton,
                            imageUrl = imageUrl.trim(),
                        )
                    )
                }
            }
        }
        item {
            OutlinedButton(onClick = onCancel, enabled = !state.busy) {
                Text(stringResource(R.string.cancel))
            }
        }
    }
}
