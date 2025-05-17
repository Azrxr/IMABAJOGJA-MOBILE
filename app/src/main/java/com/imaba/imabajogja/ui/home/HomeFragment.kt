package com.imaba.imabajogja.ui.home

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
import com.imaba.imabajogja.data.response.HomeResponse
import com.imaba.imabajogja.databinding.FragmentHomeBinding
import com.imaba.imabajogja.data.utils.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getHomeData().observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    // show loading
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    // do something with the data
                    showHomeData(it.data)
                }

                is Result.Error -> {
                    showLoading(false)
                    // show error message
                    showToast("Error: ${it.message}")
                    Log.d("data", "homeFragment: ${it.message}")
                }
            }
        }
    }

    private fun showHomeData(homeResponse: HomeResponse) {
        binding.tvTitle.text = homeResponse.data.title
        binding.tvDesc.text = homeResponse.data.description
        binding.tvAddress.text = homeResponse.data.address
        binding.tvContactEmail.text = homeResponse.data.contactEmail
        binding.tvContacPhone.text = homeResponse.data.contactPhone
        binding.tvContacPhone2.text = homeResponse.data.contactPhone2
        binding.tvVisi.text = homeResponse.data.vision
        binding.tvMisi.text = homeResponse.data.mission

        val files = homeResponse.data.files
        if (files.isNullOrEmpty()) {
            binding.listEmpty.visibility = View.VISIBLE
            binding.recyclerViewDocuments.visibility = View.GONE
        } else {
            binding.listEmpty.visibility = View.GONE
            binding.recyclerViewDocuments.visibility = View.VISIBLE
            val adapter = FileAdapter(files) { file ->
                openPdf(file.fileUrl)
            }
            binding.recyclerViewDocuments.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerViewDocuments.adapter = adapter
        }

//        val recyclerView = binding.recyclerViewDocuments
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        val adapter = FileAdapter(homeResponse.data.files) { file ->
//            openPdf(file.fileUrl)
//        }
//        recyclerView.adapter = adapter
//        binding.listEmpty.visibility = if (homeResponse.data.files.isEmpty()) View.VISIBLE else View.GONE
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
    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}