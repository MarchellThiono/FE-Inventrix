package com.example.inventrix.Model

import com.google.gson.annotations.SerializedName

data class ResTambahMerek(

	@field:SerializedName("pesan")
	val pesan: String? = null,

	@field:SerializedName("data")
	val data: MerekData? = null
)

data class MerekData(

	@field:SerializedName("namaMerek")
	val namaMerek: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
