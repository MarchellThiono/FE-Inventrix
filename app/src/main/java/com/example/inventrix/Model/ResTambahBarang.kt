package com.example.inventrix.Model

import com.google.gson.annotations.SerializedName

data class ResTambahBarang(

	@field:SerializedName("pesan")
	val pesan: String? = null,

	@field:SerializedName("data")
	val data: TambahData? = null
)

data class TambahData(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("kodeBarang")
	val kodeBarang: String? = null,

	@field:SerializedName("namaBarang")
	val namaBarang: String? = null,

	@field:SerializedName("merek")
	val merek: MerekItem? = null, // <── ubah dari String jadi objek

	@field:SerializedName("hargaBeli")
	val hargaBeli: Double? = null,

	@field:SerializedName("hargaJual")
	val hargaJual: Double? = null,

	@field:SerializedName("stokToko")
	val stokToko: Int? = null,

	@field:SerializedName("stokGudang")
	val stokGudang: Int? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("imageUrl")
	val imageUrl: String? = null,

	@field:SerializedName("hargaBeliFormatted")
	val hargaBeliFormatted: String? = null,

	@field:SerializedName("hargaJualFormatted")
	val hargaJualFormatted: String? = null
)

data class MerekItem(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("namaMerek")
	val namaMerek: String? = null
)
