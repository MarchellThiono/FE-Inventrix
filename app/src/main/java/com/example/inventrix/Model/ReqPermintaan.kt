package com.example.inventrix.Model

data class Peringatan(
    val namaBarang: String,
    val kodeBarang: String,
    val stokTersisa: Int,
    val tanggal: String,
    val waktu: String
)

