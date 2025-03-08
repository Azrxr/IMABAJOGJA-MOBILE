package com.imaba.imabajogja.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class ProfileResponse(

	@field:SerializedName("data")
	val data: ProfileUser,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
) : Parcelable

@Parcelize
data class ProfileUser(

	@field:SerializedName("member_type")
	val memberType: String,

	@field:SerializedName("role")
	val role: String,

	@field:SerializedName("gender")
	val gender: String,

	@field:SerializedName("is_studyng")
	val isStudyng: Int,

	@field:SerializedName("agama")
	val agama: String,

	@field:SerializedName("tahun_lulus")
	val tahunLulus: Int,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("regency")
	val regency: String,

	@field:SerializedName("full_address")
	val fullAddress: String,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("province")
	val province: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("banned")
	val banned: Int,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("tanggal_lahir")
	val tanggalLahir: String,

	@field:SerializedName("no_member")
	val noMember: String,

	@field:SerializedName("profile_img_url")
	val profileImgUrl: String,

	@field:SerializedName("angkatan")
	val angkatan: Int,

	@field:SerializedName("nisn")
	val nisn: Int,

	@field:SerializedName("kode_pos")
	val kodePos: Int,

	@field:SerializedName("email_verified_at")
	val emailVerifiedAt: Boolean,

	@field:SerializedName("scholl_origin")
	val schollOrigin: String,

	@field:SerializedName("tempat")
	val tempat: String,

	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("ban_reason")
	val banReason: String,

	@field:SerializedName("district")
	val district: String,

	@field:SerializedName("phone_number")
	val phoneNumber: Int,

	@field:SerializedName("fullname")
	val fullname: String,

	@field:SerializedName("username")
	val username: String
) : Parcelable
