package com.example.inventrix.Model

import com.google.gson.annotations.SerializedName

data class ResListPermintaanItem(

	@SerializedName("id")
	val id: Int? = null,

	@SerializedName("createdAt")
	val tanggal: String?,
	val totalItem: Int,
	val status: String
)