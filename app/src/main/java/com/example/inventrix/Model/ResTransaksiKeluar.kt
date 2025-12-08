package com.example.inventrix.Model

import com.google.gson.annotations.SerializedName

data class ResTransaksiKeluar(

	@SerializedName("transaksiId")
	val transaksiId: Int? = null,

	@SerializedName("laporanId")
	val laporanId: Long? = null,

	@SerializedName("pesan")
	val pesan: String? = null,

	@SerializedName("totalHargaFormatted")
	val totalHargaFormatted: String? = null,

	@SerializedName("totalHarga")
	val totalHarga: Int? = null
)
