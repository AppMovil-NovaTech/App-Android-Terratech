package com.novatech.terratech.monitoring.domain.valueobject

import com.novatech.terratech.core.domain.Failure

@JvmInline
value class MacAddress private constructor(val value: String) {
    companion object {
        private val macRegex =
            Regex("^([0-9A-Fa-f]{2}[:-]){5}[0-9A-Fa-f]{2}$|^[0-9A-Fa-f]{12}$")

        fun of(value: String): MacAddress {
            val mac = value.trim()
            if (!macRegex.matches(mac)) throw Failure("INVALID_SENSOR_CODE")
            return MacAddress(mac)
        }
    }
}