package com.novatech.terratech.core.presentation.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.novatech.terratech.inventory.presentation.navigation.CreateInventoryDestination
import com.novatech.terratech.inventory.presentation.navigation.EditInventoryDestination
import com.novatech.terratech.inventory.presentation.navigation.InventoryDestination
import com.novatech.terratech.monitoring.presentation.navigation.CreateFieldDestination
import com.novatech.terratech.monitoring.presentation.navigation.FieldSensorsDestination
import com.novatech.terratech.monitoring.presentation.navigation.FieldsDestination
import com.novatech.terratech.monitoring.presentation.navigation.HistoryDestination
import com.novatech.terratech.monitoring.presentation.navigation.ReadingDestination
import com.novatech.terratech.monitoring.presentation.navigation.RegisterSensorDestination
import com.novatech.terratech.monitoring.presentation.navigation.SensorDestination
import com.novatech.terratech.profile.presentation.navigation.ProfileDestination

enum class AppDestination {
    HOME,
    FIELDS,
    CREATE_FIELD,
    SENSORS,
    REGISTER_SENSOR,
    SENSOR,
    HISTORY,
    READING,
    INVENTORY,
    CREATE_INVENTORY,
    EDIT_INVENTORY,
    PROFILE;

    val isRoot: Boolean
        get() = this in setOf(HOME, FIELDS, INVENTORY, PROFILE)

    val showBottomBar: Boolean
        get() = this !in setOf(CREATE_FIELD, REGISTER_SENSOR, CREATE_INVENTORY, EDIT_INVENTORY)

    val showToolbar: Boolean
        get() = this !in setOf(CREATE_FIELD, CREATE_INVENTORY, EDIT_INVENTORY)

    val canRefresh: Boolean
        get() = this !in setOf(PROFILE, CREATE_FIELD, REGISTER_SENSOR, CREATE_INVENTORY, EDIT_INVENTORY)

    companion object {
        fun from(destination: NavDestination?): AppDestination =
            when {
                destination?.hasRoute<FieldsDestination>() == true -> FIELDS
                destination?.hasRoute<CreateFieldDestination>() == true -> CREATE_FIELD
                destination?.hasRoute<FieldSensorsDestination>() == true -> SENSORS
                destination?.hasRoute<RegisterSensorDestination>() == true -> REGISTER_SENSOR
                destination?.hasRoute<SensorDestination>() == true -> SENSOR
                destination?.hasRoute<HistoryDestination>() == true -> HISTORY
                destination?.hasRoute<ReadingDestination>() == true -> READING
                destination?.hasRoute<InventoryDestination>() == true -> INVENTORY
                destination?.hasRoute<CreateInventoryDestination>() == true -> CREATE_INVENTORY
                destination?.hasRoute<EditInventoryDestination>() == true -> EDIT_INVENTORY
                destination?.hasRoute<ProfileDestination>() == true -> PROFILE
                else -> HOME
            }
    }
}
