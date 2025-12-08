package com.example.inventrix.Model

import com.google.gson.annotations.SerializedName

data class ResKategoriList(
    @SerializedName("pesan") val pesan: String?,
    @SerializedName("data") val data: List<KategoriData?>?
)

data class KategoriData(
    @SerializedName("id") val id: Int,
    @SerializedName("nama") val nama: String,
    @SerializedName("kodeAwal") val kodeAwal: String
)
