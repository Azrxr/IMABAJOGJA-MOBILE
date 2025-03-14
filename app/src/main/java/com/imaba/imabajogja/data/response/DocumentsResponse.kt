package com.imaba.imabajogja.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class DocumentsResponse(

	@field:SerializedName("data")
	val data: DataDocument,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
) : Parcelable

@Parcelize
data class DataDocument(

	@field:SerializedName("member_id")
	val memberId: Int,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("documents_url")
	val documentsUrl: DocumentsUrl,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("home_photo")
	val homePhoto: List<HomePhotoItem>
) : Parcelable

@Parcelize
data class HomePhotoItem(

	@field:SerializedName("photo_img_path")
	val photoImgPath: String,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("photo_title")
	val photoTitle: String,

	@field:SerializedName("photo_img_url")
	val photoImgUrl: String,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("document_id")
	val documentId: Int
) : Parcelable

@Parcelize
data class DocumentsUrl(
	@field:SerializedName("ktp_path") val ktpPath: String? = null,
	@field:SerializedName("kartu_kks_path") val kartuKksPath: String? = null,
	@field:SerializedName("kk_path") val kkPath: String? = null,
	@field:SerializedName("akte_legalisir_path") val akteLegalisirPath: String? = null,
	@field:SerializedName("raport_legalisir_path") val raportLegalisirPath: String? = null,
	@field:SerializedName("surat_penghasilan_ortu_path") val suratPenghasilanOrtuPath: String? = null,
	@field:SerializedName("sertifikat_prestasi_path") val sertifikatPrestasiPath: String? = null,
	@field:SerializedName("surat_pajak_bumi_bangunan_path") val suratPajakBumiBangunanPath: String? = null,
	@field:SerializedName("skhu_legalisir_path") val skhuLegalisirPath: String? = null,
	@field:SerializedName("ijazah_legalisir_path") val ijazahLegalisirPath: String? = null,
	@field:SerializedName("surat_tidak_mampu_path") val suratTidakMampuPath: String? = null,
	@field:SerializedName("kartu_pkh_path") val kartuPkhPath: String? = null,
	@field:SerializedName("surat_rekom_kades_path") val suratRekomKadesPath: String? = null,
	@field:SerializedName("skck_path") val skckPath: String? = null,
	@field:SerializedName("ijazah_skl_path") val ijazahSklPath: String? = null,
	@field:SerializedName("kartu_kip_path") val kartuKipPath: String? = null,
	@field:SerializedName("raport_path") val raportPath: String? = null,
	@field:SerializedName("surat_keterangan_baik_path") val suratKeteranganBaikPath: String? = null,
	@field:SerializedName("ijazah_path") val ijazahPath: String? = null,
	@field:SerializedName("kk_legalisir_path") val kkLegalisirPath: String? = null,
	@field:SerializedName("surat_baik_path") val suratBaikPath: String? = null,
	@field:SerializedName("photo_3x4_path") val photo3x4Path: String? = null,
	@field:SerializedName("foto_keluarga_path") val fotoKeluargaPath: String? = null,
	@field:SerializedName("surat_tidak_pdam_path") val suratTidakPdamPath: String? = null,
	@field:SerializedName("token_listrik_path") val tokenListrikPath: String? = null
) : Parcelable

val documentFieldMap = mapOf(
	"KTP" to "ktp_path",
	"Kartu Keluarga" to "kk_path",
	"Ijazah" to "ijazah_path",
	"SKL" to "ijazah_skl_path",
	"Raport" to "raport_path",
	"Foto 3x4" to "photo_3x4_path",
	// berkas sipil
	"Kartu Keluarga Legalisir" to "kk_legalisir_path",
	"Akte Lahir Legalisir" to "akte_legalisir_path",
	// berkas sekolah
	"Ijazah Legalisir" to "ijazah_legalisir_path",
	"SKHU Legalisir" to "skhu_legalisir_path",
	"Raport Legalisir" to "raport_legalisir_path",
	"Surat Keterangan Baik" to "surat_baik_path",
	"Surat Rekomendasi" to "surat_rekom_kades_path",
	// kades
	"Surat Keterangan Kelakuan Baik" to "surat_keterangan_baik_path",
	"Surat Pendapatan Orang Tua" to "surat_penghasilan_ortu_path",
	"Surat Keterangan Tidak Mampu" to "surat_tidak_mampu_path",
	"Surat Pajak Bumi dan Bangunan" to "surat_pajak_bumi_bangunan_path",
	"Surat Tidak Berlangganan PDAM" to "surat_tidak_pdam_path",
	"Token Listrik" to "token_listrik_path",
	// kapolsek
	"SKCK" to "skck_path",
	// lain-lain
	"Sertifikat Prestasi Minimal Tingkat Kabupaten" to "sertifikat_prestasi_path",
	"Kartu Indonesia Pintar (KIP)" to "kartu_kip_path",
	"Program Keluarga Harapan (PKH)" to "kartu_pkh_path",
	"Kartu Keluarga Sejahtera (KKS)" to "kartu_kks_path",
	"Foto Keluarga" to "foto_keluarga_path"
)
