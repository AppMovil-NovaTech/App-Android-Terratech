package com.novatech.terratech.inventory.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.unit.dp
import com.novatech.terratech.R
import com.novatech.terratech.core.presentation.component.EmptyCard
import com.novatech.terratech.core.presentation.component.PageTitle
import com.novatech.terratech.core.presentation.component.PrimaryButton
import com.novatech.terratech.inventory.presentation.component.InventoryErrorText
import com.novatech.terratech.inventory.presentation.component.InventoryItemCard
import com.novatech.terratech.inventory.presentation.state.InventoryState

@Composable
fun InventoryScreen(
    state: InventoryState,
    onCreate: () -> Unit,
    onEdit: (Int) -> Unit,
    onRefresh: () -> Unit,
) {
    var search by rememberSaveable { mutableStateOf("") }
    var stockFilter by rememberSaveable { mutableStateOf(0) }
    var productType by rememberSaveable { mutableStateOf("") }
    val productById = state.products.associateBy { it.id }
    val types = state.products.map { it.type }.filter { it.isNotBlank() }.distinct().sorted()
    val filtered =
        state.items.filter { item ->
            val product = productById[item.productId]
            val matchesSearch =
                product?.name.orEmpty().contains(search, ignoreCase = true) ||
                    item.warehouseLocation.contains(search, ignoreCase = true)
            val matchesStock =
                stockFilter == 0 || (stockFilter == 1 && item.stockQuantity > 0) ||
                    (stockFilter == 2 && item.stockQuantity == 0)
            val matchesType = productType.isBlank() || product?.type == productType
            matchesSearch && matchesStock && matchesType
        }
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { PageTitle(stringResource(R.string.inventory_title), stringResource(R.string.inventory_count, state.items.size)) }
        item { PrimaryButton(stringResource(R.string.inventory_add), !state.busy, onCreate) }
        item {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text(stringResource(R.string.inventory_search)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (filter in 0..2) {
                    FilterChip(
                        selected = stockFilter == filter,
                        onClick = { stockFilter = filter },
                        label = {
                            Text(
                                stringResource(
                                    when (filter) {
                                        1 -> R.string.inventory_available
                                        2 -> R.string.inventory_out_of_stock
                                        else -> R.string.inventory_all
                                    }
                                )
                            )
                        },
                    )
                }
            }
        }
        if (types.isNotEmpty()) {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = productType.isBlank(),
                            onClick = { productType = "" },
                            label = { Text(stringResource(R.string.inventory_all_types)) },
                        )
                    }
                    items(types) { type ->
                        FilterChip(
                            selected = productType == type,
                            onClick = { productType = type },
                            label = { Text(type) },
                        )
                    }
                }
            }
        }
        if (state.busy) item { CircularProgressIndicator() }
        if (state.error != null) {
            item {
                InventoryErrorText(state.error)
                OutlinedButton(onClick = onRefresh) { Text(stringResource(R.string.retry)) }
            }
        }
        if (filtered.isEmpty() && !state.busy) {
            item {
                EmptyCard(
                    stringResource(R.string.inventory_empty),
                    stringResource(R.string.inventory_empty_help),
                )
            }
        }
        items(filtered, key = { it.id }) { item ->
            InventoryItemCard(item, productById[item.productId], state.busy, onEdit)
        }
    }
}
