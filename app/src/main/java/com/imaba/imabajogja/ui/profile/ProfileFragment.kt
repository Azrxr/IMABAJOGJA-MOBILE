package com.imaba.imabajogja.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.response.ProfileResponse
import com.imaba.imabajogja.data.response.ProfileUser
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.data.utils.showToast
import com.imaba.imabajogja.databinding.FragmentProfileBinding
import com.imaba.imabajogja.ui.MainViewModel
import com.imaba.imabajogja.ui.authentication.UpdatePasswordActivity
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
    private val REQUEST_UPDATE_PROFILE = 1001

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
                startActivityForResult(intent, REQUEST_UPDATE_PROFILE)
            } ?: requireContext().showToast("Data profil belum tersedia")
        }

        binding.btnLogout.setOnClickListener {
            mainViewModel.logout()
            startActivity(Intent(requireContext(), WelcomeActivity::class.java))
        }
        binding.btnChangePassword.setOnClickListener {
            startActivity(Intent(requireContext(), UpdatePasswordActivity::class.java))
        }
        binding.btnAbout.setOnClickListener {
            startActivity(Intent(requireContext(), AboutActivity::class.java))
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_UPDATE_PROFILE && resultCode == Activity.RESULT_OK) {
            viewModel.getProfileData() // Ambil ulang data profile terbaru
        }
    }

    private fun showProfileData(data: ProfileResponse) {
        val profile = data.data
        binding.tvName.text = profile?.username ?: getString(R.string.empty)
        binding.tvFullname.text = profile?.fullname ?: getString(R.string.empty)
        binding.tvEmail.text = profile?.email ?: getString(R.string.empty)
        binding.tvPhone.text = profile?.phoneNumber ?: getString(R.string.empty)
        binding.tvPhoneNumber.text = profile?.phoneNumber ?: getString(R.string.empty)
        binding.tvAddress.text = if (profile?.fullAddress != null && profile.district != null && profile.regency != null && profile.province != null) {
            "${profile.fullAddress}, ${profile.district}, ${profile.regency}, ${profile.province}"
        } else {
            getString(R.string.empty)
        }
        binding.tvGender.text = profile?.gender ?: getString(R.string.empty)
        binding.tvNISN.text = profile?.nisn?.toString() ?: getString(R.string.empty)
        binding.tvType.text = profile?.memberType ?: getString(R.string.empty)
        binding.tvReligion.text = profile?.agama ?: getString(R.string.empty)
        binding.tvPlaceDate.text = if (profile?.tempat != null && profile.tanggalLahir != null) {
            "${profile.tempat}, ${profile.tanggalLahir}"
        } else {
            getString(R.string.empty)
        }

        binding.tvGeneration.text = profile?.angkatan ?: getString(R.string.empty)

        Glide.with(requireContext())
            .load(profile?.profileImgUrl)
            .placeholder(R.drawable.ic_user) // Placeholder jika loading gambar
            .error(R.drawable.ic_image_broken) // Gambar default jika gagal memuat
            .into(binding.ivProfile)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}