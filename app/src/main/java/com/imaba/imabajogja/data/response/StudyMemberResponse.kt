package com.imaba.imabajogja.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class StudyMemberResponse(

	@field:SerializedName("data")
	val data: CurrentStudyData? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
) : Parcelable

@Parcelize
data class CurrentStudyData(

	@field:SerializedName("member_id")
	val memberId: Int? = null,

	@field:SerializedName("program_study")
	val programStudy: CurrentProgramStudy? = null,

	@field:SerializedName("faculty_id")
	val facultyId: Int? = null,

	@field:SerializedName("university_id")
	val universityId: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("university")
	val university: CurrentUniversity? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("program_study_id")
	val programStudyId: Int? = null,

	@field:SerializedName("faculty")
	val faculty: CurrentFaculty? = null
) : Parcelable

@Parcelize
data class CurrentProgramStudy(

	@field:SerializedName("faculty_id")
	val facultyId: Int? = null,

	@field:SerializedName("university_id")
	val universityId: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("jenjang")
	val jenjang: String? = null
) : Parcelable

@Parcelize
data class CurrentFaculty(

	@field:SerializedName("university_id")
	val universityId: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
) : Parcelable

@Parcelize
data class CurrentUniversity(

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
) : Parcelable