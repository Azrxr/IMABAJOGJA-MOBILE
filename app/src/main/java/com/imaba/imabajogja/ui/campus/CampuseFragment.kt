package com.imaba.imabajogja.ui.campus

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.response.StudyMemberResponse
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.data.utils.showToast
import com.imaba.imabajogja.databinding.FragmentCampuseBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CampuseFragment : Fragment() {

    companion object {
        fun newInstance() = CampuseFragment()
    }

    private lateinit var binding: FragmentCampuseBinding
    private lateinit var adapter: StudyPlansAdapter
    private val viewModel: CampuseViewModel by viewModels()

    private lateinit var universityAdapter: ArrayAdapter<String>
    private lateinit var facultyAdapter: ArrayAdapter<String>
    private lateinit var programStudyAdapter: ArrayAdapter<String>

    private var selectedUniversityId: Int? = null
    private var selectedFacultyId: Int? = null
    private var selectedProgramStudyId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCampuseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = StudyPlansAdapter(
            onDeleteClick = { studyPlanId -> showDeleteConfirmation(studyPlanId) }
        )

        binding.rvStudyPlans.adapter = adapter
        binding.rvStudyPlans.layoutManager = LinearLayoutManager(requireContext())

        loadStudyPlans()
        binding.btnAddPlans.setOnClickListener {
            startActivity(Intent(requireContext(), AddStudyPlanActivity::class.java))
        }
        binding.btnLengkapiDoc.setOnClickListener {
            startActivity(Intent(requireContext(), DokumenCampuseActivity::class.java))
        }

        getStudyMember()
        editStudyMember()

    }

    private fun getStudyMember() {
        viewModel.getStudyMember().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvSaveCampusCurrent.visibility = View.GONE
                    binding.tvEditCampusCurrent.visibility = View.VISIBLE
                    binding.etCampusName.isEnabled = false
                    binding.etFaculty.isEnabled = false
                    binding.etStudyProgram.isEnabled = false
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

        if (studyData != null) {
            // ✅ Set Nama Universitas
            binding.etCampusName.setText(studyData.university?.name ?: "Belum dipilih")
            selectedUniversityId = studyData.university?.id

            // ✅ Set Nama Fakultas
            binding.etFaculty.setText(studyData.faculty?.name ?: "Belum dipilih")
            selectedFacultyId = studyData.faculty?.id

            // ✅ Set Nama Program Studi
            val programStudyName = studyData.programStudy?.name ?: "Belum dipilih"
            val jenjang = studyData.programStudy?.jenjang ?: ""
            binding.etStudyProgram.setText("$jenjang - $programStudyName")
            selectedProgramStudyId = studyData.programStudy?.id

        } else {
            requireContext().showToast("Data study member tidak tersedia!")
            requireContext().showToast("Data study member tidak tersedia!")

            // 🔥 Set warna orange jika data tidak ada
            val warningColor = ContextCompat.getColor(requireContext(), R.color.orange)

            binding.etCampusName.setText("Belum ada study")
            binding.tilCampusName.boxStrokeColor = warningColor

            binding.etFaculty.setText("Belum ada study")
            binding.tilFaculty.boxStrokeColor = warningColor

            binding.etStudyProgram.setText("Belum ada study")
            binding.tilStudyProgram.boxStrokeColor = warningColor
        }
    }

    private fun editStudyMember() {
        binding.tvEditCampusCurrent.setOnClickListener {
            binding.tvSaveCampusCurrent.visibility = View.VISIBLE
            binding.tvEditCampusCurrent.visibility = View.GONE
            binding.etCampusName.isEnabled = true
            binding.etFaculty.isEnabled = true
            binding.etStudyProgram.isEnabled = true
            setupUniversityDropdown()
            updateStudyMember()
        }
    }
    private fun updateStudyMember() {
        binding.tvSaveCampusCurrent.setOnClickListener {
            if (selectedUniversityId == null || selectedProgramStudyId == null) {
                requireContext().showToast("Pilih universitas dan program studi terlebih dahulu!")
                return@setOnClickListener
            }
            viewModel.updateStudyMember(
                selectedUniversityId ?: 0,
                selectedFacultyId ?: 0,
                selectedProgramStudyId ?: 0
            )
                .observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Result.Loading -> {
                            Log.d("Plan", "Menambah renana studi...")
                            requireContext() showToast ("Memproses Rencana Studi...")
                        }
                        is Result.Success -> {
                            Log.d("Plan", "Rencana study berhasil ditambah")
                            requireContext() showToast ("Rencana studi berhasil ditambah")
                            getStudyMember()
                        }

                        is Result.Error -> {
                            Log.e("Plan", "Gagal menambah rencana studi: ${result.message}")
                            requireContext() showToast ("Gagal menambah rencana studi: ${result.message}")
                        }


                    }
                }
        }
    }


    private fun loadStudyPlans() {
        viewModel.getStudyPlans().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(result.data)
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    requireContext().showToast("Gagal mengambil data: ${result.message}")
                }
            }
        }
    }

    private fun deleteStudyMember(){
        binding.btnDelete.setOnClickListener {
            viewModel.deleteStudyMember().observe(viewLifecycleOwner) { result ->
                when(result) {
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

    private fun showDeleteConfirmation(studyPlanId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Rencana study")
            .setMessage("Apakah Anda yakin ingin menghapus renacana ini?")
            .setPositiveButton("Hapus") { _, _ -> deleteStudyPlan(studyPlanId) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteStudyPlan(studyPlanId: Int) {
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

    private fun setupUniversityDropdown() {
        viewModel.getUniversity().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val universityNames = result.data.map { it.name }
                    universityAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        universityNames
                    )
                    binding.etCampusName.setAdapter(universityAdapter)

                    binding.etCampusName.setOnItemClickListener { _, _, position, _ ->
                        selectedUniversityId = result.data[position].id
                        setupProgramStudyDropdown(selectedUniversityId!!)
                        setupFacultyDropdown(selectedUniversityId!!)
                    }
                }

                is Result.Error -> {
                    requireContext().showToast("Gagal mengambil universitas: ${result.message}")
                    Log.e("Plan", "Gagal mengambil universitas: ${result.message}")
                }

                is Result.Loading -> requireContext().showToast("Memuat data university...")
            }
        }
    }

    private fun setupFacultyDropdown(universityId: Int) {
        viewModel.getFaculty(universityId).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val programStudyNames = result.data.map { it.name }
                    facultyAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        programStudyNames
                    )
                    binding.etFaculty.setAdapter(facultyAdapter)

                    binding.etFaculty.setOnItemClickListener { _, _, position, _ ->
                        selectedFacultyId = result.data[position].id
                    }
                }

                is Result.Error -> requireContext().showToast("Gagal mengambil faculty: ${result.message}")
                is Result.Loading -> requireContext().showToast("Memuat data faculty...")
            }

        }
    }
    private fun setupProgramStudyDropdown(universityId: Int) {
        viewModel.getProgramStudy(universityId).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val programStudyNames = result.data.map { it.name }
                    programStudyAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        programStudyNames
                    )
                    binding.etStudyProgram.setAdapter(programStudyAdapter)

                    binding.etStudyProgram.setOnItemClickListener { _, _, position, _ ->
                        selectedProgramStudyId = result.data[position].id
                    }
                }

                is Result.Error -> requireContext().showToast("Gagal mengambil program studi: ${result.message}")
                is Result.Loading -> requireContext().showToast("Memuat data program studi...")
            }

        }
    }
}