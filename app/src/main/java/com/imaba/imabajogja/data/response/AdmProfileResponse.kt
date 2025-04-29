package com.imaba.imabajogja.data.response

import com.google.gson.annotations.SerializedName

data class AdmProfileResponse(

	@field:SerializedName("data")
	val data: AdmDataUser? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class AdmRegency(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class AdmDistrict(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class AdmDataUser(

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("ban_reason")
	val banReason: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("admin")
	val admin: AdminProfile? = null,

	@field:SerializedName("email_verified_at")
	val emailVerifiedAt: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("banned")
	val banned: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)

data class AdmProvincy(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class AdminProfile(

	@field:SerializedName("provincy")
	val provincy: AdmProvincy? = null,

	@field:SerializedName("regency_id")
	val regencyId: Int? = null,

	@field:SerializedName("profile_img_url")
	val profileImgUrl: String? = null,

	@field:SerializedName("profile_img_path")
	val profileImgPath: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("regency")
	val regency: AdmRegency? = null,

	@field:SerializedName("full_address")
	val fullAddress: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("district")
	val district: AdmDistrict? = null,

	@field:SerializedName("phone_number")
	val phoneNumber: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("fullname")
	val fullname: String? = null,

	@field:SerializedName("district_id")
	val districtId: Int? = null,

	@field:SerializedName("provincy_id")
	val provincyId: Int? = null
)
