package com.example.inventrix.Model

import com.google.gson.annotations.SerializedName

data class ResEditMerek(

	@field:SerializedName("pesan")
	val pesan: String? = null,

	@field:SerializedName("data")
	val data: Data? = null
)

data class Data(

	@field:SerializedName("namaMerek")
	val namaMerek: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
