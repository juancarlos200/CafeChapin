package com.example.cafechapin.ui.components

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatOrderDate(createdAt: Long): String {
    return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        .format(Date(createdAt))
}
