package com.novatech.terratech.monitoring.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import com.novatech.terratech.monitoring.domain.valueobject.MacAddress

@Composable
fun RegisterSensorScreen(
    busy: Boolean,
    onSave: (String, String, String) -> Unit,
    fieldName: String = "",
    error: String? = null,
    onClear: () -> Unit = {},
) {
    var code by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var status by rememberSaveable { mutableStateOf("ONLINE") }
    var attempted by rememberSaveable { mutableStateOf(false) }
    val validCode = runCatching { MacAddress.of(code) }.isSuccess
    val validName = name.trim().length in 2..100
    val focus = LocalFocusManager.current
    val submit = {
        attempted = true
        if (validCode && validName && !busy) {
            focus.clearFocus()
            onSave(code, name, status)
        }
    }
    RegisterSensorContent(
        code = code,
        name = name,
        status = status,
        attempted = attempted,
        validCode = validCode,
        validName = validName,
        busy = busy,
        fieldName = fieldName,
        error = error,
        onCodeChange = {
            code = formatMac(it)
            onClear()
        },
        onNameChange = { name = it },
        onStatusChange = { status = it },
        onSubmit = submit,
    )
}

private fun formatMac(input: String): String {
    val hex = input.uppercase().filter { it in '0'..'9' || it in 'A'..'F' }.take(12)
    return hex.chunked(2).joinToString(":")
}
