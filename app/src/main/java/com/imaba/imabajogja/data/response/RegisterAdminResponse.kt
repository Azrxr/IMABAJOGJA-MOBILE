package com.imaba.imabajogja.data.response

import com.google.gson.annotations.SerializedName

data class RegisterAdminResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("registerResult")
	val registerResult: RegisterAdminResult
)

data class RegisterAdminResult(

	@field:SerializedName("role")
	val role: String,

	@field:SerializedName("phone_number")
	val phoneNumber: String,

	@field:SerializedName("fullname")
	val fullname: String,

	@field:SerializedName("userID")
	val userID: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("username")
	val username: String
)
