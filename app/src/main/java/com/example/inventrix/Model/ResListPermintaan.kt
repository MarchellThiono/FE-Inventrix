package com.example.inventrix.Model

import com.google.gson.annotations.SerializedName

data class ResListPermintaanItem(

	@field:SerializedName("barang")
	val barang: Barang? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("keterangan")
	val keterangan: String? = null,

	@field:SerializedName("completedAt")
	val completedAt: Any? = null,

	@field:SerializedName("jumlah")
	val jumlah: Int? = null,

	@field:SerializedName("createdBy")
	val createdBy: String? = null,

	@field:SerializedName("processedAt")
	val processedAt: Any? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("processedBy")
	val processedBy: Any? = null,

	@field:SerializedName("completedBy")
	val completedBy: Any? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Barang(

	@field:SerializedName("stokToko")
	val stokToko: Int? = null,

	@field:SerializedName("stokGudang")
	val stokGudang: Int? = null,

	@field:SerializedName("hargaJualFormatted")
	val hargaJualFormatted: String? = null,

	@field:SerializedName("hargaBeliFormatted")
	val hargaBeliFormatted: String? = null,

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
	val merek: Merek? = null   // PAKAI class Merek yang sudah ada!
)

