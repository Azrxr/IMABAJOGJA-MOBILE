package com.imaba.imabajogja.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.imaba.imabajogja.data.response.ProfileResponse
import com.imaba.imabajogja.data.response.ProfileUser
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.data.utils.showLoading
import com.imaba.imabajogja.data.utils.showToast
import com.imaba.imabajogja.databinding.FragmentProfileBinding
import com.imaba.imabajogja.ui.MainViewModel
import com.imaba.imabajogja.ui.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private val viewModel: ProfileViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var profileData: ProfileUser? = null

        viewModel.getProfileData().observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    // show loading
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    // do something with the data
                    profileData = it.data.data
                    showProfileData(it.data)
                }

                is Result.Error -> {
                    showLoading(false)
                    // show error message
                    requireContext().showToast("Error: ${it.message}")
                    Log.d("data", "homeFragment: ${it.message}")
                }
            }
        }

        binding.btnEdit.setOnClickListener {
            profileData?.let { profile ->
                val intent = EditProfileActivity.newIntent(requireContext(), profile)
                startActivity(intent)
            } ?: requireContext().showToast("Data profil belum tersedia")
        }

        binding.btnLogout.setOnClickListener {
            mainViewModel.logout()
            startActivity(Intent(requireContext(), WelcomeActivity::class.java))
        }
    }

    private fun showProfileData(data: ProfileResponse) {
        val profile = data.data
        binding.tvName.text = profile.fullname
        binding.tvEmail.text = profile.email
        binding.tvPhone.text = profile.phoneNumber.toString()
        binding.tvAddress.text = profile.fullAddress
        binding.tvGender.text = profile.gender
        binding.tvNISN.text = profile.nisn.toString()
        binding.tvType.text = profile.memberType
        binding.tvReligion.text = profile.agama
        binding.tvPlaceDate.text = "${profile.tempat}, ${profile.tanggalLahir}"
        binding.tvGeneration.text = profile.angkatan?.toString() ?: "Tidak tersedia"

        Glide.with(requireContext()).load(profile.profileImgUrl).into(binding.ivProfile)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}