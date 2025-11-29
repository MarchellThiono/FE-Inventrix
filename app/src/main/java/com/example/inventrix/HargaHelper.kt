package com.example.inventrix

fun toHargaInt(value: Any?): Int {
    return when (value) {
        is Int -> value
        is Long -> value.toInt()
        is Double -> value.toInt()   // 1050000.0 → 1050000
        is Float -> value.toInt()
        is String -> {
            val clean = value.replace(".0", "")  // Hapus .0 saja
            clean.toIntOrNull() ?: 0
        }
        else -> 0
    }
}

