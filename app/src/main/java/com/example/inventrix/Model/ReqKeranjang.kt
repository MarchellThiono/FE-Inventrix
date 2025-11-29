package com.example.inventrix.Model

data class ReqKeranjang(
    val barangId: Int,
    val namaBarang: String,
    val harga: Int,
    val imageUrl: String?,
    var jumlah: Int = 1,
    val merek: String? = null,
    val kodeBarang: String? = null,
    val stokToko: Int? = null
)
