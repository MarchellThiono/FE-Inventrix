package com.example.inventrix.Model

data class Notifikasi(
    val id: Long,
    val userId: Long,
    val judul: String,
    val pesan: String,
    val tipe: String,    // STOK_MINIM, MUTASI, DLL
    val tanggal: String  // "2025-11-26 19:00:00"
)
