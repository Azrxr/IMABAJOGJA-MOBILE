package com.imaba.imabajogja.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class ImportMemberResponse(

	@field:SerializedName("success_count")
	val successCount: Int,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("error_count")
	val errorCount: Int,

	@field:SerializedName("success_data")
	val successData: List<SuccessDataItem>,

	@field:SerializedName("error_data")
	val errorData: List<ErrorDataItem>
) : Parcelable

@Parcelize
data class ErrorDataItem(

	@field:SerializedName("no_member")
	val noMember: String,

	@field:SerializedName("row")
	val row: Int,

	@field:SerializedName("error")
	val error: String
) : Parcelable

@Parcelize
data class SuccessDataItem(

	@field:SerializedName("no_member")
	val noMember: String,

	@field:SerializedName("action")
	val action: String
) : Parcelable
