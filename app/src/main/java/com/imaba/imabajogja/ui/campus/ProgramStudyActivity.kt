package com.imaba.imabajogja.ui.campus

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.data.utils.showToast
import com.imaba.imabajogja.databinding.ActivityProgramStudyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProgramStudyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgramStudyBinding
    private val viewModel: CampuseViewModel by viewModels()

    private lateinit var adapter: ProgramStudyAdapter
    private var activeJenjangFilter: String? = null
    private var searchQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProgramStudyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadProgramStudy()
        setupSearch()
        setupFilterButton()
    }

    private fun addStudyPlan(
        universityId: Int, programStudyId: Int
    ){
        viewModel.getStudyPlans().observe(this) { result ->
            if (result is Result.Success) {
                val studyPlans = result.data

                val uniqueUniversities = studyPlans.map { it.universityId }.distinct().size

                if (uniqueUniversities >= 2 && !studyPlans.any { it.universityId == universityId }) {
                    AlertDialog.Builder(this)
                        .setTitle("Batas Maksimum Universitas")
                        .setMessage("Anda hanya dapat menambahkan maksimal 2 universitas.")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                    return@observe
                }

                // 🔥 Cek jumlah program studi dalam universitas yang sama
                val studyCount = studyPlans.count { it.universityId == universityId }

                if (studyCount >= 2) {
                    // 🚨 Jika sudah ada 2 program studi dalam universitas yang sama
                    AlertDialog.Builder(this)
                        .setTitle("Batas Maksimum Program Studi")
                        .setMessage("Anda hanya dapat menambahkan maksimal 2 program studi dalam satu universitas.")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                    return@observe
                }

                viewModel.addStudyPlan(universityId, programStudyId)
                    .observe(this) { result ->
                        when (result) {
                            is Result.Success -> {
                                Log.d("Plan", "Rencana study berhasil ditambah")
                                showToast("Rencana studi berhasil ditambah")
                                val intent = Intent()
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }

                            is Result.Error -> {
                                Log.e("Plan", "Gagal menambah rencana studi: ${result.message}")
                                showToast("Gagal menambah rencana studi: ${result.message}")
                            }

                            is Result.Loading -> {
                                Log.d("Plan", "Menambah renana studi...")
                                showToast("Memproses Rencana Studi...")
                            }
                        }
                    }
            }
        }
    }
    private fun loadProgramStudy() {
        adapter = ProgramStudyAdapter(
            onAddClick = { item ->
                val universityId = item.universityId ?: return@ProgramStudyAdapter
                val programStudyId = item.programStudyId ?: return@ProgramStudyAdapter
                addStudyPlan(universityId, programStudyId)
            },
        )
        binding.rvProgramStudy.adapter = adapter
        binding.rvProgramStudy.layoutManager = LinearLayoutManager(this)


        viewModel.getAllPrograStudy(
            search = searchQuery,
            jenjang = activeJenjangFilter
        ).observe(this) { result ->
            when (result) {
                is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
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
                .setSingleChoiceItems(jenjangList.toTypedArray(), jenjangList.indexOf(activeJenjangFilter)) { _, which ->
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