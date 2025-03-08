package com.imaba.imabajogja.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class MembersResponse(

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
) : Parcelable

@Parcelize
data class DataItemMember(

	@field:SerializedName("member_type")
	val memberType: String,

	@field:SerializedName("gender")
	val gender: String,

	@field:SerializedName("is_studyng")
	val isStudyng: Int,

	@field:SerializedName("agama")
	val agama: String,

	@field:SerializedName("tahun_lulus")
	val tahunLulus: Int,

	@field:SerializedName("regency")
	val regency: String,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("full_address")
	val fullAddress: String,

	@field:SerializedName("province")
	val province: String,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("tanggal_lahir")
	val tanggalLahir: String,

	@field:SerializedName("profile_img_url")
	val profileImgUrl: String,

	@field:SerializedName("no_member")
	val noMember: String,

	@field:SerializedName("study_members")
	val studyMembers: List<StudyMembersItem>,

	@field:SerializedName("angkatan")
	val angkatan: String,

	@field:SerializedName("nisn")
	val nisn: Int,

	@field:SerializedName("kode_pos")
	val kodePos: Int,

	@field:SerializedName("scholl_origin")
	val schollOrigin: String,

	@field:SerializedName("tempat")
	val tempat: String,

	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("district")
	val district: String,

	@field:SerializedName("phone_number")
	val phoneNumber: String,

	@field:SerializedName("fullname")
	val fullname: String,

	@field:SerializedName("study_plans")
	val studyPlans: List<StudyPlansItem>
) : Parcelable

@Parcelize
data class Data(

	@field:SerializedName("first_page_url")
	val firstPageUrl: String,

	@field:SerializedName("path")
	val path: String,

	@field:SerializedName("per_page")
	val perPage: Int,

	@field:SerializedName("total")
	val total: Int,

	@field:SerializedName("data")
	val data: List<DataItemMember>,

	@field:SerializedName("last_page")
	val lastPage: Int,

	@field:SerializedName("last_page_url")
	val lastPageUrl: String,

	@field:SerializedName("next_page_url")
	val nextPageUrl: String,

	@field:SerializedName("from")
	val from: Int,

	@field:SerializedName("to")
	val to: Int,

	@field:SerializedName("prev_page_url")
	val prevPageUrl: String,

	@field:SerializedName("current_page")
	val currentPage: Int
) : Parcelable

@Parcelize
data class StudyPlansItem(

	@field:SerializedName("program_study")
	val programStudy: String,

	@field:SerializedName("university")
	val university: String,

	@field:SerializedName("status")
	val status: String
) : Parcelable

@Parcelize
data class StudyMembersItem(

	@field:SerializedName("program_study")
	val programStudy: String,

	@field:SerializedName("university")
	val university: String,

	@field:SerializedName("faculty")
	val faculty: String
) : Parcelable
