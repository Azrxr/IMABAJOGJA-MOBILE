package com.imaba.imabajogja.data.response

import com.google.gson.annotations.SerializedName

data class MembersResponse(

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class DataItemMember(

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
	val angkatan: Int,

	@field:SerializedName("nisn")
	val nisn: Int,

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
)

data class Data(

	@field:SerializedName("per_page")
	val perPage: Int,

	@field:SerializedName("data")
	val data: List<DataItemMember>,

	@field:SerializedName("last_page")
	val lastPage: Int,

	@field:SerializedName("next_page_url")
	val nextPageUrl: String,

	@field:SerializedName("prev_page_url")
	val prevPageUrl: String,

	@field:SerializedName("first_page_url")
	val firstPageUrl: String,

	@field:SerializedName("path")
	val path: String,

	@field:SerializedName("total")
	val total: Int,

	@field:SerializedName("last_page_url")
	val lastPageUrl: String,

	@field:SerializedName("from")
	val from: Int,

	@field:SerializedName("links")
	val links: List<LinksItem>,

	@field:SerializedName("to")
	val to: Int,

	@field:SerializedName("current_page")
	val currentPage: Int
)

data class LinksItem(

	@field:SerializedName("active")
	val active: Boolean,

	@field:SerializedName("label")
	val label: String,

	@field:SerializedName("url")
	val url: String
)
