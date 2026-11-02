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
import androidx.compose.material3.FilterChip
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
import com.novatech.terratech.inventory.presentation.component.InventoryErrorText
import com.novatech.terratech.inventory.presentation.state.InventoryState

@Composable
fun CreateInventoryScreen(
    state: InventoryState,
    onRegisterExisting: (Int, Int, String) -> Unit,
    onRegisterNew: (String, String, Double, String, Int, String) -> Unit,
    onCancel: () -> Unit,
) {
    var newProduct by rememberSaveable { mutableStateOf(false) }
    var selectedProductId by rememberSaveable { mutableIntStateOf(0) }
    var productSearch by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("0") }
    var quantity by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var validationError by rememberSaveable { mutableStateOf(false) }
    val parsedQuantity = quantity.toIntOrNull()
    val parsedPrice = price.replace(',', '.').toDoubleOrNull()
    val valid =
        parsedQuantity != null && parsedQuantity >= 0 &&
            if (newProduct) {
                name.isNotBlank() && type.isNotBlank() && parsedPrice != null &&
                    parsedPrice.isFinite() && parsedPrice >= 0
            } else selectedProductId > 0
    val products = state.products.filter { it.name.contains(productSearch, ignoreCase = true) }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { PageTitle(stringResource(R.string.inventory_add), stringResource(R.string.inventory_add_subtitle)) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !newProduct,
                    onClick = { newProduct = false; validationError = false },
                    label = { Text(stringResource(R.string.inventory_existing_product)) },
                )
                FilterChip(
                    selected = newProduct,
                    onClick = { newProduct = true; validationError = false },
                    label = { Text(stringResource(R.string.inventory_new_product)) },
                )
            }
        }
        if (!newProduct) {
            item {
                OutlinedTextField(
                    value = productSearch,
                    onValueChange = { productSearch = it },
                    label = { Text(stringResource(R.string.inventory_find_product)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (products.isEmpty()) {
                item { Text(stringResource(R.string.inventory_no_products)) }
            }
            items(products, key = { it.id }) { product ->
                Row(
                    Modifier.fillMaxWidth().clickable { selectedProductId = product.id },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = selectedProductId == product.id,
                        onClick = { selectedProductId = product.id },
                    )
                    Text(product.name + " · " + product.type)
                }
            }
        } else {
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.inventory_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }
            item {
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text(stringResource(R.string.inventory_type)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }
            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.inventory_description)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text(stringResource(R.string.inventory_price)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }
        }
        item {
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text(stringResource(R.string.inventory_quantity)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        }
        item {
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text(stringResource(R.string.inventory_location)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (validationError) item { Text(stringResource(R.string.inventory_invalid_form)) }
        if (state.error != null) item { InventoryErrorText(state.error) }
        if (state.partialProductCreated && state.error != null) {
            item { Text(stringResource(R.string.inventory_partial_created)) }
        }
        if (state.busy) item { CircularProgressIndicator() }
        item {
            PrimaryButton(stringResource(R.string.save), !state.busy) {
                if (!valid) {
                    validationError = true
                } else {
                    validationError = false
                    val units = parsedQuantity ?: return@PrimaryButton
                    if (newProduct) {
                        onRegisterNew(
                            name, description, parsedPrice ?: return@PrimaryButton,
                            type, units, location,
                        )
                    } else onRegisterExisting(selectedProductId, units, location)
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
