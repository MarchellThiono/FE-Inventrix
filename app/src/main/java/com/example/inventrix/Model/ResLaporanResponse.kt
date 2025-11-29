package com.example.inventrix.Model

import com.google.gson.annotations.SerializedName

data class ResLaporanResponse(

	@field:SerializedName("number")
	val number: Int? = null,

	@field:SerializedName("last")
	val last: Boolean? = null,

	@field:SerializedName("size")
	val size: Int? = null,

	@field:SerializedName("numberOfElements")
	val numberOfElements: Int? = null,

	@field:SerializedName("totalPages")
	val totalPages: Int? = null,

	@field:SerializedName("pageable")
	val pageable: Pageable? = null,

	@field:SerializedName("sort")
	val sort: Sort? = null,

	@field:SerializedName("content")
	val content: List<ContentItem?>? = null,

	@field:SerializedName("first")
	val first: Boolean? = null,

	@field:SerializedName("totalElements")
	val totalElements: Int? = null,

	@field:SerializedName("empty")
	val empty: Boolean? = null
)

data class Sort(

	@field:SerializedName("unsorted")
	val unsorted: Boolean? = null,

	@field:SerializedName("sorted")
	val sorted: Boolean? = null,

	@field:SerializedName("empty")
	val empty: Boolean? = null
)

data class Pageable(

	@field:SerializedName("paged")
	val paged: Boolean? = null,

	@field:SerializedName("pageNumber")
	val pageNumber: Int? = null,

	@field:SerializedName("offset")
	val offset: Int? = null,

	@field:SerializedName("pageSize")
	val pageSize: Int? = null,

	@field:SerializedName("unpaged")
	val unpaged: Boolean? = null,

	@field:SerializedName("sort")
	val sort: Sort? = null
)

data class ContentItem(
	@SerializedName("transaksiId") val transaksiId: Int?,
	@SerializedName("createdBy") val createdBy: String?,
	@SerializedName("totalItem") val totalItem: Int?,
	@SerializedName("supplier") val supplier: Any?,
	@SerializedName("jenis") val jenis: String?,
	@SerializedName("id") val id: Int?,
	@SerializedName("tanggal") val tanggal: String?
)
