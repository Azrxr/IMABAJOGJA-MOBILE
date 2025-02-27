package com.imaba.imabajogja.data.response

import com.google.gson.annotations.SerializedName

data class HomeResponse(

	@field:SerializedName("data")
	val data: OrganizationProfile,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class OrganizationFile(

	@field:SerializedName("file_url")
	val fileUrl: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	val title: String
)

data class OrganizationProfile(

	@field:SerializedName("vision")
	val vision: String,

	@field:SerializedName("mission")
	val mission: String,

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("contact_phone")
	val contactPhone: String,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("contact_phone2")
	val contactPhone2: String,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("files")
	val files: List<OrganizationFile>,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("contact_email")
	val contactEmail: String
)
