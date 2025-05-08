package com.imaba.imabajogja.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class MemberDetailResponse(

	@field:SerializedName("data")
	val data: DataItemMember? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
) : Parcelable

@Parcelize
data class DocumentsItem(

	@field:SerializedName("ktp_path")
	val ktpPath: String? = null,

	@field:SerializedName("raport_legalisir_path")
	val raportLegalisirPath: String? = null,

	@field:SerializedName("surat_penghasilan_ortu_path")
	val suratPenghasilanOrtuPath: String? = null,

	@field:SerializedName("sertifikat_prestasi_path")
	val sertifikatPrestasiPath: String? = null,

	@field:SerializedName("skhu_legalisir_path")
	val skhuLegalisirPath: String? = null,

	@field:SerializedName("surat_tidak_mampu_path")
	val suratTidakMampuPath: String? = null,

	@field:SerializedName("kartu_pkh_path")
	val kartuPkhPath: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("surat_rekom_kades_path")
	val suratRekomKadesPath: String? = null,

	@field:SerializedName("ijazah_skl_path")
	val ijazahSklPath: String? = null,

	@field:SerializedName("surat_keterangan_baik_path")
	val suratKeteranganBaikPath: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("kk_legalisir_path")
	val kkLegalisirPath: String? = null,

	@field:SerializedName("foto_keluarga_path")
	val fotoKeluargaPath: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("surat_tidak_pdam_path")
	val suratTidakPdamPath: String? = null,

	@field:SerializedName("member_id")
	val memberId: Int? = null,

	@field:SerializedName("kartu_kks_path")
	val kartuKksPath: String? = null,

	@field:SerializedName("kk_path")
	val kkPath: String? = null,

	@field:SerializedName("akte_legalisir_path")
	val akteLegalisirPath: String? = null,

	@field:SerializedName("surat_pajak_bumi_bangunan_path")
	val suratPajakBumiBangunanPath: String? = null,

	@field:SerializedName("skck_path")
	val skckPath: String? = null,

	@field:SerializedName("kartu_kip_path")
	val kartuKipPath: String? = null,

	@field:SerializedName("raport_path")
	val raportPath: String? = null,

	@field:SerializedName("ijazah_path")
	val ijazahPath: String? = null,

	@field:SerializedName("surat_baik_path")
	val suratBaikPath: String? = null,

	@field:SerializedName("photo_3x4_path")
	val photo3x4Path: String? = null,

	@field:SerializedName("documents_url")
	val documentsUrl: DocumentsUrl? = null,

	@field:SerializedName("token_listrik_path")
	val tokenListrikPath: String? = null,

	@field:SerializedName("home_photo")
	val homePhoto: List<HomePhotoItem?>? = null
) : Parcelable

