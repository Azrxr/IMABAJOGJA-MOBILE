package com.imaba.imabajogja.ui.admin.campuse

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.response.DataDocument
import com.imaba.imabajogja.data.response.DataItemMember
import com.imaba.imabajogja.data.response.StudyPlansItem
import com.imaba.imabajogja.data.response.documentFieldMap
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.data.utils.compressPdf
import com.imaba.imabajogja.data.utils.showToast
import com.imaba.imabajogja.data.utils.uriToFile
import com.imaba.imabajogja.data.utils.uriToFilePdf
import com.imaba.imabajogja.databinding.ActivityAdmStudyDetailBinding
import com.imaba.imabajogja.ui.admin.member.AdmMemberDetailActivity
import com.imaba.imabajogja.ui.campus.CampuseViewModel
import com.imaba.imabajogja.ui.campus.DialogStudyCurrent
import com.imaba.imabajogja.ui.campus.DocumentAdapter
import com.imaba.imabajogja.ui.campus.HomePhotoAdapter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File

@AndroidEntryPoint
class AdmStudyDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdmStudyDetailBinding
    private val viewModel: AdmCampuseViewModel by viewModels()
    private val campuseViewModel: CampuseViewModel by viewModels()

    private lateinit var studyPlanAdapter: AdmStudyPlansAdapter
    private lateinit var documentAdapter: DocumentAdapter
    private lateinit var homePhotoAdapter: HomePhotoAdapter

    private val memberId: Int? by lazy {
        intent.getParcelableExtra<DataItemMember>(AdmMemberDetailActivity.EXTRA_MEMBER)?.id
    }
    private val documentId: Int? by lazy {
        intent.getParcelableExtra<DataItemMember>(AdmMemberDetailActivity.EXTRA_MEMBER)?.documents?.get(
            0
        )?.id
    }

    private val addStudyPlanLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            getMemberDetail(memberId ?: 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdmStudyDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Deteksi mode terang/gelap
        val isDarkMode =
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> true
                else -> false
            }

// Ambil warna sesuai mode
        val resolvedColor = ContextCompat.getColor(
            this,
            if (isDarkMode) R.color.maroon_primary_dark else R.color.maroon_primary
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                // Bersihkan flag transparan
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                // Aktifkan menggambar sistem bar
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

                statusBarColor = resolvedColor
                navigationBarColor = resolvedColor
            }
        }

