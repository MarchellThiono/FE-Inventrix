package com.example.inventrix.Model

data class ReqPermintaanBarang (
     val nama : String,
     val merek : String,
     val kodeBarang: String,
     val Stok : Int,
     val imageUrl: String?,
    val status: String
)