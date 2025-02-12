package com.imaba.imabajogja.data.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("registerResult")
	val registerResult: RegisterResult,

	@field:SerializedName("status")
	val error: Boolean
)

data class RegisterResult(

	@field:SerializedName("role")
	val role: String,

	@field:SerializedName("userID")
	val userID: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("username")
	val username: String
)
