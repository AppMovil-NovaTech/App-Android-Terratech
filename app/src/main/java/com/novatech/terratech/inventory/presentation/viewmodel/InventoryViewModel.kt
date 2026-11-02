package com.novatech.terratech.inventory.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novatech.terratech.core.domain.Failure
import com.novatech.terratech.inventory.application.usecase.InventoryActions
import com.novatech.terratech.inventory.presentation.state.InventoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class InventoryViewModel @Inject constructor(private val actions: InventoryActions) : ViewModel() {
    private val mutable = MutableStateFlow(InventoryState())
    val state = mutable.asStateFlow()
    private var currentUserId: Int? = null
    private var activeRequest: Job? = null

    fun loadForUser(userId: Int) {
        if (currentUserId != userId) {
            activeRequest?.cancel()
            currentUserId = userId
            mutable.value = InventoryState(userId = userId)
        }
        refresh()
    }

    fun clearMessage() {
        mutable.update { it.copy(error = null, saved = false, partialProductCreated = false) }
    }

    fun refresh() {
        if (mutable.value.busy) return
        launch {
            val snapshot = actions.load()
            mutable.update { it.copy(items = snapshot.items, products = snapshot.products) }
        }
    }

    fun registerExisting(productId: Int, quantity: Int, location: String) {
        launch {
            val result = actions.register(productId, quantity, location)
            mutable.update { it.copy(items = it.items + result, saved = true) }
        }
    }

    fun registerNew(
        name: String,
        description: String,
        price: Double,
        type: String,
        quantity: Int,
        location: String,
    ) {
        launch {
            val product = actions.addProduct(name, description, price, type)
            mutable.update { it.copy(products = it.products + product, partialProductCreated = true) }
            val result = actions.register(product.id, quantity, location)
            mutable.update {
                it.copy(items = it.items + result, saved = true, partialProductCreated = false)
            }
        }
    }

    fun updateStock(id: Int, quantity: Int) {
        launch {
            val result = actions.updateStock(id, quantity)
            mutable.update {
                it.copy(items = it.items.map { current -> if (current.id == id) result else current }, saved = true)
            }
        }
    }

    private fun launch(block: suspend () -> Unit) {
        if (mutable.value.busy) return
        mutable.update { it.copy(busy = true, error = null, saved = false, partialProductCreated = false) }
        val requestUserId = currentUserId
        activeRequest = viewModelScope.launch {
            try {
                block()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                val code = (exception as? Failure)?.code ?: "INVENTORY_ERROR"
                if (currentUserId == requestUserId) mutable.update { it.copy(error = code) }
            } finally {
                if (currentUserId == requestUserId) mutable.update { it.copy(busy = false) }
            }
        }
    }
}
