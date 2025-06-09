package com.imaba.imabajogja.ui.campus

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.imaba.imabajogja.data.response.DataDocument
import com.imaba.imabajogja.data.response.StudyMemberResponse
import com.imaba.imabajogja.data.response.documentFieldMap
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.data.utils.compressPdf
import com.imaba.imabajogja.data.utils.showLoading
import com.imaba.imabajogja.data.utils.showToast
import com.imaba.imabajogja.data.utils.uriToFile
import com.imaba.imabajogja.data.utils.uriToFilePdf
import com.imaba.imabajogja.databinding.FragmentCampuseBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File

@AndroidEntryPoint
class CampuseFragment : Fragment() {

    companion object {
        fun newInstance() = CampuseFragment()
        private const val REQUEST_PICK_DOCUMENT = 2000
        private const val REQUEST_PICK_IMAGE = 2001
        private var selectedDocumentType: String = "" // Untuk menyimpan tipe dokumen yang dipilih
        private var selectedImageFile: File? = null
    }

    private lateinit var binding: FragmentCampuseBinding
    private lateinit var adapter: StudyPlansAdapter
    private lateinit var homePhotoAdapter: HomePhotoAdapter
    private lateinit var documentAdapter: DocumentAdapter
    private val viewModel: CampuseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCampuseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadStudyPlans()
        studyPlans()
        getStudyMember()
        loadDocuments()
        updateStudyCurrent()
        binding.btnAddHomePhoto.setOnClickListener {
            pickImageFromGallery()
        }
    }

    private fun updateStudyCurrent() {
        binding.btnEditCurrentStudy.setOnClickListener {
            val dialog = DialogStudyCurrent.newInstanceForUser()
            dialog.onSuccessListener = {
                getStudyMember()
            }
            dialog.show(parentFragmentManager, "DialogStudyCurrent")

        }
    }

    private fun getStudyMember() {
        viewModel.getStudyMember().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    setStudyMember(result.data)
                    deleteStudyMember()
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    requireContext().showToast("Gagal mengambil data: ${result.message}")
                }
            }
        }
    }

    private fun setStudyMember(data: StudyMemberResponse) {
        val studyData = data.data
        if (studyData == null) {
            binding.btnDeleteCurrentStudy.visibility = View.GONE
        } else {
            binding.btnDeleteCurrentStudy.visibility = View.VISIBLE
        }
        binding.tvUniversityCurrent.text = studyData?.university?.name ?: "Belum ada data"
        binding.tvFacultyCurrent.text = studyData?.faculty?.name ?: "Belum ada data"
        binding.tvProgramCurrent.text = studyData?.programStudy?.name ?: "Belum ada data"
    }

    private fun loadStudyPlans() {
        viewModel.getStudyPlans().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val studyPlans = result.data
                    if (studyPlans.isEmpty()) {
                        binding.tvEmptyPlans.visibility = View.VISIBLE
                        binding.rvStudyPlans.visibility = View.GONE
                    } else {
                        binding.tvEmptyPlans.visibility = View.GONE
                        binding.rvStudyPlans.visibility = View.VISIBLE
                        adapter.submitList(studyPlans)
                    }
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    requireContext().showToast("Gagal mengambil data: ${result.message}")
                }
            }
        }
    }

    private fun studyPlans() {
        adapter = StudyPlansAdapter(
            onDeleteClick = { studyPlanId -> showDeleteConfirmation(studyPlanId) },
            isEditing = false
        )

        binding.rvStudyPlans.adapter = adapter
        binding.rvStudyPlans.layoutManager = LinearLayoutManager(requireContext())

        binding.btnEdit.setOnClickListener {
            adapter.setEditingMode(true)
            binding.btnCancel.visibility = View.VISIBLE
            binding.btnEdit.visibility = View.GONE
        }

        binding.btnCancel.setOnClickListener {
            adapter.setEditingMode(false)
            binding.btnCancel.visibility = View.GONE
            binding.btnEdit.visibility = View.VISIBLE
        }
        binding.btnAdd.setOnClickListener {
//            Intent(requireContext(), AddStudyPlanActivity::class.java).apply {
//                startActivity(this)
//            }
            Intent(requireContext(), ProgramStudyActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun showDeleteConfirmation(studyPlanId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Rencana study")
            .setMessage("Apakah Anda yakin ingin menghapus renacana ini?")
            .setPositiveButton("Hapus") { _, _ ->
                viewModel.deleteStudyPlan(studyPlanId).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            requireContext().showToast(result.data)
                            loadStudyPlans() // 🔥 Refresh daftar setelah hapus
                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            requireContext().showToast("Gagal menghapus: ${result.message}")
                        }
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun loadDocuments() {
        viewModel.getDocuments().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    showLoading(binding.progressBar, false)
                    val documentsData = result.data.data // 👈 Pastikan ini berisi DataDocument
                    setupDocument(documentsData) // 👈 Pastikan parameter cocok
                }

                is Result.Error -> {
                    showLoading(binding.progressBar, false)
                    requireContext().showToast("Gagal mengambil dokumen: ${result.message}")
                }

                is Result.Loading -> {
                    showLoading(binding.progressBar, true)
                }
            }
        }
    }

    private fun setupDocument(data: DataDocument) {

        val homePhotos = data.homePhoto ?: emptyList()

        if (homePhotos.isEmpty()) {
            binding.tvEmptyHomePhoto.visibility = View.VISIBLE
            binding.rvHomePhoto.visibility = View.GONE
        } else {
            binding.tvEmptyHomePhoto.visibility = View.GONE
            binding.rvHomePhoto.visibility = View.VISIBLE
        }

        homePhotoAdapter = HomePhotoAdapter(
            homePhotos = data.homePhoto ?: emptyList(),
            onDeleteClick = { photo -> deleteHomePhoto(photo.id) }
        )
        binding.rvHomePhoto.adapter = homePhotoAdapter
        binding.rvHomePhoto.layoutManager = GridLayoutManager(requireContext(), 2)

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
            Timber.d("Nama Dokumen: $name, URL: $url")
        }
        photoDocData.forEach { (name, url) ->
            Timber.d("Nama Dokumen: $name, URL: $url")
        }

        documentAdapter = DocumentAdapter(
            documentList = documentData + photoDocData,
            onAddClick = { docType -> pickDocument(docType) },
            onDeleteClick = { field -> deleteDocument(field) },
            context = requireContext()
        )
        binding.rvDocuments.adapter = documentAdapter
        binding.rvDocuments.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun deleteHomePhoto(id: Int) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Hapus Foto?")
            .setMessage("Apakah Anda yakin ingin menghapus foto ini?")
            .setPositiveButton("Ya") { _, _ ->
                binding.progressBar.visibility = View.VISIBLE

                viewModel.deleteHomePhoto(id).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Result.Loading -> {
                            showLoading(binding.progressBar, true)
                        }

                        is Result.Success -> {
                            showLoading(binding.progressBar, false)
                            requireContext().showToast("Foto berhasil dihapus")
                            Log.d("DeleteHomePhoto", "Berhasil: ${result.data}")
                            loadDocuments() // 🔄 Refresh list setelah berhasil
                        }

                        is Result.Error -> {
                            showLoading(binding.progressBar, false)
                            Log.e("DeleteHomePhoto", "Error: ${result.message}")
                            requireContext().showToast("Gagal menghapus: ${result.message}")
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
            requireContext().showToast("Jenis dokumen tidak valid")
            return
        }
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = when (fileType) {
            "photo_3x4_path", "foto_keluarga_path" -> "image/*" // 📸 Jika kategori foto
            else -> "application/pdf" // 📄 Jika kategori dokumen
        }
        selectedDocumentType = fileType
        startActivityForResult(Intent.createChooser(intent, "Pilih Dokumen"), REQUEST_PICK_DOCUMENT)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_PICK_DOCUMENT && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val fileType = selectedDocumentType

                if (fileType == "photo_3x4_path" || fileType == "foto_keluarga_path") {
                    // 📸 Jika kategori foto, gunakan `uriToFile()`
                    val photoFile = uriToFile(uri, requireContext())
                    uploadPhotoDoc(fileType, photoFile) // 🔥 Upload Foto
                } else {
                    // 📄 Jika kategori dokumen, gunakan `uriToFilePdf()`
                    val pdfFile = uriToFilePdf(uri, requireContext())
                    val compressedFile = compressPdf(pdfFile) // 🔥 Kompres PDF jika >2MB
                    uploadDocument(fileType, compressedFile) // 🔥 Upload Dokumen
                }
            }
        }

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                selectedImageFile = uriToFile(uri, requireContext()) // 🔥 Konversi URI ke File
                showPhotoTitleDialog() // 🔥 Setelah pilih foto, minta title
            } else {
                requireContext().showToast("Gagal mengambil gambar, coba lagi.")
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
                is Result.Loading -> {
                    showLoading(binding.progressBar, true)
                }

                is Result.Success -> {
                    showLoading(binding.progressBar, false)
                    Log.d("UploadDocument", "Berhasil: ${result.data}")
                    loadDocuments() // 🔄 Refresh daftar dokumen setelah upload
                }

                is Result.Error -> {
                    showLoading(binding.progressBar, false)
                    requireContext().showToast("Gagal mengupload: ${result.message}")
                    Log.e("UploadDocument", "Error: ${result.message}")
                }
            }
        }
    }

    private fun uploadPhotoDoc(documentType: String, file: File) {
        Timber.tag("UploadDocument")
            .d("Mengunggah dokumen dengan field API: $documentType, Nama file: ${file.name}")

        viewModel.uploadPhotoDoc(documentType, file).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(binding.progressBar, true)
                }

                is Result.Success -> {
                    showLoading(binding.progressBar, false)
                    Timber.d("Berhasil: ${result.data}")
                    loadDocuments() // 🔄 Refresh daftar dokumen setelah upload
                }

                is Result.Error -> {
                    showLoading(binding.progressBar, true)
                    requireContext().showToast("Gagal mengupload: ${result.message}")
                    Timber.e("Error: ${result.message}")
                }
            }
        }
    }

    private fun deleteDocument(field: String) {
        val fileType = documentFieldMap[field]
        if (fileType == null) {
            requireContext().showToast("Jenis dokumen tidak valid")
            return
        }
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Hapus Dokumen?")
            .setMessage("Apakah Anda yakin ingin menghapus dokumen ini?")
            .setPositiveButton("Ya") { _, _ ->
                binding.progressBar.visibility = View.VISIBLE

                Timber.d("🗑 Menghapus dokumen: $field")

                viewModel.deleteDocument(fileType).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Result.Loading -> {
                            showLoading(binding.progressBar, true)
                            Timber.d("⏳ Menghapus dokumen...")
                        }

                        is Result.Success -> {
                            Timber.d("✅ Berhasil menghapus: %s", field)
                            showLoading(binding.progressBar, false)
                            loadDocuments() // 🔄 Refresh daftar dokumen setelah delete
                        }

                        is Result.Error -> {
                            showLoading(binding.progressBar, false)
                            Timber.e("❌ Gagal menghapus: %s", result.message)
                            requireContext().showToast("Gagal menghapus: ${result.message}")
                        }
                    }

                    // Pastikan indikator loading selalu di-hide
                    binding.progressBar.visibility = View.GONE
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
                        showLoading(binding.progressBar, false)
                        requireContext().showToast("Foto rumah berhasil diunggah!")
                        Timber.d("Berhasil: %s", result.data)
                        loadDocuments() // 🔄 Refresh list setelah berhasil
                    }

                    is Result.Error -> {
                        showLoading(binding.progressBar, false)
                        requireContext() showToast ("Gagal mengupload foto: ${result.message}")
                        Log.e("UploadPhoto", "Error: ${result.message}")
                    }

                    is Result.Loading -> {
                        showLoading(binding.progressBar, true)
                    }
                }
            }
        }
            ?: requireContext().showToast("Pilih gambar terlebih dahulu") // 🔥 Mencegah error jika file belum dipilih
    }

    private fun showPhotoTitleDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Masukkan judul foto"
            inputType = InputType.TYPE_CLASS_TEXT
        }

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Judul Foto")
            .setView(input)
            .setPositiveButton("Upload") { _, _ ->
                val title = input.text.toString().trim()
                if (title.isNotEmpty()) {
                    uploadPhoto(title) // 🔥 STEP 4: Kirim Foto & Title ke Server
                } else {
                    requireContext().showToast("Judul tidak boleh kosong")
                }
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun deleteStudyMember() {
        binding.btnDeleteCurrentStudy.setOnClickListener {
            viewModel.deleteStudyMember().observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        requireContext().showToast(result.data.message)
                        loadStudyPlans()
                        getStudyMember()
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        requireContext().showToast("Gagal menghapus: ${result.message}")
                    }
                }
            }
        }
    }
}