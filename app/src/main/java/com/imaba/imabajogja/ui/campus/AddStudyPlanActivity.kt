package com.imaba.imabajogja.ui.campus

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.data.utils.showToast
import com.imaba.imabajogja.databinding.ActivityAddStudyPlanBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddStudyPlanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStudyPlanBinding
    private val viewModel: CampuseViewModel by viewModels()

    private lateinit var universityAdapter: ArrayAdapter<String>
    private lateinit var programStudyAdapter: ArrayAdapter<String>

    private var selectedUniversityId: Int? = null
    private var selectedProgramStudyId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStudyPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupUniversityDropdown()
        addStudyPlan()
    }

    private fun addStudyPlan() {
        binding.tvSaveCampusPlan.setOnClickListener {
            if (selectedUniversityId == null || selectedProgramStudyId == null) {
                showToast("Pilih universitas dan program studi terlebih dahulu!")
                return@setOnClickListener
            }
            viewModel.getStudyPlans().observe(this) { result ->
                if (result is Result.Success) {
                    val studyPlans = result.data

                    val uniqueUniversities = studyPlans.map { it.universityId }.distinct().size

                    if (uniqueUniversities >= 2 && !studyPlans.any { it.universityId == selectedUniversityId }) {
                        AlertDialog.Builder(this)
                            .setTitle("Batas Maksimum Universitas")
                            .setMessage("Anda hanya dapat menambahkan maksimal 2 universitas.")
                            .setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                                binding.etCampusNamePlan.text.clear()
                                binding.etProdiPlan.text.clear()
                                selectedUniversityId = null
                                selectedProgramStudyId = null
                            }
                            .show()
                        return@observe
                    }

                    // 🔥 Cek jumlah program studi dalam universitas yang sama
                    val studyCount = studyPlans.count { it.universityId == selectedUniversityId }

                    if (studyCount >= 2) {
                        // 🚨 Jika sudah ada 2 program studi dalam universitas yang sama
                        AlertDialog.Builder(this)
                            .setTitle("Batas Maksimum Program Studi")
                            .setMessage("Anda hanya dapat menambahkan maksimal 2 program studi dalam satu universitas.")
                            .setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                                binding.etProdiPlan.text.clear()
                                selectedProgramStudyId = null
                            }
                            .show()
                        return@observe
                    }

                    viewModel.addStudyPlan(selectedUniversityId ?: 0, selectedProgramStudyId ?: 0)
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
    }

    private fun setupUniversityDropdown() {
        viewModel.getUniversity().observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val universityNames = result.data.map { it.name }
                    universityAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        universityNames
                    )
                    binding.etCampusNamePlan.setAdapter(universityAdapter)

                    binding.etCampusNamePlan.setOnItemClickListener { _, _, position, _ ->
                        selectedUniversityId = result.data[position].id
                        setupProgramStudyDropdown(selectedUniversityId!!)
                    }
                }

                is Result.Error -> {
                    showToast("Gagal mengambil universitas: ${result.message}")
                    Log.e("Plan", "Gagal mengambil universitas: ${result.message}")
                }

                is Result.Loading -> showToast("Memuat data university...")
            }
        }
    }

    private fun setupProgramStudyDropdown(universityId: Int) {
        viewModel.getProgramStudy(universityId).observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val programStudyNames = result.data.map { it.name }
                    programStudyAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        programStudyNames
                    )
                    binding.etProdiPlan.setAdapter(programStudyAdapter)

                    binding.etProdiPlan.setOnItemClickListener { _, _, position, _ ->
                        selectedProgramStudyId = result.data[position].id
                    }
                }

                is Result.Error -> showToast("Gagal mengambil program studi: ${result.message}")
                is Result.Loading -> showToast("Memuat data program studi...")
            }

        }
    }
}