// Untuk Android M+ (ikon status bar terang/gelap)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            WindowCompat.getInsetsController(
                window,
                window.decorView
            )?.isAppearanceLightStatusBars = !isDarkMode
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        showMemberDetail()
        getMemberDetail(memberId ?: 0)


        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, AdmProgramStudyActivity::class.java)
            intent.putExtra(AdmProgramStudyActivity.ARG_MEMBER_ID, memberId)
            addStudyPlanLauncher.launch(intent)
        }
            updateStudyMember()
        }

        private fun showMemberDetail() {
            val member =
                intent.getParcelableExtra<DataItemMember>(AdmMemberDetailActivity.EXTRA_MEMBER)
            member?.let {
                binding.tvFullname.text = it.fullname

                binding.tvUniversityCurrent.text =
                    it.studyMembers?.firstOrNull()?.university ?: "Belum ada"
                binding.tvFacultyCurrent.text =
                    it.studyMembers?.firstOrNull()?.faculty ?: "Belum ada"
                binding.tvProgramCurrent.text =
                    it.studyMembers?.firstOrNull()?.programStudy ?: "Belum ada"

                Glide.with(this)
                    .load(member.profileImgUrl)
                    .placeholder(R.drawable.ic_user) // 🔥 Set placeholder jika gambar kosong
                    .error(R.drawable.ic_image_broken) // 🔥 Jika gagal load, tampilkan default
                    .into(binding.ivImage)

            }
        }

        private fun updateStudyMember() {
            binding.btnEditCurrentStudy.setOnClickListener {
                val dialog = DialogStudyCurrent.newInstanceForAdmin(memberId ?: 0)
                dialog.onSuccessListener = {
                    getMemberDetail(memberId ?: 0)
                }
                dialog.show(supportFragmentManager, "DialogStudyCurrent")

            }
        }

        private fun getMemberDetail(memberId: Int) {
            viewModel.getMemberDetail(memberId).observe(this) { result ->
                when (result) {
                    is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val data = result.data.data
                        binding.tvFullname.text = data?.fullname
                        binding.tvUniversityCurrent.text =
                            data?.studyMembers?.firstOrNull()?.university ?: "Belum ada"
                        binding.tvFacultyCurrent.text =
                            data?.studyMembers?.firstOrNull()?.faculty ?: "Belum ada"
                        binding.tvProgramCurrent.text =
                            data?.studyMembers?.firstOrNull()?.programStudy ?: "Belum ada"

                        Glide.with(this)
                            .load(data?.profileImgUrl)
                            .placeholder(R.drawable.ic_user) // 🔥 Set placeholder jika gambar kosong
                            .error(R.drawable.ic_image_broken) // 🔥 Jika gagal load, tampilkan default
                            .into(binding.ivImage)

                        val studyPlans = result.data.data?.studyPlans?.firstOrNull()
                        if (studyPlans == null) {
                            binding.tvEmptyPlans.visibility = View.VISIBLE
                            binding.rvStudyPlans.visibility = View.GONE
                        } else {
                            binding.tvEmptyPlans.visibility = View.GONE
                            binding.rvStudyPlans.visibility = View.VISIBLE
                            studyPlans(result.data.data.studyPlans)
                        }

                        documents(result.data.data?.documents)

                        binding.tvStatusDoc.text =
                            if (data?.berkasLengkap == true) "Lengkap" else "Belum Lengkap"
                        binding.tvProgressDoc.text = data?.berkasProgress

                        binding.btnAddHomePhoto.setOnClickListener {
                            pickImageLauncher.launch("image/*")
                        }
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        this.showToast("Gagal mendapatkan data: ${result.message}")
                    }
                }
            }
        }

        private fun studyPlans(plans: List<StudyPlansItem>?) {

            studyPlanAdapter = AdmStudyPlansAdapter(
                onDeleteClick = { studyPlanId ->
                    deleteStudyPlan(studyPlanId)
                },
                onStatusChanged = { memberId, studyPlanId, newStatus ->
                    updateStatusStudyPlan(memberId, studyPlanId, newStatus)
                }
            )

            binding.rvStudyPlans.apply {
                adapter = studyPlanAdapter
                layoutManager = LinearLayoutManager(this@AdmStudyDetailActivity)
            }

            plans?.let { studyPlanAdapter.submitList(it) }

            binding.btnEdit.setOnClickListener {
                studyPlanAdapter.setEditingPositions(true)
                binding.btnCancel.visibility = View.VISIBLE
                binding.btnEdit.visibility = View.GONE
            }

            binding.btnCancel.setOnClickListener {
                studyPlanAdapter.setEditingPositions(false)
                binding.btnCancel.visibility = View.GONE
                binding.btnEdit.visibility = View.VISIBLE
            }
        }

        private fun _addStudyPlan() {
            val dialog = DialogStudyPlanAdd.newInstance(memberId ?: 0)
            dialog.onSuccessListener = {
                // Refresh data ketika berhasil menambah
                getMemberDetail(memberId ?: 0)
            }
            dialog.show(supportFragmentManager, "DialogStudyPlanAdd")
        }

        private fun updateStatusStudyPlan(memberId: Int, studyPlanId: Int, status: String) {
            viewModel.updateStatusPlan(memberId, studyPlanId, status).observe(this) { result ->
                when (result) {
                    is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        // 🔥 Refresh daftar setelah update
                        studyPlanAdapter.setEditingPositions(false)
                        binding.btnCancel.visibility = View.GONE
                        binding.btnEdit.visibility = View.VISIBLE
                        getMemberDetail(memberId ?: 0)
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        this.showToast("Gagal mengupdate: ${result.message}")
                        Log.e("UpdateStatusPlan", "Error: ${result.message}")
                    }
                }
            }
        }

        private fun deleteStudyPlan(studyPlanId: Int) {
            campuseViewModel.deleteStudyPlan(studyPlanId).observe(this) { result ->
                when (result) {
                    is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        this.showToast(result.data)
                        // 🔥 Refresh daftar setelah hapus
                        getMemberDetail(memberId ?: 0)
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        this.showToast("Gagal menghapus: ${result.message}")
                    }
                }
            }
        }

        private fun documents(data: List<DataDocument>?) {

            val homePhoto = data?.flatMap { it.homePhoto ?: emptyList() } ?: emptyList()
            if (homePhoto.isEmpty()) {
                binding.tvEmptyHomePhoto.visibility = View.VISIBLE
            } else {
                binding.tvEmptyHomePhoto.visibility = View.GONE
            }
            homePhotoAdapter = HomePhotoAdapter(
                homePhotos = homePhoto,
                onDeleteClick = { photo -> deleteHomePhoto(photo.id) }
            )
            binding.rvHomePhoto.apply {
                adapter = homePhotoAdapter
                layoutManager = GridLayoutManager(this@AdmStudyDetailActivity, 2)
            }

            // Adapter Dokumen

            val photoDocData = data?.flatMap { doc ->
                val doc = doc.documentsUrl
                listOf(
                    "Foto Keluarga" to doc.fotoKeluargaPath,
                    "Foto 3x4" to doc.photo3x4Path
                )
            } ?: emptyList()

            val documentData = data?.flatMap { doc ->
                val doc = doc.documentsUrl
                listOf(
                    "KTP" to doc.ktpPath,
                    "Kartu Keluarga" to doc.kkPath,
                    "Ijazah" to doc.ijazahPath,
                    "SKL" to doc.ijazahSklPath,
                    "Raport" to doc.raportPath,
//            "Foto 3x4" to doc.photo3x4Path,
                    //berkas sipil
                    "Kartu Keluarga Legalisir" to doc.kkLegalisirPath,
                    "Akte Lahir Legalisir" to doc.akteLegalisirPath,
                    //berkas sekolah
                    "Ijazah Legalisir" to doc.ijazahLegalisirPath, //TODO: tidak terunggah
                    "SKHU Legalisir" to doc.skhuLegalisirPath,
                    "Raport Legalisir" to doc.raportLegalisirPath,
                    "Surat Keterangan Baik" to doc.suratBaikPath,
                    "Surat Rekomendasi" to doc.suratRekomKadesPath,
                    //kades
                    "Surat Keterangan Kelakuan Baik" to doc.suratKeteranganBaikPath,
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
            }
            // Debugging: Cetak data dokumen
            documentData?.forEach { (name, url) ->
                Timber.d("Nama Dokumen: $name, URL: $url")
            }
            photoDocData.forEach { (name, url) ->
                Timber.d("Nama Dokumen: $name, URL: $url")
            }

            documentAdapter = DocumentAdapter(
                documentList = documentData?.plus(photoDocData) ?: emptyList(),
                onAddClick = { docType -> pickDocument(docType) },
                onDeleteClick = { field -> deleteDocument(field) },
                context = this
            )
            binding.rvDocuments.adapter = documentAdapter
            binding.rvDocuments.layoutManager = LinearLayoutManager(this)
        }


        private val pickDocumentLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    val fileType = selectedDocumentType

                    if (fileType == "photo_3x4_path" || fileType == "foto_keluarga_path") {
                        // 📸 If it's a photo category, use `uriToFile()`
                        val photoFile = uriToFile(uri, this)
                        uploadPhotoDoc(fileType, photoFile) // 🔥 Upload Photo
                    } else {
                        // 📄 If it's a document category, use `uriToFilePdf()`
                        val pdfFile = uriToFilePdf(uri, this)
                        val compressedFile = compressPdf(pdfFile) // 🔥 Compress PDF if >2MB
                        uploadDocument(fileType, compressedFile) // 🔥 Upload Document
                    }
                }
            }

        private val pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    selectedImageFile = uriToFile(uri, this) // 🔥 Convert URI to File
                    showPhotoTitleDialog() // 🔥 After selecting a photo, request a title
                } ?: showToast("Failed to pick an image, please try again.")
            }

        private fun pickDocument(docType: String) {
            val fileType = documentFieldMap[docType]
            if (fileType == null) {
                showToast("Invalid document type")
                return
            }
            selectedDocumentType = fileType
            val mimeType = when (fileType) {
                "photo_3x4_path", "foto_keluarga_path" -> "image/*" // 📸 For photo categories
                else -> "application/pdf" // 📄 For document categories
            }
            pickDocumentLauncher.launch(mimeType)
        }


        private fun uploadDocument(documentType: String, file: File) {
            Log.d(
                "UploadDocument",
                "Mengunggah dokumen dengan field API: $documentType, Nama file: ${file.name}"
            )
            viewModel.uploadDocument(memberId ?: 0, documentId ?: 0, documentType, file)
                .observe(this) { result ->
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            showToast("Mengunggah dokumen...")
                        }

                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            showToast("Dokumen berhasil diunggah!")
                            Log.d("UploadDocument", "Berhasil: ${result.data}")
                            getMemberDetail(
                                memberId ?: 0
                            ) // 🔄 Refresh daftar dokumen setelah upload
                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            showToast("Gagal mengupload: ${result.message}")
                            Log.e("UploadDocument", "Error: ${result.message}")
                        }
                    }
                }
        }

        private fun uploadPhotoDoc(documentType: String, file: File) {
            Timber.tag("UploadDocument")
                .d("Mengunggah dokumen dengan field API: $documentType, Nama file: ${file.name}")

            viewModel.uploadPhotoDoc(memberId ?: 0, documentId ?: 0, documentType, file)
                .observe(this) { result ->
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            showToast("Mengunggah dokumen...")
                        }

                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            showToast("Dokumen berhasil diunggah!")
                            Timber.d("Berhasil: ${result.data}")
                            getMemberDetail(
                                memberId ?: 0
                            ) // 🔄 Refresh daftar dokumen setelah upload
                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            showToast("Gagal mengupload: ${result.message}")
                            Log.e("UploadDocument", "Error: ${result.message}")
                        }
                    }
                }
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

        private fun uploadPhoto(photoTitle: String) {
            selectedImageFile?.let { file ->
                viewModel.uploadHomePhoto(memberId ?: 0, photoTitle, file).observe(this) { result ->
                    when (result) {
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            showToast("Foto rumah berhasil diunggah!")
                            Timber.d("Berhasil: %s", result.data)
                            getMemberDetail(memberId ?: 0) // 🔄 Refresh list setelah berhasil
                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            showToast("Gagal mengupload foto: ${result.message}")
                            Log.e("UploadPhoto", "Error: ${result.message}")
                        }

                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            showToast("Mengunggah foto rumah...")
                        }
                    }
                }
            }
                ?: showToast("Pilih gambar terlebih dahulu") // 🔥 Mencegah error jika file belum dipilih
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
                    binding.progressBar.visibility = View.VISIBLE

                    Timber.d("🗑 Menghapus dokumen: $field")

                    viewModel.deleteDocument(memberId ?: 0, fileType).observe(this) { result ->
                        when (result) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                                Timber.d("⏳ Menghapus dokumen...")
                            }

                            is Result.Success -> {
                                Timber.d("✅ Berhasil menghapus: %s", field)
                                showToast("Dokumen berhasil dihapus!")
                                getMemberDetail(
                                    memberId ?: 0
                                ) // 🔄 Refresh daftar dokumen setelah delete
                            }

                            is Result.Error -> {
                                Log.e("DeleteDocument", "Error: ${result.message}")
                                showToast("Gagal menghapus: ${result.message}")
                            }
                        }
                        // Pastikan indikator loading selalu di-hide
                        binding.progressBar.visibility = View.GONE
                    }
                }
                .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        private fun deleteHomePhoto(id: Int) {
            AlertDialog.Builder(this)
                .setTitle("Hapus Foto?")
                .setMessage("Apakah Anda yakin ingin menghapus foto ini?")
                .setPositiveButton("Ya") { _, _ ->
                    binding.progressBar.visibility = View.VISIBLE

                    viewModel.deleteHomePhoto(id).observe(this) { result ->
                        when (result) {
                            is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                            is Result.Success -> {
                                binding.progressBar.visibility = View.GONE
                                showToast("Foto berhasil dihapus")
                                Log.d("DeleteHomePhoto", "Berhasil: ${result.data}")
                                getMemberDetail(memberId ?: 0) // 🔄 Refresh list setelah berhasil
                            }

                            is Result.Error -> {
                                binding.progressBar.visibility = View.GONE
                                Log.e("DeleteHomePhoto", "Error: ${result.message}")
                                showToast("Gagal menghapus: ${result.message}")
                            }
                        }
                    }
                }
                .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        companion object {
            private const val REQUEST_PICK_DOCUMENT = 2000
            private const val REQUEST_PICK_IMAGE = 2001
            private var selectedDocumentType: String = ""
            private var selectedImageFile: File? = null
        }
    }