package com.imaba.imabajogja.ui.campus

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
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

    private fun loadUniversityData(etUniversity: AutoCompleteTextView) {
        viewModel.getUniversity().observe(viewLifecycleOwner) { result ->
            if (result is Result.Success) {
                val universities = result.data
                val universityNames = universities.map { it.name }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, universityNames)

                etUniversity.setAdapter(adapter)
                etUniversity.setOnItemClickListener { _, _, position, _ ->
                    val selectedUniversity = universities[position]
                    etUniversity.setText(selectedUniversity.name)
                    loadProgramStudyData(selectedUniversity.id)
                }
            }
        }
    }

    private fun loadProgramStudyData(universityId: Int) {
        viewModel.getProgramStudy(universityId).observe(viewLifecycleOwner) { result ->
            if (result is Result.Success) {
                val programs = result.data
                val programNames = programs.map { it.name }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, programNames)

                binding.etStudyProgram.setAdapter(adapter)
                binding.etStudyProgram.setOnItemClickListener { _, _, position, _ ->
                    val selectedProgram = programs[position]
                    binding.etStudyProgram.setText(selectedProgram.name)
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
}