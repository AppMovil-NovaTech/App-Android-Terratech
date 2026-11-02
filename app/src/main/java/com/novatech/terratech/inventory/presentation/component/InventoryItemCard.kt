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
import com.novatech.terratech.inventory.domain.entity.InventoryProduct
import com.novatech.terratech.ui.theme.Muted

@Composable
fun InventoryItemCard(
    item: InventoryItem,
    product: InventoryProduct?,
    busy: Boolean,
    onEdit: (Int) -> Unit,
) {
    FarmCard {
        Text(product?.name ?: stringResource(R.string.inventory_product_fallback, item.productId), style = MaterialTheme.typography.titleMedium)
        Text(product?.type.orEmpty(), color = Muted, style = MaterialTheme.typography.bodySmall)
        Pill(stringResource(R.string.inventory_units, item.stockQuantity))
        if (item.stockQuantity == 0) {
            Text(stringResource(R.string.inventory_out_of_stock), color = MaterialTheme.colorScheme.error)
        }
        if (item.warehouseLocation.isNotBlank()) {
            Text(stringResource(R.string.inventory_location_value, item.warehouseLocation), color = Muted)
        }
        PrimaryButton(stringResource(R.string.inventory_update), !busy) { onEdit(item.id) }
    }
}
