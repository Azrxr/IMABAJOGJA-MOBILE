package com.imaba.imabajogja.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Member

@Parcelize
data class ProfileUpdateResponse(

	@field:SerializedName("data")
	val data: UpdateProfileResult,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
) : Parcelable

@Parcelize
data class MemberUpdate(

	@field:SerializedName("member_type")
	val memberType: String,

	@field:SerializedName("no_member")
	val noMember: String,

	@field:SerializedName("regency_id")
	val regencyId: Int,

	@field:SerializedName("gender")
	val gender: String,

	@field:SerializedName("profile_img_url")
	val profileImgUrl: String,

	@field:SerializedName("angkatan")
	val angkatan: String,

	@field:SerializedName("nisn")
	val nisn: Int,

	@field:SerializedName("profile_img_path")
	val profileImgPath: String,

	@field:SerializedName("is_studyng")
	val isStudyng: Int,

	@field:SerializedName("agama")
	val agama: String,

	@field:SerializedName("kode_pos")
	val kodePos: Int,

	@field:SerializedName("tahun_lulus")
	val tahunLulus: Int,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("full_address")
	val fullAddress: String,

	@field:SerializedName("scholl_origin")
	val schollOrigin: String,

	@field:SerializedName("tempat")
	val tempat: String,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("province_id")
	val provinceId: Int,

	@field:SerializedName("phone_number")
	val phoneNumber: Int,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("fullname")
	val fullname: String,

	@field:SerializedName("district_id")
	val districtId: Int,

	@field:SerializedName("tanggal_lahir")
	val tanggalLahir: String
) : Parcelable

@Parcelize
data class UpdateProfileResult(

	@field:SerializedName("user")
	val user: UserUpdate
) : Parcelable

@Parcelize
data class UserUpdate(

	@field:SerializedName("role")
	val role: String,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("ban_reason")
	val banReason: String,

	@field:SerializedName("member")
	val member: MemberUpdate,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("email_verified_at")
	val emailVerifiedAt: Boolean,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("banned")
	val banned: Int,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("username")
	val username: String
) : Parcelable
