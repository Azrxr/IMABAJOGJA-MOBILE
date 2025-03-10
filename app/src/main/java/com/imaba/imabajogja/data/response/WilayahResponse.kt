package com.imaba.imabajogja.data.response

import com.google.gson.annotations.SerializedName

data class WilayahResponse(

	@field:SerializedName("data")
	val data: List<WilayahItem>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class WilayahItem(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: Int
)
