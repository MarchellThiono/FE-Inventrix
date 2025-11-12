package com.example.inventrix.Model

import com.google.gson.annotations.SerializedName

data class ResTampilDetail(

	@field:SerializedName("pesan")
	val pesan: String? = null,

	@field:SerializedName("data")
	val data: DetailData? = null
)

data class DetailData(

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

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("hargaBeli")
	val hargaBeli: Any? = null,

	@field:SerializedName("merek")
	val merek: String? = null
)
