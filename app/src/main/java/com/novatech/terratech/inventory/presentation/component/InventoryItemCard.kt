package com.novatech.terratech.inventory.presentation.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.novatech.terratech.R
import com.novatech.terratech.core.presentation.component.FarmCard
import com.novatech.terratech.core.presentation.component.Pill
import com.novatech.terratech.core.presentation.component.PrimaryButton
import com.novatech.terratech.inventory.domain.entity.InventoryItem
import com.novatech.terratech.ui.theme.Muted

@Composable
fun InventoryItemCard(
    item: InventoryItem,
    fieldName: String?,
    busy: Boolean,
    onEdit: (Int) -> Unit,
) {
    FarmCard {
        Text(item.name, style = MaterialTheme.typography.titleMedium)
        Text(item.unitType, color = Muted, style = MaterialTheme.typography.bodySmall)
        Pill(stringResource(R.string.inventory_units, item.stockQuantity))
        if (item.stockQuantity == 0) {
            Text(stringResource(R.string.inventory_out_of_stock), color = MaterialTheme.colorScheme.error)
        }
        Text(stringResource(R.string.inventory_field_value, fieldName ?: "#${item.fieldId}"), color = Muted)
        Text(stringResource(R.string.inventory_total_weight, item.totalWeightKg), color = Muted)
        Text(stringResource(R.string.inventory_unit_price, item.price), color = Muted)
        PrimaryButton(stringResource(R.string.inventory_update), !busy) { onEdit(item.id) }
    }
}
