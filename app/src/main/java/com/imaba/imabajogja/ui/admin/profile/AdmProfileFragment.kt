package com.imaba.imabajogja.ui.admin.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.response.AdmDataUser
import com.imaba.imabajogja.data.response.AdmProfileResponse
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.data.utils.reduceFileImage
import com.imaba.imabajogja.data.utils.showLoading
import com.imaba.imabajogja.data.utils.showToast
import com.imaba.imabajogja.data.utils.uriToFile
import com.imaba.imabajogja.databinding.FragmentAdmProfileBinding
import com.imaba.imabajogja.ui.AboutActivity
import com.imaba.imabajogja.ui.MainViewModel
import com.imaba.imabajogja.ui.authentication.AdmUpdatePasswordActivity
import com.imaba.imabajogja.ui.profile.ProfileViewModel
import com.imaba.imabajogja.ui.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AdmProfileFragment : Fragment() {

    companion object {
        private const val PROFILE_DATA = "profile_data"
        private const val REQUEST_CAPTURE_IMAGE = 1000
        private const val REQUEST_PICK_IMAGE = 1000
        fun newInstance() = AdmProfileFragment()
    }

    private val mainViewModel: MainViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var binding: FragmentAdmProfileBinding
    private val viewModel: AdmProfileViewModel by viewModels()

    private lateinit var provinceAdapter: ArrayAdapter<String>
    private lateinit var regencyAdapter: ArrayAdapter<String>
    private lateinit var districtAdapter: ArrayAdapter<String>

    private var selectedImageFile: File? = null

    private var selectedProvinceId: Int? = null
    private var selectedRegencyId: Int? = null
    private var selectedDistrictId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdmProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var profileData: AdmDataUser? = null

        viewModel.getProfileData().observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    // show loading
                    showLoading(binding.progressIndicator, true)
                }

                is Result.Success -> {
                    showLoading(binding.progressIndicator, false)
                    // do something with the data
                    profileData = it.data.data
                    setupEdtEnabled(false)
                    showProfileData(it.data)
                }

                is Result.Error -> {
                    showLoading(binding.progressIndicator, false)
                    // show error message
                    requireContext().showToast("Error: ${it.message}")
                    Log.d("data", "homeFragment: ${it.message}")
                }
            }
        }
        binding.btnLogout.setOnClickListener {
            mainViewModel.logout()
            startActivity(Intent(requireContext(), WelcomeActivity::class.java))
        }
        binding.btnChangePassword.setOnClickListener {
            startActivity(Intent(requireContext(), AdmUpdatePasswordActivity::class.java))
        }
        binding.btnAbout.setOnClickListener {
            startActivity(Intent(requireContext(), AboutActivity::class.java))
        }
        editProfile()
        pickImageFromGallery()
    }

    @Deprecated("Use registerForActivityResult instead")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                // 🔥 Gunakan utils yang aman
                selectedImageFile = uriToFile(selectedImageUri, requireContext()).reduceFileImage()

                // Tampilkan preview
                Glide.with(requireContext())
                    .load(selectedImageUri)
                    .into(binding.ivProfile)

                uploadPhoto()
            } else {
                requireContext().showToast("Gagal memilih gambar")
            }
        }
    }


    private fun showProfileData(data: AdmProfileResponse) {
        val user = data.data
        val profile = user?.admin
        binding.tvName.text = user?.username ?: getString(R.string.empty)
        binding.tvEmail.text = user?.email ?: getString(R.string.empty)
        binding.tvPhoneNumber.text = profile?.phoneNumber ?: getString(R.string.empty)
        binding.tvFullAddress.text =
            if (profile?.fullAddress != null && profile.district?.name != null && profile.regency?.name != null && profile.provincy?.name != null) {
                "${profile.fullAddress}, ${profile.district.name}, ${profile.regency.name}, ${profile.provincy?.name}"
            } else {
                getString(R.string.empty)
            }

        binding.etUsername.setText(user?.username)
        binding.etEmail.setText(user?.email)
        binding.etFullname.setText(profile?.fullname)
        binding.etPhoneNumber.setText(profile?.phoneNumber)
        binding.etAddress.setText(profile?.fullAddress)
        binding.etProvince.setText(profile?.provincy?.name)
        selectedProvinceId = profile?.provincyId
        binding.etCity.setText(profile?.regency?.name)
        selectedRegencyId = profile?.regencyId
        binding.etDistrict.setText(profile?.district?.name)
        selectedDistrictId = profile?.districtId


        Glide.with(requireContext())
            .load(profile?.profileImgUrl)
            .placeholder(R.drawable.ic_user) // Placeholder jika loading gambar
            .error(R.drawable.ic_image_broken) // Gambar default jika gagal memuat
            .into(binding.ivProfile)
    }

    private fun setupEdtEnabled(isEdit: Boolean) {
        binding.etUsername.isEnabled = isEdit
        binding.etEmail.isEnabled = isEdit
        binding.etFullname.isEnabled = isEdit
        binding.etPhoneNumber.isEnabled = isEdit
        binding.etAddress.isEnabled = isEdit
        binding.etDistrict.isEnabled = isEdit
        binding.etCity.isEnabled = isEdit
        binding.etProvince.isEnabled = isEdit

        binding.btnEdit.visibility = if (isEdit) View.GONE else View.VISIBLE
        binding.btnSave.visibility = if (isEdit) View.VISIBLE else View.GONE
        listOf(
            binding.tvAccount,
            binding.tilUsername,
            binding.tilEmail
        ).forEach { it.visibility = if (isEdit) View.VISIBLE else View.GONE }
    }


    private fun editProfile() {
        binding.btnEdit.setOnClickListener {
            setupEdtEnabled(true)
            setupProvinceDropdown()
        }

        binding.btnSave.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()
            val fullname = binding.etFullname.text.toString()
            val phoneNumber = binding.etPhoneNumber.text.toString()
            val fullAddress = binding.etAddress.text.toString()
            // Validation
            if (username.isEmpty()) {
                binding.etUsername.error = "Username cannot be empty"
                return@setOnClickListener
            }
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.error = "Invalid email address"
                return@setOnClickListener
            }
            if (fullname.isEmpty()) {
                binding.etFullname.error = "Full name cannot be empty"
                return@setOnClickListener
            }
            if (phoneNumber.isEmpty() || !phoneNumber.matches(Regex("^\\d{10,13}\$"))) {
                binding.etPhoneNumber.error = "Invalid phone number"
                return@setOnClickListener
            }
            if (fullAddress.isEmpty()) {
                binding.etAddress.error = "Address cannot be empty"
                return@setOnClickListener
            }

            if (selectedProvinceId == null) {
                binding.etProvince.error = "Please select a province"
                return@setOnClickListener
            } else {
                binding.etProvince.error = null
            }
            if (selectedRegencyId == null) {
                binding.etCity.error = "Please select a city"
                return@setOnClickListener
            } else {
                binding.etCity.error = null
            }
            if (selectedDistrictId == null) {
                binding.etDistrict.error = "Please select a district"
                return@setOnClickListener
            } else {
                binding.etDistrict.error = null
            }
            save(
                username, email,
                fullname, phoneNumber,
                fullAddress,
            )
        }
    }

    private fun save(
        username: String, email: String,
        fullname: String, phoneNumber: String,
        fullAddress: String,
    ) {
        viewModel.updateProfile(
            username, email,
            fullname, phoneNumber,
            selectedProvinceId ?: 0, selectedRegencyId ?: 0, selectedDistrictId ?: 0, fullAddress,
        ).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(binding.progressIndicator, true)
                }

                is Result.Success -> {
                    showLoading(binding.progressIndicator, false)
                    requireContext().showToast("Profile updated successfully")
                    listOf(
                        binding.tvAccount,
                        binding.tilUsername,
                        binding.tilEmail
                    ).forEach { it.visibility = View.GONE }
                    setupEdtEnabled(false)
                }

                is Result.Error -> {
                    showLoading(binding.progressIndicator, false)
                    requireContext().showToast("Error: ${result.message}")
                    val message = result.message.lowercase()
                    when {
                        "the username has already been taken" in message -> {
                            binding.tilUsername.error = "username sudah terdaftar"
                        }

                        "the email has already been taken" in message -> {
                            binding.tilEmail.error = "email sudah terdaftar"
                        }

                        else -> {
                            AlertDialog.Builder(requireContext()).apply {
                                setTitle("Oops!")
                                setMessage(message)
                                setPositiveButton("OK") { _, _ -> }
                                create()
                                show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun pickImageFromGallery() {
        binding.btnReplacePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_PICK_IMAGE)
        }
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val file = File(requireContext().cacheDir, "profile.jpg").apply {
                    outputStream().use { out ->
                        requireContext().contentResolver.openInputStream(uri)?.copyTo(out)
                    }
                }
                selectedImageFile = file

                Glide.with(requireContext())
                    .load(uri)
                    .into(binding.ivProfile)

                uploadPhoto()
            }
        }


    private fun uploadPhoto() {
        selectedImageFile?.let { file ->
            viewModel.updatePhotoProfile(file).observe(this) { result ->
                when (result) {
                    is Result.Success -> {
                        requireContext().showToast("Foto profil berhasil diperbarui!")
                        Log.d("UploadPhoto", "Berhasil: ${result.data}")
                    }

                    is Result.Error -> {
                        requireContext().showToast("Gagal mengupload foto: ${result.message}")
                        Log.e("UploadPhoto", "Error: ${result.message}")
                    }

                    is Result.Loading -> {
                        requireContext().showToast("Mengunggah foto profil...")
                    }
                }
            }
        } ?: requireContext().showToast("Pilih gambar terlebih dahulu")
    }

    private fun setupProvinceDropdown() {
        profileViewModel.getProvinces().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val provinceNames = result.data.map { it.name }
                    provinceAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        provinceNames
                    )
                    binding.etProvince.setAdapter(provinceAdapter)

                    binding.etProvince.setOnItemClickListener { _, _, position, _ ->
                        selectedProvinceId = result.data[position].id
                        setupRegencyDropdown()
                    }
                }

                is Result.Error -> {}
                is Result.Loading -> {}
            }
        }
    }

    private fun setupRegencyDropdown() {
        binding.etCity.setOnClickListener {
            if (selectedProvinceId == null) {
                binding.tilProvince.error = "Harap pilih provinsi terlebih dahulu"
            } else {
                binding.tilProvince.error = null
            }
        }

        selectedProvinceId?.let { provinceId ->
            profileViewModel.getRegencies(provinceId).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Success -> {
                        val regencyNames = result.data.map { it.name }
                        regencyAdapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            regencyNames
                        )
                        binding.etCity.setAdapter(regencyAdapter)

                        binding.etCity.setOnItemClickListener { _, _, position, _ ->
                            selectedRegencyId = result.data[position].id
                            binding.tilCity.error = null
                            setupDistrictDropdown()
                        }
                    }

                    is Result.Error -> {}
                    is Result.Loading -> {}
                }
            }
        }
    }

    private fun setupDistrictDropdown() {
        binding.etDistrict.setOnClickListener {
            if (selectedRegencyId == null) {
                binding.tilCity.error = "Pilih kabupaten/kota terlebih dahulu"
            } else {
                binding.tilCity.error = null
            }
        }

        selectedRegencyId?.let { regencyId ->
            profileViewModel.getDistricts(regencyId).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Success -> {
                        val districtNames = result.data.map { it.name }
                        districtAdapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            districtNames
                        )
                        binding.etDistrict.setAdapter(districtAdapter)

                        binding.etDistrict.setOnItemClickListener { _, _, position, _ ->
                            selectedDistrictId = result.data[position].id
                        }
                    }

                    is Result.Error -> {}
                    is Result.Loading -> {}
                }
            }
        }
    }
}