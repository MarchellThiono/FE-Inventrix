package com.example.inventrix.Model

data class ReqKeranjang(
    val nama: String,
    val merek: String,
    val kodeBarang: String,
    val harga: Int,
    var jumlah: Int = 1
)


