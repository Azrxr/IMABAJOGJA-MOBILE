package com.imaba.imabajogja.ui.admin.campuse


import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.databinding.DialogAddStudyPlaneBinding
import com.imaba.imabajogja.ui.campus.CampuseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DialogStudyPlanAdd : DialogFragment() {

    private var _binding: DialogAddStudyPlaneBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CampuseViewModel by viewModels()
    private val admCampusViewModel: AdmCampuseViewModel by viewModels()

    private lateinit var universityAdapter: ArrayAdapter<String>
    private lateinit var programStudyAdapter: ArrayAdapter<String>

    private var selectedUniversityId: Int? = null
    private var selectedProgramStudyId: Int? = null

    var onSuccessListener: (() -> Unit)? = null

    private val memberId: Int by lazy {
        arguments?.getInt(ARG_MEMBER_ID) ?: 0
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddStudyPlaneBinding.inflate(requireActivity().layoutInflater)

        val dialog = AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setTitle("Tambah Rencana Studi")
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Simpan", null)
            .create()

        dialog.setOnShowListener{
            setupUniversityDropdown()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (selectedUniversityId == null || selectedProgramStudyId == null) {
                    showToast("Pilih universitas dan program studi terlebih dahulu!")
                } else {
                    saveStudyPlan()
                }
            }
        }
        return dialog
    }

    private fun saveStudyPlan() {
            admCampusViewModel.getMemberDetail(memberId).observe(this) { result ->
                if (result is Result.Success) {
                    val studyPlans = result.data.data?.studyPlans

                    // Skip validation if studyPlans is null
                    if (studyPlans == null) {
                        addStudyPlanToServer()
                        return@observe
                    }

                    // Validasi maksimal 2 universitas
                    val uniqueUniversities = studyPlans.map { it.universityId }.distinct().size
                    if (uniqueUniversities >= 2 && !studyPlans.any { it.universityId == selectedUniversityId }) {
                        showToast("Anda hanya dapat menambahkan maksimal 2 universitas.")
                        return@observe
                    }

                    // Validasi maksimal 2 program studi per universitas
                    val studyCount = studyPlans.count { it.universityId == selectedUniversityId }
                    if (studyCount >= 2) {
                        showToast("Anda hanya dapat menambahkan maksimal 2 program studi dalam satu universitas.")
                        return@observe
                    }

                    addStudyPlanToServer()
                }

        }
    }

    private fun addStudyPlanToServer() {
        admCampusViewModel.addStudyPlan(memberId,selectedUniversityId ?: 0, selectedProgramStudyId ?: 0)
            .observe(this) { result ->
                when (result) {
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        showToast("Rencana studi berhasil ditambah")
                        onSuccessListener?.invoke()
                        dismiss()
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

    private fun setupUniversityDropdown() {
        viewModel.getUniversity().observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val universityNames = result.data.map { it.name }
                    universityAdapter = ArrayAdapter(
                        requireContext(),
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
                    binding.progressBar.visibility = View.GONE
                    showToast("Gagal mengambil universitas: ${result.message}")
                }
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    showToast("Memuat data university...")
                }
            }
        }
    }

    private fun setupProgramStudyDropdown(universityId: Int) {
        viewModel.getProgramStudy(universityId).observe(this) { result ->
            when (result) {
                is Result.Success -> {
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
                is Result.Error -> {
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

        fun newInstance(memberId: Int): DialogStudyPlanAdd {
            return DialogStudyPlanAdd().apply {
                arguments = Bundle().apply {
                    putInt(ARG_MEMBER_ID, memberId)
                }
            }

        }
    }
    fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}