package com.example.inventrix.Model

import com.google.gson.annotations.SerializedName

data class TampilBarangRes(

	@SerializedName("pesan")
	val pesan: String? = null,

	@SerializedName("totalItems")
	val totalItems: Int? = null,

	@SerializedName("data")
	val data: List<DataItem?>? = null,

	@SerializedName("totalPages")
	val totalPages: Int? = null,

	@SerializedName("currentPage")
	val currentPage: Int? = null
)

data class DataItem(

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

	// BE kirim Double, bukan String
	@SerializedName("hargaJual")
	val hargaJual: Double? = null,

	@SerializedName("stokToko")
	val stokToko: Int? = null,

	@SerializedName("stokGudang")
	val stokGudang: Int? = null
)
