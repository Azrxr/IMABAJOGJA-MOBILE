package com.imaba.imabajogja.data.response

import com.google.gson.annotations.SerializedName

data class SuccesResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
