package com.example.inventrix.Model

data class ReqTambahBarang(
    val kodeBarang : String,
    val namaBarang : String,
    val merek : String,
    val hargaBeli : Double,
    val hargaJual : Double,
    val deskripsi : String,
    val imageUrl : String
)
