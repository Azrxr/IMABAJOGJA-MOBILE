package com.imaba.imabajogja.data.response

import com.google.gson.annotations.SerializedName

data class ProgramStudyImportResponse(

	@field:SerializedName("report")
	val report: Report,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class Report(

	@field:SerializedName("fail")
	val fail: Int,

	@field:SerializedName("success")
	val success: Int,

	@field:SerializedName("activities")
	val activities: List<ActivitiesItem>
)

data class ActivitiesItem(

	@field:SerializedName("row")
	val row: Int,

	@field:SerializedName("actions")
	val actions: List<String>
)
