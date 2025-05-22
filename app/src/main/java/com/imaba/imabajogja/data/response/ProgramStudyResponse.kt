package com.imaba.imabajogja.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class ProgramStudyResponse(

	@field:SerializedName("data")
	val data: List<DataItemProgramStudy?>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
) : Parcelable

@Parcelize
data class DataItemProgramStudy(

	@field:SerializedName("program_study")
	val programStudy: String? = null,

	@field:SerializedName("universityId")
	val universityId: Int? = null,

	@field:SerializedName("facultyId")
	val facultyId: Int? = null,

	@field:SerializedName("program_studyId")
	val programStudyId: Int? = null,

	@field:SerializedName("university")
	val university: String? = null,

	@field:SerializedName("jenjang")
	val jenjang: String? = null,

	@field:SerializedName("faculty")
	val faculty: String? = null
) : Parcelable
