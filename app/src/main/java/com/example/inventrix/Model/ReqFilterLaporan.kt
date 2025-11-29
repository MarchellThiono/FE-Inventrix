package com.example.inventrix.Model

data class ReqFilterLaporan(
    val jenis: String = "",
    val tanggalMulai: String = "",
    val tanggalSelesai: String = "",
    val namaBarang: String = "",
    val createdBy: String = "",
    val page: Int = 0,
    val size: Int = 50
)
