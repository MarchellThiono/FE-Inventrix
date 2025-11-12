package com.example.inventrix.Model

import com.google.gson.annotations.SerializedName

data class LoginRes(

	@field:SerializedName("pesan")
	val pesan: String? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("token")
	val token: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
