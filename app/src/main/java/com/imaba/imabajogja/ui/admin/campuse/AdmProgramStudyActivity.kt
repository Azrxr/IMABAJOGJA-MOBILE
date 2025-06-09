package com.imaba.imabajogja.ui.admin.campuse

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.response.ProgramStudyImportResponse
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.data.utils.saveToDownloadImaba
import com.imaba.imabajogja.data.utils.showToast
import com.imaba.imabajogja.data.utils.uriToExcel
import com.imaba.imabajogja.databinding.ActivityAdmProgramStudyBinding
import com.imaba.imabajogja.ui.campus.CampuseViewModel
import com.imaba.imabajogja.ui.campus.ProgramStudyAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@AndroidEntryPoint
class AdmProgramStudyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdmProgramStudyBinding
    private val viewModel: CampuseViewModel by viewModels()
    private val viewModelAdm: AdmCampuseViewModel by viewModels()

    private lateinit var adapter: ProgramStudyAdapter
    private var activeJenjangFilter: String? = null
    private var searchQuery: String? = null

    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestStoragePermissionLauncher: ActivityResultLauncher<String>

    private val memberId: Int by lazy {
        intent.getIntExtra(ARG_MEMBER_ID, 0)
    }

    companion object {
        const val ARG_MEMBER_ID = "member_id"
        private const val STORAGE_PERMISSION_CODE = 999
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdmProgramStudyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Deteksi mode terang/gelap
        val isDarkMode = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
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
            WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = !isDarkMode
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        requestStoragePermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                downloadExcelTemplate()
            } else {
                Toast.makeText(this, "Izin ditolak", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnImport.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type =
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            filePickerLauncher.launch(Intent.createChooser(intent, "Pilih file Excel"))
        }
        binding.btnTemplate.setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                // For Android 11+ (API 30+)
                downloadExcelTemplate()
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                // For Android 6.0 to 10
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    downloadExcelTemplate()
                } else {
                    requestStoragePermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            } else {
                // For older versions
                downloadExcelTemplate()
            }
        }
        filePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { uri ->
                        val file = uriToExcel(this, uri)
                        importProgramStudy(file)
                    }
                }
            }
        loadProgramStudy()
        setupSearch()
        setupFilterButton()
    }

    private fun importProgramStudy(file: File) {
        viewModelAdm.importProgramStudy(file).observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val data = result.data
                    showImportResultDialog(data)
                    Log.d("ImportCheck", "Sukses upload, data = ${data.report.activities}")
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    this.showToast("Gagal: ${result.message}")
                    Log.d("ImportCheck", "gagal upload, data = ${result.message}")
                }

                is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
            }
        }
    }

    private fun showImportResultDialog(response: ProgramStudyImportResponse) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val message = buildString {
            Log.d("ImportCheck", "Response: ${response.message}")
            append("Berhasil: ${response.report.success}\n")
            append("Gagal: ${response.report.fail}\n\n")

            if (!response.report.activities.isNullOrEmpty()) {
                append("Detail Aktivitas:\n")
                response.report.activities.forEach { activity ->
                    append("• Baris: ${activity.row}, Aksi: ${activity.actions.joinToString(", ")}\n")
                }
            }
        }

        builder.setTitle("Hasil Import")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ -> adapter.notifyDataSetChanged() }
            .show()

    }

    private fun downloadExcelTemplate() {
        lifecycleScope.launch {
            try {
                val fileNameBase = "template_prog_study_yogyakarta2025"
                val fileExt = ".xlsx"
                var fileName = "$fileNameBase$fileExt"
                val savedFile = withContext(Dispatchers.IO) {
                    val inputStream = assets.open("$fileNameBase$fileExt")
                    var file = File(getExternalFilesDir(null), fileName)
                    var count = 2
                    while (file.exists()) {
                        fileName = "$fileNameBase($count)$fileExt"
                        file = File(getExternalFilesDir(null), fileName)
                        count++
                    }
                    saveToDownloadImaba(this@AdmProgramStudyActivity, fileName, inputStream)
                }

                val dialog = androidx.appcompat.app.AlertDialog.Builder(this@AdmProgramStudyActivity)
                    .setTitle("Export Berhasil")
                    .setMessage("File berhasil disimpan di:\n${savedFile.absolutePath}")
                    .setPositiveButton("OK", null)
                    .setNegativeButton("Open") { _, _ ->
                        openExcelFile(this@AdmProgramStudyActivity, savedFile)
                    }
                    .create()
                dialog.show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@AdmProgramStudyActivity,
                    "Gagal menyimpan file template: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun openExcelFile(context: Activity, file: File) {
        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(
            uri,
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        // TODO : Periksa apakah ada aplikasi yang bisa menangani file Excel
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Tidak ada aplikasi untuk membuka file Excel.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun saveStudyPlan(universityId: Int, programStudyId: Int) {
        viewModelAdm.getMemberDetail(memberId).observe(this) { result ->
            if (result is Result.Success) {
                val studyPlans = result.data.data?.studyPlans

                // Skip validation if studyPlans is null
                if (studyPlans == null) {
                    addStudyPlanToServer(universityId, programStudyId)
                    return@observe
                }

                // Validasi maksimal 2 universitas
                val uniqueUniversities = studyPlans.map { it.universityId }.distinct().size
                if (uniqueUniversities >= 2 && !studyPlans.any { it.universityId == universityId }) {
                    showToast("Anda hanya dapat menambahkan maksimal 2 universitas.")
                    return@observe
                }

                // Validasi maksimal 2 program studi per universitas
                val studyCount = studyPlans.count { it.universityId == universityId }
                if (studyCount >= 2) {
                    showToast("Anda hanya dapat menambahkan maksimal 2 program studi dalam satu universitas.")
                    return@observe
                }

                addStudyPlanToServer(universityId, programStudyId)
            }

        }
    }

    private fun addStudyPlanToServer(universityId: Int, programStudyId: Int) {
        viewModelAdm.addStudyPlan(memberId, universityId, programStudyId)
            .observe(this) { result ->
                when (result) {
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        showToast("Rencana studi berhasil ditambah")
                        val intent = Intent()
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        showToast("Gagal menambah rencana studi: ${result.message}")
                    }

                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        showToast("Memproses Rencana Studi...")
                    }
                }
            }
    }

    private fun loadProgramStudy() {
        adapter = ProgramStudyAdapter(
            onAddClick = { item ->
                val universityId = item.universityId ?: return@ProgramStudyAdapter
                val programStudyId = item.programStudyId ?: return@ProgramStudyAdapter
                saveStudyPlan(universityId, programStudyId)
            },
        )
        binding.rvProgramStudy.adapter = adapter
        binding.rvProgramStudy.layoutManager = LinearLayoutManager(this)


        viewModel.getAllPrograStudy(
            search = searchQuery,
            jenjang = activeJenjangFilter
        ).observe(this) { result ->
            when (result) {
                is Result.Loading -> binding.progressBar.visibility =
                    View.VISIBLE

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val data = result.data.data
                    if (data.isNullOrEmpty()) {
                        binding.tvEmptyPlans.visibility = View.VISIBLE
                        binding.rvProgramStudy.visibility = View.GONE
                    } else {
                        binding.tvEmptyPlans.visibility = View.GONE
                        binding.rvProgramStudy.visibility = View.VISIBLE
                        adapter.submitList(data)
                    }
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Gagal memuat program studi", Toast.LENGTH_SHORT).show()
                    Log.d("program_stidy", "gagal memuat programStudy, data = ${result.message}")
                }
            }
        }
    }

    private fun setupSearch() {
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchQuery = query
                loadProgramStudy() // Reload the list with the search query
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery = newText
                loadProgramStudy() // Reload the list as the query changes
                return true
            }
        })
    }

    private fun setupFilterButton() {
        val jenjangList = listOf("D1", "D2", "D3", "D4", "S1", "S2", "S3", "Profesi", "S2 Terapan")

        binding.btnFilter.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
                .setTitle("Pilih Jenjang")
                .setSingleChoiceItems(
                    jenjangList.toTypedArray(),
                    jenjangList.indexOf(activeJenjangFilter)
                ) { _, which ->
                    activeJenjangFilter = jenjangList[which]
                }
                .setPositiveButton("OK") { _, _ ->
                    updateFilterIcon()
                    loadProgramStudy() // Reload the list with the selected filter
                }
                .setNegativeButton("Batal", null)
                .setNeutralButton("Clear") { _, _ ->
                    activeJenjangFilter = null
                    updateFilterIcon()
                    loadProgramStudy() // Reload the list without any filter
                }
                .show()
        }
    }

    private fun updateFilterIcon() {
        val isActive = !activeJenjangFilter.isNullOrEmpty()
        val drawable = if (isActive) R.drawable.ic_filter_on else R.drawable.ic_filter_off
        binding.btnFilter.setImageResource(drawable)
    }
}