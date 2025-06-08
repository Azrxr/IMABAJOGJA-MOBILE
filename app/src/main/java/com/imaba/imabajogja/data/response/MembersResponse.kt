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
	val message: String,

	@field:SerializedName("total_member")
	val totalMember: Int,

	@field:SerializedName("total_member_regular")
	val totalMemberRegular: Int,

	@field:SerializedName("total_member_demissioner")
	val totalMemberDemissioner: Int,

	@field:SerializedName("total_member_prospective")
	val totalMemberProspective: Int,

	@field:SerializedName("total_member_management")
	val totalMemberManagement: Int,

	@field:SerializedName("total_member_special")
	val totalMemberSpecial: Int,

	@field:SerializedName("total_study_plan")
	val totalStudyPlan: Int,

	@field:SerializedName("total_plan_pending")
	val totalPlanPending: Int,

	@field:SerializedName("total_plan_accepted")
	val totalPlanAccepted: Int,

	@field:SerializedName("total_plan_rejected")
	val totalPlanRejected: Int,

	@field:SerializedName("total_plan_active")
	val totalPlanActive: Int,

	@field:SerializedName("total_univ_plan")
	val totalUnivPlanSelect: Int,

) : Parcelable

@Parcelize
data class DataItemMember(

	@field:SerializedName("member_type")
	val memberType: String? = null,

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("is_studyng")
	val isStudyng: Int,

	@field:SerializedName("agama")
	val agama: String? = null,

	@field:SerializedName("tahun_lulus")
	val tahunLulus: Int? = null,

	@field:SerializedName("regency")
	val regency: String? = null,
	@field:SerializedName("regencyId")
	val regencyId: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("full_address")
	val fullAddress: String? = null,

	@field:SerializedName("province")
	val province: String? = null,
	@field:SerializedName("provinceId")
	val provinceId: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("tanggal_lahir")
	val tanggalLahir: String? = null,

	@field:SerializedName("profile_img_url")
	val profileImgUrl: String? = null,

	@field:SerializedName("no_member")
	val noMember: String? = null,

	@field:SerializedName("study_members")
	val studyMembers: List<StudyMembersItem>? = null,

	@field:SerializedName("angkatan")
	val angkatan: String? = null,

	@field:SerializedName("nisn")
	val nisn: String? = null,

	@field:SerializedName("kode_pos")
	val kodePos: Int? = null,

	@field:SerializedName("scholl_origin")
	val schollOrigin: String? = null,

	@field:SerializedName("tempat")
	val tempat: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("district")
	val district: String? = null,
	@field:SerializedName("districtId")
	val districtId: Int? = null,

	@field:SerializedName("phone_number")
	val phoneNumber: String? = null,

	@field:SerializedName("fullname")
	val fullname: String? = null,

	@field:SerializedName("study_plans")
	val studyPlans: List<StudyPlansItem>? = null,

	@field:SerializedName("documents")
	val documents: List<DataDocument>? = null,
//	val documents: DataDocument? = null,

	@field:SerializedName("berkas_progress")
	val berkasProgress: String? = null,

	@field:SerializedName("has_home_photos")
	val hasHomePhotos: Boolean? = null,

	@field:SerializedName("berkas_lengkap")
	val berkasLengkap: Boolean? = null,

	@field:SerializedName("status_kuliah")
	val statusKuliah: String? = null,

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

	@field:SerializedName("member_id")
	val memberId: Int? = null,
	@field:SerializedName("study_plan_id")
	val studyPlanId: Int? = null,

	@field:SerializedName("program_study")
	val programStudy: String? = null,
	@field:SerializedName("program_studyId")
	val programStudyId: Int? = null,

	@field:SerializedName("university")
	val university: String? = null,
	@field:SerializedName("universityId")
	val universityId: Int? = null,

	@field:SerializedName("status")
    var status: String? = null
) : Parcelable

@Parcelize
data class StudyMembersItem(

	@field:SerializedName("member_id")
	val memberId: Int? = null,
	@field:SerializedName("study_member_id")
	val studyMemberId: Int? = null,

	@field:SerializedName("program_study")
	val programStudy: String? = null,
	@field:SerializedName("program_studyId")
	val programStudyId: Int? = null,

	@field:SerializedName("university")
	val university: String? = null,
	@field:SerializedName("universityId")
	val universityId: Int? = null,

	@field:SerializedName("facultyId")
	val facultyId: Int? = null,
	@field:SerializedName("faculty")
	val faculty: String? = null
) : Parcelable
