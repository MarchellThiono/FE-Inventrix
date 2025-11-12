package com.example.inventrix.Model

import com.google.gson.annotations.SerializedName

data class TampilBarangRes(

	@field:SerializedName("pesan")
	val pesan: String? = null,

	@field:SerializedName("totalItems")
	val totalItems: Int? = null,

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("totalPages")
	val totalPages: Int? = null,

	@field:SerializedName("currentPage")
	val currentPage: Int? = null
)

data class DataItem(

	@field:SerializedName("stokToko")
	val stokToko: Int? = null,

	@field:SerializedName("stokGudang")
	val stokGudang: Int? = null,

	@field:SerializedName("imageUrl")
	val imageUrl: String? = null,

	@field:SerializedName("kodeBarang")
	val kodeBarang: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("namaBarang")
	val namaBarang: String? = null,

	@field:SerializedName("hargaJual")
	val hargaJual: Any? = null,

	@field:SerializedName("merek")
	val merek: String? = null
)
