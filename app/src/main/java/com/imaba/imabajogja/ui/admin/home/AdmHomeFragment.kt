package com.imaba.imabajogja.ui.admin.home

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.response.HomeResponse
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.data.utils.setTextOrPlaceholder
import com.imaba.imabajogja.data.utils.showLoading
import com.imaba.imabajogja.data.utils.showToast
import com.imaba.imabajogja.data.utils.uriToFilePdf
import com.imaba.imabajogja.databinding.FragmentAdmHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AdmHomeFragment : Fragment() {

    private lateinit var binding: FragmentAdmHomeBinding
    private val viewModel: AdmHomeViewModel by viewModels()
    private var selectedFile: File? = null

    companion object {
        private const val REQUEST_PICK_DOCUMENT = 2000
        fun newInstance() = AdmHomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdmHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getHomeData()
        editProfileOrganization()
        binding.btnDocPlus.setOnClickListener {
            pickDocumentLauncher.launch("application/pdf")
        }
    }

    private fun getHomeData() {
        viewModel.getHomeData().observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    // show loading
                    showLoading(binding.progressIndicator, true)
                }

                is Result.Success -> {
                    showLoading(binding.progressIndicator, false)
                    listOf(
                        binding.etTitle,
                        binding.etDesk,
                        binding.etVisi,
                        binding.etMisi,
                        binding.etEmail,
                        binding.etPhoneNumber,
                        binding.etPhoneNumber2,
                        binding.etAddress
                    ).forEach { it.isEnabled = false }
                    showHomeData(it.data)
                }

                is Result.Error -> {
                    showLoading(binding.progressIndicator, false)
                    // show error message
                    requireContext().showToast("Error: ${it.message}")
                    Log.d("data", "homeFragment: ${it.message}")
                }
            }
        }
    }

    private fun editProfileOrganization() {
        binding.btnEdit.setOnClickListener {
            binding.btnEdit.visibility = View.GONE
            binding.btnSave.visibility = View.VISIBLE
            listOf(
                binding.etTitle,
                binding.etDesk,
                binding.etVisi,
                binding.etMisi,
                binding.etEmail,
                binding.etPhoneNumber,
                binding.etPhoneNumber2,
                binding.etAddress
            ).forEach { it.isEnabled = true }
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val description = binding.etDesk.text.toString().trim()
            val vision = binding.etVisi.text.toString().trim()
            val mission = binding.etMisi.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()
            val phoneNumber2 = binding.etPhoneNumber2.text.toString().trim()

            // Validation checks
            if (title.isBlank()) {
                binding.etTitle.error = "Judul tidak boleh kosong"
                binding.tilTitle.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            }

            if (description.isBlank() || description.length < 200) {
                binding.etDesk.error = "Deskripsi minimal harus 200 karakter"
                binding.tilDesk.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            }

            if (vision.isBlank() || vision.length < 30) {
                binding.etVisi.error = "Visi minimal harus 30 karakter"
                binding.tilVisi.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            }

            if (mission.isBlank() || mission.length < 30) {
                binding.etMisi.error = "Misi minimal harus 30 karakter"
                binding.tilMisi.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            }
            if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.error = "Email harus dalam format yang benar"
                binding.tilEmail.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            }

            if (phoneNumber.isBlank() || !phoneNumber.matches("\\d+".toRegex())) {
                binding.etPhoneNumber.error = "Nomor telepon harus berupa angka"
                binding.tilPhoneNumber.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            }

            if (phoneNumber2.isNotBlank() && !phoneNumber2.matches("\\d+".toRegex())) {
                binding.etPhoneNumber2.error = "Nomor telepon 2 harus berupa angka"
                binding.tilPhoneNumber2.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            }
            save(
                title,
                description,
                vision,
                mission,
                address,
                email,
                phoneNumber,
                phoneNumber2
            )
        }

        binding.etPhoneNumber.setOnClickListener {
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()
            if (phoneNumber.matches("\\d+".toRegex())) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/62$phoneNumber"))
                startActivity(intent)
            } else {
                requireContext().showToast("Nomor telepon tidak valid")
            }
        }

        binding.etPhoneNumber2.setOnClickListener {
            val phoneNumber2 = binding.etPhoneNumber2.text.toString().trim()
            if (phoneNumber2.matches("\\d+".toRegex())) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/62$phoneNumber2"))
                startActivity(intent)
            } else {
                requireContext().showToast("Nomor telepon 2 tidak valid")
            }
        }
    }

    private fun save(
        title: String,
        description: String,
        vision: String,
        mission: String,
        address: String,
        email: String,
        phoneNumber: String,
        phoneNumber2: String
    ) {
        viewModel.updateProfileOrganization(
            title,
            description,
            vision,
            mission,
            address,
            email,
            phoneNumber,
            phoneNumber2
        ).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    showLoading(binding.progressIndicator, true)
                }

                is Result.Success -> {
                    showLoading(binding.progressIndicator, false)
                    requireContext().showToast("Berhasil mengupdate data")
                    listOf(
                        binding.etTitle,
                        binding.etDesk,
                        binding.etVisi,
                        binding.etMisi,
                        binding.etEmail,
                        binding.etPhoneNumber,
                        binding.etPhoneNumber2,
                        binding.etAddress
                    ).forEach { it.isEnabled = false }
                    binding.btnEdit.visibility = View.VISIBLE
                    binding.btnSave.visibility = View.GONE
                    getHomeData() // Refresh data
                }

                is Result.Error -> {
                    showLoading(binding.progressIndicator, false)
                    requireContext().showToast("Error: ${it.message}")
                    Log.d("data", "homeFragment: ${it.message}")
                }
            }
        }
    }

    private fun showHomeData(homeResponse: HomeResponse) {
        val data = homeResponse.data
        binding.tvTitle.text = homeResponse.data.title
        binding.etTitle.setTextOrPlaceholder(data.title, R.string.title_placeholder.toString())
        binding.etDesk.setTextOrPlaceholder(
            data.description,
            R.string.description_placeholder.toString()
        )
        binding.etVisi.setTextOrPlaceholder(data.vision, R.string.vision_placeholder.toString())
        binding.etMisi.setTextOrPlaceholder(data.mission, R.string.mission_placeholder.toString())
        binding.etEmail.setTextOrPlaceholder(
            data.contactEmail,
            R.string.email_placeholder.toString()
        )
        binding.etPhoneNumber.setTextOrPlaceholder(
            data.contactPhone,
            R.string.phone_placeholder.toString()
        )
        binding.etPhoneNumber2.setTextOrPlaceholder(
            data.contactPhone2,
            R.string.phone_placeholder.toString()
        )
        binding.etAddress.setTextOrPlaceholder(
            data.address,
            R.string.address_placeholder.toString()
        )

        val files = homeResponse.data.files ?: emptyList()
        if (files.isEmpty()) {
            binding.listEmpty.visibility = View.VISIBLE
            binding.recyclerViewDocuments.visibility = View.GONE
        } else {
            binding.listEmpty.visibility = View.GONE
            binding.recyclerViewDocuments.visibility = View.VISIBLE

            val adapter = AdmFileAdapter(
                files = homeResponse.data.files ?: emptyList(),
                onItemClicked = { file -> openPdf(file.fileUrl) },
                onDeleteClicked = { file ->
                    viewModel.deleteDocumentOrganization(file.id).observe(viewLifecycleOwner) {
                        when (it) {
                            is Result.Loading -> showLoading(binding.progressIndicator, true)
                            is Result.Success -> {
                                showLoading(binding.progressIndicator, false)
                                requireContext().showToast("File berhasil dihapus")
                                getHomeData()
                            }

                            is Result.Error -> {
                                showLoading(binding.progressIndicator, false)
                                requireContext().showToast("Gagal menghapus file: ${it.message}")
                            }
                        }
                    }
                },
                isDeleteVisible = true // 🔥 Tampilkan tombol delete
            )

            binding.recyclerViewDocuments.apply {
                layoutManager = LinearLayoutManager(requireContext())
                this.adapter = adapter
            }
        }
    }

    private fun openPdf(fileUrl: String) {
        try {
            val uri = Uri.parse(fileUrl) // Ubah URL menjadi URI
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf") // Set type file PDF
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            startActivity(intent) // Jalankan intent
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Tidak bisa membuka file PDF", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private val pickDocumentLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedFile = uriToFilePdf(it, requireContext())
                showUploadDialog() // lanjut upload dengan input title dan description
            } ?: requireContext().showToast("Dokumen tidak dipilih")
        }

    private fun showUploadDialog() {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 24, 32, 4)
        }

        val etTitle = EditText(requireContext()).apply {
            hint = "Masukkan judul dokumen"
            inputType = InputType.TYPE_CLASS_TEXT
        }

        val etDescription = EditText(requireContext()).apply {
            hint = "Masukkan deskripsi dokumen"
            inputType = InputType.TYPE_CLASS_TEXT
        }

        // Tambahkan ke layout
        layout.addView(etTitle)
        layout.addView(etDescription)

        // Buat dialog
        AlertDialog.Builder(requireContext())
            .setTitle("Upload Dokumen")
            .setView(layout)
            .setPositiveButton("Upload") { _, _ ->
                val title = etTitle.text.toString().trim()
                val desc = etDescription.text.toString().trim()

                if (title.isBlank() || desc.isBlank()) {
                    requireContext().showToast("Judul dan deskripsi tidak boleh kosong")
                    return@setPositiveButton
                }

                uploadDocument(title, desc)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun uploadDocument(title: String, description: String) {
        val file = selectedFile ?: return requireContext().showToast("File belum dipilih")

        viewModel.uploadDocumentOrganization(file, title, description)
            .observe(viewLifecycleOwner) {
                when (it) {
                    is Result.Loading -> showLoading(binding.progressIndicator, true)
                    is Result.Success -> {
                        showLoading(binding.progressIndicator, false)
                        requireContext().showToast("Dokumen berhasil diupload")
                        selectedFile = null
                        getHomeData() // Refresh data
                    }

                    is Result.Error -> {
                        showLoading(binding.progressIndicator, false)
                        requireContext().showToast("Error: ${it.message}")
                        Log.e("UploadDoc", "Error: ${it.message}")
                    }
                }
            }
    }

}
