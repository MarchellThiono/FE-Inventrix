package com.example.inventrix.Model

data class ReqTransaksiKeluar(
    val items: List<Item>
) {
    data class Item(
        val barangId: Int,
        val qty: Int
    )
}
