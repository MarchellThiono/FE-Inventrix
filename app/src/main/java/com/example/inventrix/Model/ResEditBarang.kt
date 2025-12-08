package com.example.inventrix.Model

import com.google.gson.annotations.SerializedName

data class ResEditBarang(
	@SerializedName("pesan")
	val pesan: String? = null,

	@SerializedName("data")
	val data: EditData? = null
)

data class EditData(

	@SerializedName("id")
	val id: Int? = null,

	@SerializedName("kodeBarang")
	val kodeBarang: String? = null,

	@SerializedName("namaBarang")
	val namaBarang: String? = null,

	@SerializedName("kategori")
	val kategori: String? = null,   // ✔ DITAMBAHKAN

	@SerializedName("merek")
	val merek: Merek? = null,

	@SerializedName("hargaBeli")
	val hargaBeli: Double? = null,

	@SerializedName("hargaJual")
	val hargaJual: Double? = null,

	@SerializedName("stokToko")
	val stokToko: Int? = null,

	@SerializedName("stokGudang")
	val stokGudang: Int? = null,

	@SerializedName("stokMinimum")
	val stokMinimum: Int? = null,   // ✔ DITAMBAHKAN

	@SerializedName("deskripsi")
	val deskripsi: String? = null,

	@SerializedName("imageUrl")
	val imageUrl: String? = null,
)


data class Merek(
	@SerializedName("id")
	val id: Int? = null,

	@SerializedName("namaMerek")
	val namaMerek: String? = null
)
