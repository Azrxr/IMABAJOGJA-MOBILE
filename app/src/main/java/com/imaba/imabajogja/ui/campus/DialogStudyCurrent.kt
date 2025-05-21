package com.imaba.imabajogja.ui.campus

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.data.utils.showToast
import com.imaba.imabajogja.databinding.DialogCurrentStudyBinding
import com.imaba.imabajogja.ui.admin.campuse.DialogStudyPlanAdd
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DialogStudyCurrent : DialogFragment(){

    private var _binding: DialogCurrentStudyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CampuseViewModel by viewModels()

    private lateinit var universityAdapter: ArrayAdapter<String>
    private lateinit var facultyAdapter: ArrayAdapter<String>
    private lateinit var programStudyAdapter: ArrayAdapter<String>

    private var selectedUniversityId: Int? = null
    private var selectedFacultyId: Int? = null
    private var selectedProgramStudyId: Int? = null

    var onSuccessListener: (() -> Unit)? = null

    private val memberId: Int by lazy {
        arguments?.getInt(ARG_MEMBER_ID) ?: 0
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogCurrentStudyBinding.inflate(requireActivity().layoutInflater)

        val dialog = AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setTitle("Edit Studi Saat Ini")
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Simpan", null)
            .create()

        dialog.setOnShowListener {
            setupUniversityDropdown()

            val btnSave = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btnSave.setOnClickListener {
                val campusText = binding.etCampusName.text.toString()
                val facultyText = binding.etFaculty.text.toString()
                val programText = binding.etProdiPlan.text.toString()

                // 🔍 Validasi kosong
                if (campusText.isBlank()) {
                    binding.etCampusName.error = "Nama kampus tidak boleh kosong"
                    return@setOnClickListener
                }
                if (facultyText.isBlank()) {
                    binding.etFaculty.error = "Fakultas tidak boleh kosong"
                    return@setOnClickListener
                }
                if (programText.isBlank()) {
                    binding.etProdiPlan.error = "Program studi tidak boleh kosong"
                    return@setOnClickListener
                }

                if (selectedUniversityId == null || selectedProgramStudyId == null || selectedFacultyId == null) {
                    showToast("Data belum lengkap. Pastikan semua pilihan sudah dipilih.")
                    return@setOnClickListener
                }

                // ✅ Panggil update
                viewModel.updateStudyMember(
                    selectedUniversityId ?: 0,
                    selectedFacultyId ?: 0,
                    selectedProgramStudyId ?: 0
                ).observe(this) { result ->
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            showToast("Memproses...")
                        }
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            showToast("Rencana studi berhasil diperbarui!")
                            onSuccessListener?.invoke() // notifikasi ke fragment pemanggil
                            dismiss()
                        }
                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            showToast("Gagal: ${result.message}")
                        }
                    }
                }
            }
        }

        return dialog
    }

    private fun setupUniversityDropdown() {
        viewModel.getUniversity().observe(this) { result ->
            when (result) {
                is com.imaba.imabajogja.data.utils.Result.Success -> {
                    binding.progressBar.visibility = View.GONE
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
                is com.imaba.imabajogja.data.utils.Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showToast("Gagal mengambil universitas: ${result.message}")
                }
                is com.imaba.imabajogja.data.utils.Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    showToast("Memuat data university...")
                }
            }
        }
    }

    private fun setupFacultyDropdown(universityId: Int) {
        viewModel.getFaculty(universityId).observe(this) { result ->
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
        viewModel.getProgramStudy(universityId).observe(this) { result ->
            when (result) {
                is com.imaba.imabajogja.data.utils.Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val programStudyNames = result.data.map { it.name }
                    programStudyAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        programStudyNames
                    )
                    binding.etProdiPlan.setAdapter(programStudyAdapter)

                    binding.etProdiPlan.setOnItemClickListener { _, _, position, _ ->
                        selectedProgramStudyId = result.data[position].id
                    }
                }
                is com.imaba.imabajogja.data.utils.Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showToast("Gagal mengambil program studi: ${result.message}")
                }
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    showToast("Memuat data program studi...")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_MEMBER_ID = "member_id"
        private const val ARG_MODE = "mode"

        fun newInstanceForUser(): DialogStudyCurrent {
            return DialogStudyCurrent().apply {
                arguments = Bundle().apply {
                    putString(ARG_MODE, "USER")
                }
            }
        }

        fun newInstanceForAdmin(memberId: Int): DialogStudyCurrent {
            return DialogStudyCurrent().apply {
                arguments = Bundle().apply {
                    putString(ARG_MODE, "ADMIN")
                    putInt(ARG_MEMBER_ID, memberId)
                }
            }
        }
    }

    fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}