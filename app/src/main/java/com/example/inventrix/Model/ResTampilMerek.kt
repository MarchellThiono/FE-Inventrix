package com.example.inventrix.Model

import com.google.gson.annotations.SerializedName

data class ResTampilMerek(

	@field:SerializedName("pesan")
	val pesan: String? = null,

	@field:SerializedName("data")
	val data: List<DataMerek?>? = null
)

data class DataMerek(

	@field:SerializedName("namaMerek")
	val namaMerek: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
