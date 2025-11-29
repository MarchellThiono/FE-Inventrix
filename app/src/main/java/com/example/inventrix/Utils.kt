package com.example.inventrix

import java.text.NumberFormat
import java.util.Locale

// ### Utility: parsing harga dari API (contoh "1.050.000") ke Int
fun parsePriceString(priceStr: String?): Int {
    if (priceStr.isNullOrBlank()) return 0
    // Hapus semua selain digit
    val digits = priceStr.replace(Regex("[^0-9]"), "")
    return try {
        digits.toInt()
    } catch (e: Exception) {
        0
    }
}

// ### Utility: format ke "Rp1.050.000"
fun formatRupiah(value: Int): String {
    val nf = NumberFormat.getInstance(Locale("in", "ID"))
    return "Rp" + nf.format(value)
}
