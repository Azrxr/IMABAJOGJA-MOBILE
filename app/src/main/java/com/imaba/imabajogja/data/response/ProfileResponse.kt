package com.imaba.imabajogja.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class ProfileResponse(

	@field:SerializedName("data")
	val data: ProfileUser? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
) : Parcelable

@Parcelize
data class ProfileUser(

	@field:SerializedName("member_type")
	val memberType: String? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("is_studyng")
	val isStudyng: Int? = null,

	@field:SerializedName("agama")
	val agama: String? = null,

	@field:SerializedName("tahun_lulus")
	val tahunLulus: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("regency")
	val regency: String? = null,

	@field:SerializedName("full_address")
	val fullAddress: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("province")
	val province: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("banned")
	val banned: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("tanggal_lahir")
	val tanggalLahir: String? = null,

	@field:SerializedName("no_member")
	val noMember: String? = null,

	@field:SerializedName("profile_img_url")
	val profileImgUrl: String? = null,

	@field:SerializedName("angkatan")
	val angkatan: String? = null,

	@field:SerializedName("nisn")
	val nisn: Int? = null,

	@field:SerializedName("kode_pos")
	val kodePos: Int? = null,

	@field:SerializedName("email_verified_at")
	val emailVerifiedAt: String? = null,

	@field:SerializedName("scholl_origin")
	val schollOrigin: String? = null,

	@field:SerializedName("tempat")
	val tempat: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("ban_reason")
	val banReason: String? = null,

	@field:SerializedName("district")
	val district: String? = null,

	@field:SerializedName("phone_number")
	val phoneNumber: String? = null,

	@field:SerializedName("fullname")
	val fullname: String? = null,

	@field:SerializedName("username")
	val username: String? = null
) : Parcelable
