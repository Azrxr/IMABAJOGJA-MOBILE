package com.imaba.imabajogja.ui.admin.member

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.response.ImportMemberResponse
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.data.utils.showLoading
import com.imaba.imabajogja.data.utils.showToast
import com.imaba.imabajogja.data.utils.uriToExcel
import com.imaba.imabajogja.databinding.FragmentAdmMemberBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class AdmMemberFragment : Fragment() {

    companion object {
        fun newInstance() = AdmMemberFragment()
        private const val STORAGE_PERMISSION_CODE = 123
    }

    private val viewModel: AdmMemberViewModel by viewModels()
    private lateinit var adapter: AdmMemberAdapter
    private lateinit var binding: FragmentAdmMemberBinding
    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestStoragePermissionLauncher: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestStoragePermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                downloadExcelTemplate()
            } else {
                Toast.makeText(requireContext(), "Izin ditolak", Toast.LENGTH_SHORT).show()
            }
        }

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdmMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { uri ->
                        val file = uriToExcel(requireContext(), uri)
                        importMember(file)
                    }
                }
            }

        binding.btnAdd.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Import Massal Anggota")
                .setMessage("Silakan pilih tindakan berikut.")
                .setPositiveButton("Download Template Excel") { _, _ ->
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                        ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            123 // request code
                        )
                    } else {
                        // permission granted / Android 10+
                        downloadExcelTemplate()
                    }
                }
                .setNeutralButton("Pilih File Excel") { _, _ ->
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type =
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    filePickerLauncher.launch(Intent.createChooser(intent, "Pilih file Excel"))
                }
                .setNegativeButton("Batal", null)
                .show()
        }
        binding.btnExport.setOnClickListener {
            showExportFilterDialog()
        }

        getMemberSummary()
        setupRecyclerView()
        setupSearch()
        setupFilterButton()
    }

    private fun setupSearch() {
        binding.searchBar.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Submit tidak diperlukan, kita listen perubahan teks saja
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText.orEmpty())
                return true
            }
        })
    }

    private fun setupFilterButton() {
        binding.btnFilter.setOnClickListener { view ->
            val popupMenu = PopupMenu(requireContext(), view)
            popupMenu.menuInflater.inflate(R.menu.member_filter, popupMenu.menu)

            val isFilterActive = viewModel.isFilterActive()
            binding.btnFilter.setImageResource(if (isFilterActive) R.drawable.ic_filter_on else R.drawable.ic_filter_off)
            popupMenu.menu.findItem(R.id.filter_clear).isVisible = isFilterActive

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.angkatan -> showGenerationFilterDialog()
                    R.id.memberType -> showMemberTypeFilterDialog()
                    R.id.filter_clear -> clearFilters()
                }
                true
            }
            popupMenu.show()
        }
    }

    private fun showGenerationFilterDialog() {
        val generations = (2015..2025).map { it.toString() }
        val currentSelections = viewModel.getCurrentGenerationFilters()
        val selectedItems = generations.map { currentSelections.contains(it) }.toBooleanArray()

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Pilih Angkatan")
            .setMultiChoiceItems(
                generations.toTypedArray(),
                selectedItems
            ) { _, which, isChecked ->
                if (isChecked) {
                    viewModel.addGenerationFilter(generations[which])
                } else {
                    viewModel.removeGenerationFilter(generations[which])
                }
            }
            .setPositiveButton("OK", null)
            .setNegativeButton("Batal", null)
            .setNeutralButton("Reset") { _, _ ->
                viewModel.clearGenerationFilters()
            }
            .create()

        dialog.show()
    }

    private fun showMemberTypeFilterDialog() {
        val memberTypes =
            listOf("Semua", "Camaba", "Pengurus", "Anggota", "Demissioner", "Istimewa")
        val currentSelections = viewModel.getCurrentMemberTypeFilters()
        val selectedItems = memberTypes.map { currentSelections.contains(it) }.toBooleanArray()

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Pilih Tipe Member")
            .setMultiChoiceItems(
                memberTypes.toTypedArray(),
                selectedItems
            ) { _, which, isChecked ->
                if (isChecked) {
                    viewModel.addMemberTypeFilter(memberTypes[which])
                } else {
                    viewModel.removeMemberTypeFilter(memberTypes[which])
                }
            }
            .setPositiveButton("OK", null)
            .setNegativeButton("Batal", null)
            .setNeutralButton("Reset") { _, _ ->
                viewModel.clearMemberTypeFilters()
            }
            .create()

        dialog.show()
    }

    private fun clearFilters() {
        viewModel.clearFilters()
        requireContext().showToast("Semua filter telah dihapus")
    }

    private fun setupRecyclerView() {

        adapter = AdmMemberAdapter(
            onItemClick = { member ->
                val intent = Intent(requireContext(), AdmMemberDetailActivity::class.java)
                intent.putExtra(AdmMemberDetailActivity.EXTRA_MEMBER, member)
                startActivity(intent)
            },
        )

        binding.rvMemberList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMemberList.adapter = adapter

        adapter.addLoadStateListener { loadState ->
            binding.progressBar.visibility =
                if (loadState.source.refresh is LoadState.Loading) View.VISIBLE else View.GONE

        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.members.collectLatest { pagingData ->
                    Log.d("Members", "fragment: Menerima data: $pagingData")
                    adapter.submitData(pagingData)

                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }
        // Cek apakah Adapter sedang loading, kosong, atau error
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                binding.progressBar.visibility =
                    if (loadStates.source.refresh is LoadState.Loading) View.VISIBLE else View.GONE

                if (loadStates.source.refresh is LoadState.NotLoading) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }

                binding.tvEmpty.visibility =
                    if (loadStates.source.refresh is LoadState.NotLoading && adapter.itemCount == 0)
                        View.VISIBLE
                    else View.GONE
            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
        }

    }

    private fun downloadExcelTemplate() {
        try {
            val inputStream = requireContext().assets.open("template_import_anggota.xlsx")

            // Simpan ke folder Download
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val outFile = File(downloadsDir, "template_import_anggota.xlsx")

            FileOutputStream(outFile).use { output ->
                inputStream.copyTo(output)
            }

            Toast.makeText(
                requireContext(),
                "File tersimpan di folder Download",
                Toast.LENGTH_LONG
            ).show()

            // (Opsional) Panggil MediaScanner biar file muncul di File Manager
            MediaScannerConnection.scanFile(
                requireContext(),
                arrayOf(outFile.absolutePath),
                arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
                null
            )

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(),
                "Gagal menyimpan file template",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun importMember(file: File) {
        viewModel.importMember(file).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val data = result.data
                    showImportResultDialog(data)
                    Log.d("ImportCheck", "Sukses upload, data = ${data.successCount}")
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    requireContext().showToast("Gagal: ${result.message}")
                    Log.d("ImportCheck", "gagal upload, data = ${result.message}")
                }

                is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
            }
        }
    }

    private fun showImportResultDialog(response: ImportMemberResponse) {
        val builder = AlertDialog.Builder(requireContext())
        val message = buildString {
            append("Berhasil: ${response.successCount}\n")
            append("Gagal: ${response.errorCount}\n\n")

            if (!response.errorData.isNullOrEmpty()) {
                append("Detail Error:\n")
                response.errorData.forEach {
                    append("• No: ${it.noMember}, Baris: ${it.row}, Error: ${it.error}\n")
                }
            }

            if (!response.successData.isNullOrEmpty()) {
                append("\nData Berhasil:\n")
                response.successData.forEach {
                    append("• ${it.noMember} (${it.action})\n")
                }
            }
        }

        builder.setTitle("Hasil Import")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ -> adapter.refresh() }
            .show()
    }

    private fun clearMemberTypeAndRefresh() {
        viewModel.clearMemberTypeFilters()
        adapter.refresh()
        requireContext().showToast("Menampilkan semua tipe member")
    }

    private fun getMemberSummary() {
        viewModel.getMemberSummary().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val data = result.data
                    binding.tvTotalMember.text = "Total anggota : ${data.totalMember}"
                    binding.tvTotalCamaba.text = "Camaba : ${data.totalMemberProspective}"
                    binding.tvTotalActive.text = "Aktif : ${data.totalMemberRegular}"
                    binding.tvTotalDemissioner.text =
                        "Demissioner : ${data.totalMemberDemissioner}"
                    binding.tvTotalPengurus.text = "Pengurus : ${data.totalMemberManagement}"
                    binding.tvTotalIstimewa.text = "Istimewa : ${data.totalMemberSpecial}"

                    // klik untuk filter
                    binding.tvTotalMember.setOnClickListener {
                        clearMemberTypeAndRefresh()
                    }

                    binding.tvTotalActive.setOnClickListener {
                        viewModel.clearMemberTypeFilters()
                        viewModel.addMemberTypeFilter("Anggota") // pastikan sesuai nama dari API
                        adapter.refresh()
                    }

                    binding.tvTotalDemissioner.setOnClickListener {
                        viewModel.clearMemberTypeFilters()
                        viewModel.addMemberTypeFilter("Demissioner")
                        adapter.refresh()
                    }
                    binding.tvTotalCamaba.setOnClickListener {
                        viewModel.clearMemberTypeFilters()
                        viewModel.addMemberTypeFilter("Camaba")
                        adapter.refresh()
                    }
                    binding.tvTotalPengurus.setOnClickListener {
                        viewModel.clearMemberTypeFilters()
                        viewModel.addMemberTypeFilter("Pengurus")
                        adapter.refresh()
                    }
                    binding.tvTotalIstimewa.setOnClickListener {
                        viewModel.clearMemberTypeFilters()
                        viewModel.addMemberTypeFilter("Istimewa")
                        adapter.refresh()
                    }

                }

                is Result.Error -> requireContext().showToast("Gagal memuat summary: ${result.message}")
                is Result.Loading -> Unit
            }
        }

    }

    private fun showExportFilterDialog() {
        val generations = (2015..2025).map { it.toString() }
        val memberTypes = listOf("camaba", "pengurus", "anggota", "demissioner", "istimewa")

        val selectedGenerations = mutableListOf<String>()
        val selectedMemberTypes = mutableListOf<String>()

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_export_filter, null)

        val generationLayout = dialogView.findViewById<LinearLayout>(R.id.generationLayout)
        val memberTypeLayout = dialogView.findViewById<LinearLayout>(R.id.memberTypeLayout)

        // Checkbox Angkatan
        generations.forEach { item ->
            val checkBox = CheckBox(requireContext()).apply {
                text = item
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) selectedGenerations.add(item) else selectedGenerations.remove(
                        item
                    )
                }
            }
            generationLayout.addView(checkBox)
        }
        // Checkbox Tipe Member
        memberTypes.forEach { item ->
            val checkBox = CheckBox(requireContext()).apply {
                text = item.replaceFirstChar { it.uppercaseChar() }
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) selectedMemberTypes.add(item) else selectedMemberTypes.remove(
                        item
                    )
                }
            }
            memberTypeLayout.addView(checkBox)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Filter Export Anggota")
            .setView(dialogView)
            .setPositiveButton("Unduh") { _, _ ->

                if (hasStoragePermission()) {
                    startExport(selectedGenerations, selectedMemberTypes)
                } else {
                    requestStoragePermission()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun startExport(generations : List<String>, memberTypes: List<String>) {
        viewModel.exportMembers(generations, memberTypes).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(binding.progressBar, true)
                    Log.d("ExportCheck", "Export in progress...")
                }
                is Result.Success -> {
                    showLoading(binding.progressBar, false)
                    requireContext().showToast("File saved at: ${result.data.absolutePath}")
                    openFile(requireContext(), result.data)
                    Log.d("ExportCheck", "Export successful: ${result.data.absolutePath}")
                }
                is Result.Error -> {
                    showLoading(binding.progressBar, false)
                    requireContext().showToast("Export failed: ${result.message}")
                    Log.e("ExportCheck", "Export error: ${result.message}")
                }
        }
    }
}
fun openFile(context: Context, file: File) {
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // Cek apakah ada aplikasi yang bisa handle file excel
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            context.showToast("Tidak ada aplikasi untuk membuka file Excel")
        }
    } catch (e: ActivityNotFoundException) {
        context.showToast("Tidak dapat membuka file: ${e.message}")
    } catch (e: Exception) {
        context.showToast("Error: ${e.localizedMessage}")
    }
}

private fun hasStoragePermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        true // No need for WRITE_EXTERNAL_STORAGE on Android 10+
    } else {
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}

private fun requestStoragePermission() {
    requestPermissions(
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ),
        STORAGE_PERMISSION_CODE
    )
}

}