package com.example.cafechapin.domain

fun validateFullName(value: String): String? {
    val trimmed = value.trim()

    if (trimmed.any { it.isDigit() }) {
        return "El nombre no puede contener números."
    }

    val letterCount = trimmed.count { it.isLetter() }

    if (letterCount < 3) {
        return "Ingresa un nombre completo válido."
    }

    return null
}

fun validatePhoneNumber(value: String): String? {
    val trimmed = value.trim()

    if (trimmed.length != 8 || trimmed.any { !it.isDigit() }) {
        return "El teléfono debe tener 8 dígitos."
    }

    return null
}

fun validateNit(value: String): String? {
    val trimmed = value.trim()

    if (trimmed.length < 5 || trimmed.any { !it.isDigit() }) {
        return "Ingresa al menos 5 dígitos para el NIT."
    }

    return null
}

fun validateBusinessName(value: String): String? {
    val trimmed = value.trim()

    if (trimmed.length < 3) {
        return "Ingresa una razón social válida."
    }

    return null
}
