package com.example.inventrix.Model

import com.google.gson.annotations.SerializedName

data class ResTampilDetail(

	@SerializedName("pesan")
	val pesan: String? = null,

	@SerializedName("data")
	val data: DetailData? = null
)

data class DetailData(

	@SerializedName("id")
	val id: Int? = null,

	@SerializedName("kodeBarang")
	val kodeBarang: String? = null,

	@SerializedName("namaBarang")
	val namaBarang: String? = null,

	@SerializedName("merek")
	val merek: String? = null,

	@SerializedName("imageUrl")
	val imageUrl: String? = null,

	// Harga asli dari BE (Double)
	@SerializedName("hargaBeli")
	val hargaBeli: Double? = null,

	@SerializedName("hargaJual")
	val hargaJual: Double? = null,

	// Harga formatted (string)
	@SerializedName("hargaBeliFormatted")
	val hargaBeliFormatted: String? = null,

	@SerializedName("hargaJualFormatted")
	val hargaJualFormatted: String? = null,

	@SerializedName("stokToko")
	val stokToko: Int? = null,

	@SerializedName("stokGudang")
	val stokGudang: Int? = null,

	@SerializedName("deskripsi")
	val deskripsi: String? = null
)
