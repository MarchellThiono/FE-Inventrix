package com.example.inventrix.Model

import com.google.gson.annotations.SerializedName

data class ResDetailRiwayat(

	@field:SerializedName("totalHargaFormatted")
	val totalHargaFormatted: String? = null,

	@field:SerializedName("jenis")
	val jenis: String? = null,

	@field:SerializedName("totalHarga")
	val totalHarga: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("tanggal")
	val tanggal: String? = null,

	@field:SerializedName("items")
	val items: List<ItemsItem?>? = null
)

data class ItemsItem(

	@field:SerializedName("barangId")
	val barangId: Int? = null,

	@field:SerializedName("hargaSatuan")
	val hargaSatuan: Int? = null,

	@field:SerializedName("subtotal")
	val subtotal: Int? = null,

	@field:SerializedName("stokTokoAfter")
	val stokTokoAfter: Int? = null,

	@field:SerializedName("qty")
	val qty: Int? = null,

	@field:SerializedName("stokTokoBefore")
	val stokTokoBefore: Int? = null,

	@field:SerializedName("namaBarang")
	val namaBarang: String? = null,

	@field:SerializedName("stokGudangAfter")
	val stokGudangAfter: Int? = null,

	@field:SerializedName("hargaSatuanFormatted")
	val hargaSatuanFormatted: String? = null,

	@field:SerializedName("stokGudangBefore")
	val stokGudangBefore: Int? = null,

	@field:SerializedName("subtotalFormatted")
	val subtotalFormatted: String? = null
)
