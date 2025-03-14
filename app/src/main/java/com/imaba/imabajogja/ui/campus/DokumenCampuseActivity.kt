package com.imaba.imabajogja.ui.campus

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.imaba.imabajogja.data.response.DataDocument
import com.imaba.imabajogja.data.response.documentFieldMap
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.data.utils.compressPdf
import com.imaba.imabajogja.data.utils.showToast
import com.imaba.imabajogja.data.utils.uriToFile
import com.imaba.imabajogja.data.utils.uriToFilePdf
import com.imaba.imabajogja.databinding.ActivityDokumenCampuseBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class DokumenCampuseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDokumenCampuseBinding
    private val viewModel: CampuseViewModel by viewModels()
    private lateinit var homePhotoAdapter: HomePhotoAdapter
    private lateinit var documentAdapter: DocumentAdapter

    private var selectedImageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDokumenCampuseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loadDocuments()
        binding.btnImagePlus.setOnClickListener {
            pickImageFromGallery()
        }
    }

    private fun loadDocuments() {
        viewModel.getDocuments().observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val documentsData = result.data.data // 👈 Pastikan ini berisi DataDocument
                    setupAdapter(documentsData) // 👈 Pastikan parameter cocok
                }

                is Result.Error -> showToast("Gagal mengambil dokumen: ${result.message}")
                is Result.Loading -> showToast("Memuat dokumen...")
            }
        }
    }

    private fun setupAdapter(data: DataDocument) {

        homePhotoAdapter = HomePhotoAdapter(
            homePhotos = data.homePhoto,
            onDeleteClick = { photo -> deleteHomePhoto(photo.id) }
        )
        binding.rvHomePhoto.adapter = homePhotoAdapter
        binding.rvHomePhoto.layoutManager = GridLayoutManager(this, 2)

        // Adapter Dokumen
        var doc = data.documentsUrl
        val photoDocData = listOf(
            "Foto 3x4" to doc.photo3x4Path,
            "Foto Keluarga" to doc.fotoKeluargaPath,
        )
        val documentData = listOf(
            "KTP" to doc.ktpPath,
            "Kartu Keluarga" to doc.kkPath,
            "Ijazah" to doc.ijazahPath,
            "SKL" to doc.ijazahSklPath,
            "Raport" to doc.raportPath,
//            "Foto 3x4" to doc.photo3x4Path,
            //berkas sipil
            "Kartu Keluarga Legalisir" to doc.kkLegalisirPath,
            "Akte Lahir Legaliisir" to doc.akteLegalisirPath,
            //berkas sekolah
            "Ijazah Legalisir" to doc.ijazahLegalisirPath,
            "SKHU Legalisir" to doc.skhuLegalisirPath,
            "Raport Legalisir" to doc.raportLegalisirPath,
            "Surat Keterangan Baik" to doc.suratBaikPath,
            "Surat Rekomendasi" to doc.suratRekomKadesPath, //TODO
            //kades
            "Surat Keterangan Kelakuan Baik" to doc.suratRekomKadesPath, //TODO
            "Surat Pendapatan Orang Tua" to doc.suratPenghasilanOrtuPath,
            "Surat Keterangan Tidak Mampu" to doc.suratTidakMampuPath,
            "Surat Pajak Bumi dan Bangunan" to doc.suratPajakBumiBangunanPath,
            "Surat Tidak Berlangganan PDAM" to doc.suratTidakPdamPath,
            "Token Listrik" to doc.tokenListrikPath,
            //kapolsek
            "SKCK" to doc.skckPath,
            //lain lain
            "Sertifikat Prestasi Minimal Tingkat Kabupaten" to doc.sertifikatPrestasiPath,
            "Kartu Indonesia Pintar (KIP)" to doc.kartuKipPath,
            "Program Keluarga Harapan (PKH)" to doc.kartuPkhPath,
            "Kartu Keluarga Sejahtera (KKS)" to doc.kartuKksPath,
//            "Foto Keluarga" to doc.fotoKeluargaPath,
        )
        // Debugging: Cetak data dokumen
        documentData.forEach { (name, url) ->
            Log.d("DocumentData", "Nama Dokumen: $name, URL: $url")
        }
        photoDocData.forEach { (name, url) ->
            Log.d("DocumentData", "Nama Dokumen: $name, URL: $url")
        }
        documentAdapter = DocumentAdapter(
            documentList = documentData + photoDocData,
            onAddClick = { docType -> pickDocument(docType) },
            onDeleteClick = { field -> deleteDocument(field) },
            context = this
        )
        binding.rvDocuments.adapter = documentAdapter
        binding.rvDocuments.layoutManager = LinearLayoutManager(this)
    }


    private fun deleteHomePhoto(id: Int) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Foto?")
            .setMessage("Apakah Anda yakin ingin menghapus foto ini?")
            .setPositiveButton("Ya") { _, _ ->
                binding.progressIndicator.visibility = View.VISIBLE

                viewModel.deleteHomePhoto(id).observe(this) { result ->
                    when (result) {
                        is Result.Loading -> binding.progressIndicator.visibility = View.VISIBLE
                        is Result.Success -> {
                            binding.progressIndicator.visibility = View.GONE
                            showToast("Foto berhasil dihapus")
                            Log.d("DeleteHomePhoto", "Berhasil: ${result.data}")
                            loadDocuments() // 🔄 Refresh list setelah berhasil
                        }

                        is Result.Error -> {
                            binding.progressIndicator.visibility = View.GONE
                            Log.e("DeleteHomePhoto", "Error: ${result.message}")
                            showToast("Gagal menghapus: ${result.message}")
                        }
                    }
                }
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun pickDocument(docType: String) {

        val fileType = documentFieldMap[docType] // 🔥 Ambil nama field API yang benar
        if (fileType == null) {
            showToast("Jenis dokumen tidak valid")
            return
        }
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        startActivityForResult(Intent.createChooser(intent, "Pilih Dokumen"), REQUEST_PICK_DOCUMENT)
        selectedDocumentType = fileType // Simpan tipe dokumen yang dipilih
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_PICK_DOCUMENT && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val file = uriToFilePdf(uri, this)

                // 🔥 Kompresi PDF jika lebih dari 2MB
                val compressedFile = compressPdf(file)

                uploadDocument(selectedDocumentType, compressedFile)
            }
        }
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                selectedImageFile = uriToFile(uri, this) // 🔥 Konversi URI ke File
                showPhotoTitleDialog() // 🔥 Setelah pilih foto, minta title
            } else {
                showToast("Gagal mengambil gambar, coba lagi.")
            }
        }
    }

    private fun uploadDocument(documentType: String, file: File) {
        Log.d(
            "UploadDocument",
            "Mengunggah dokumen dengan field API: $documentType, Nama file: ${file.name}"
        )

        viewModel.uploadDocument(documentType, file).observe(this) { result ->
            when (result) {
                is Result.Loading -> showToast("Mengunggah dokumen...")
                is Result.Success -> {
                    showToast("Dokumen berhasil diunggah!")
                    Log.d("UploadDocument", "Berhasil: ${result.data}")
                    loadDocuments() // 🔄 Refresh daftar dokumen setelah upload
                }

                is Result.Error -> {
                    showToast("Gagal mengupload: ${result.message}")
                    Log.e("UploadDocument", "Error: ${result.message}")
                }
            }
        }
    }

    private fun deleteDocument(field: String) {
        val fileType = documentFieldMap[field]
        if (fileType == null) {
            showToast("Jenis dokumen tidak valid")
            return
        }
        AlertDialog.Builder(this)
            .setTitle("Hapus Dokumen?")
            .setMessage("Apakah Anda yakin ingin menghapus dokumen ini?")
            .setPositiveButton("Ya") { _, _ ->
                binding.progressIndicator.visibility = View.VISIBLE

                Log.d("DeleteDocument", "🗑 Menghapus dokumen: $field")

                viewModel.deleteDocument(fileType).observe(this) { result ->
                    when (result) {
                        is Result.Loading -> {
                            binding.progressIndicator.visibility = View.VISIBLE
                            Log.d("DeleteDocument", "⏳ Menghapus dokumen...")
                        }

                        is Result.Success -> {
                            Log.d("DeleteDocument", "✅ Berhasil menghapus: $field")
                            showToast("Dokumen berhasil dihapus!")
                            loadDocuments() // 🔄 Refresh daftar dokumen setelah delete
                        }

                        is Result.Error -> {
                            Log.e("DeleteDocument", "❌ Gagal menghapus: ${result.message}")
                            showToast("Gagal menghapus: ${result.message}")
                        }
                    }

                    // Pastikan indikator loading selalu di-hide
                    binding.progressIndicator.visibility = View.GONE
                }
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun pickImageFromGallery() {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                type = "image/*"
            }
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    private fun uploadPhoto(photoTitle: String) {
        selectedImageFile?.let { file ->
            viewModel.uploadHomePhoto(photoTitle, file).observe(this) { result ->
                when (result) {
                    is Result.Success -> {
                        showToast("Foto rumah berhasil diunggah!")
                        Log.d("UploadPhoto", "Berhasil: ${result.data}")
                        loadDocuments() // 🔄 Refresh list setelah berhasil
                    }

                    is Result.Error -> {
                        showToast("Gagal mengupload foto: ${result.message}")
                        Log.e("UploadPhoto", "Error: ${result.message}")
                    }

                    is Result.Loading -> showToast("Mengunggah foto rumah...")
                }
            }
        } ?: showToast("Pilih gambar terlebih dahulu") // 🔥 Mencegah error jika file belum dipilih
    }

    private fun showPhotoTitleDialog() {
        val input = EditText(this).apply {
            hint = "Masukkan judul foto"
            inputType = InputType.TYPE_CLASS_TEXT
        }

        AlertDialog.Builder(this)
            .setTitle("Judul Foto")
            .setView(input)
            .setPositiveButton("Upload") { _, _ ->
                val title = input.text.toString().trim()
                if (title.isNotEmpty()) {
                    uploadPhoto(title) // 🔥 STEP 4: Kirim Foto & Title ke Server
                } else {
                    showToast("Judul tidak boleh kosong")
                }
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    companion object {
        private const val REQUEST_PICK_DOCUMENT = 2000
        private const val REQUEST_PICK_IMAGE = 2001
        private var selectedDocumentType: String = "" // Untuk menyimpan tipe dokumen yang dipilih
    }
}