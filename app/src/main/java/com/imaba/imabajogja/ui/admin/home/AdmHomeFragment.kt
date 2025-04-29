package com.imaba.imabajogja.ui.admin.home

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.response.HomeResponse
import com.imaba.imabajogja.data.utils.showLoading
import com.imaba.imabajogja.databinding.FragmentAdmHomeBinding
import com.imaba.imabajogja.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.data.utils.setTextOrPlaceholder
import com.imaba.imabajogja.data.utils.showToast
import com.imaba.imabajogja.ui.home.FileAdapter

@AndroidEntryPoint
class AdmHomeFragment : Fragment() {

    private lateinit var binding: FragmentAdmHomeBinding
    private val viewModel: AdmHomeViewModel by viewModels()

    companion object {
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

        viewModel.getHomeData().observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    // show loading
                    showLoading(binding.progressIndicator,true)
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

        editProfileOrganization()
    }

    private fun editProfileOrganization(){
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
            val title = binding.etTitle.text.toString()
            val description = binding.etDesk.text.toString()
            val vision = binding.etVisi.text.toString()
            val mission = binding.etMisi.text.toString()
            val address = binding.etAddress.text.toString()
            val email = binding.etEmail.text.toString()
            val phoneNumber = binding.etPhoneNumber.text.toString()
            val phoneNumber2 = binding.etPhoneNumber2.text.toString()

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
                    }

                    is Result.Error -> {
                        showLoading(binding.progressIndicator, false)
                        requireContext().showToast("Error: ${it.message}")
                        Log.d("data", "homeFragment: ${it.message}")
                    }
                }
            }
        }

    }

    private fun showHomeData(homeResponse: HomeResponse) {
        val data = homeResponse.data
        binding.tvTitle.text = homeResponse.data.title
        binding.etTitle.setTextOrPlaceholder(data.title, R.string.title_placeholder.toString())
        binding.etDesk.setTextOrPlaceholder(data.description, R.string.description_placeholder.toString())
        binding.etVisi.setTextOrPlaceholder(data.vision, R.string.vision_placeholder.toString())
        binding.etMisi.setTextOrPlaceholder(data.mission, R.string.mission_placeholder.toString())
        binding.etEmail.setTextOrPlaceholder(data.contactEmail, R.string.email_placeholder.toString())
        binding.etPhoneNumber.setTextOrPlaceholder(data.contactPhone, R.string.phone_placeholder.toString())
        binding.etPhoneNumber2.setTextOrPlaceholder(data.contactPhone2, R.string.phone_placeholder.toString())
        binding.etAddress.setTextOrPlaceholder(data.address, R.string.address_placeholder.toString())

        val recyclerView = binding.recyclerViewDocuments
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = FileAdapter(homeResponse.data.files ?: emptyList()) { file ->
            openPdf(file.fileUrl)
        }
        recyclerView.adapter = adapter
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
            Toast.makeText(requireContext(), "Tidak bisa membuka file PDF", Toast.LENGTH_SHORT).show()
        }
    }
}