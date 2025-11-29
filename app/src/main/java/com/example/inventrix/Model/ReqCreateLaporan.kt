package com.example.inventrix.Model

data class ReqCreateLaporan(
    val jenis: String,
    val supplier: String?,
    val items: List<ReqLaporanItem>
)
