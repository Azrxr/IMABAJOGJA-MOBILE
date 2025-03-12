package com.imaba.imabajogja.data.response

import com.google.gson.annotations.SerializedName

data class StudyPlansResponse(

	@field:SerializedName("data")
	val data: List<StudyPlans>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class University(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: Int
)

data class StudyPlans(

	@field:SerializedName("member_id")
	val memberId: Int,

	@field:SerializedName("program_study")
	val programStudy: ProgramStudy,

	@field:SerializedName("university_id")
	val universityId: Int,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("university")
	val university: University,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("program_study_id")
	val programStudyId: Int,

	@field:SerializedName("status")
	val status: String
)

data class ProgramStudy(

	@field:SerializedName("faculty_id")
	val facultyId: Int,

	@field:SerializedName("university_id")
	val universityId: Int,


	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("jenjang")
	val jenjang: String
)
