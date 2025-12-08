package com.example.inventrix.Model

data class ResDetailPermintaan(
    val id: Int,
    val keterangan: String?,
    val createdAt: String?,
    val createdBy: String?,
    val status: StatusValue,
    val items: List<ItemDetail>
)

data class StatusValue(val value: String?)

data class ItemDetail(
    val id: Int?,
    val barangId: Int?,
    val namaBarang: String?,
    val kodeBarang: String?,    // ← tambahkan ini
    val jumlah: Int?,
    val keterangan: String?
)

