package com.imaba.imabajogja.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.imaba.imabajogja.data.response.ProfileUser
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.data.utils.createCustomTempFile
import com.imaba.imabajogja.data.utils.showToast
import com.imaba.imabajogja.databinding.ActivityEditProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private var profileData: ProfileUser? = null
    private var selectedImageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Terima data dari Intent
        profileData = intent.getParcelableExtra(PROFILE_DATA)

        profileData?.let {
            showProfileData(it)
        }
        updateProfile()

    }

    private fun showProfileData(data: ProfileUser) {
        val profile = data

        binding.etUsername.setText(profile.username)
        binding.etEmail.setText(profile.email)

        binding.etFullname.setText(profile.fullname)
        binding.etPhoneNumber.setText(profile.phoneNumber)
        binding.etFullAddress.setText(profile.fullAddress)
        binding.etPostalCode.setText(profile.kodePos)
        binding.etReligion.setText(profile.agama)
        binding.etNisn.setText(profile.nisn)
        binding.etBirthPlace.setText(profile.tempat)
        binding.etBirthDate.setText(profile.tanggalLahir)
        binding.etGender.setText(profile.gender)
        binding.etSchoolOrigin.setText(profile.schollOrigin)
        binding.etGraduationYear.setText(profile.tahunLulus)

        Glide.with(this).load(profile.profileImgUrl).into(binding.ivProfile)
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                binding.ivProfile.setImageURI(selectedImageUri) // 🔥 Tampilkan preview gambar
                selectedImageFile = uriToFile(selectedImageUri, this) // 🔥 Ubah Uri ke File untuk upload
            } else {
                showToast("Gagal memilih gambar")
            }
        }
    }

    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val myFile = createCustomTempFile(context)
        val inputStream = context.contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }
        outputStream.close()
        inputStream.close()
        return myFile
    }


//    private fun setupGenderSpinner() {
//        val genderList = listOf("Laki-laki", "Perempuan")
//        val adapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, genderList)
//        binding.spinnerGender.adapter = adapter
//
//        binding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                selectedGender = genderList[position] // Simpan nilai yang dipilih
//            }
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }
//    }


//    private fun getProvinces() {
//        lifecycleScope.launch {
//            val response = apiService.getProvinces()
//            if (response.isSuccessful) {
//                val provinces = response.body() ?: emptyList()
//                val adapter = ArrayAdapter(this@EditProfileActivity, android.R.layout.simple_spinner_dropdown_item, provinces.map { it.name })
//                binding.spinnerProvince.adapter = adapter
//            }
//        }
//    }
//
//    private fun getRegencies(provinceId: Int) {
//        lifecycleScope.launch {
//            val response = apiService.getRegencies(provinceId)
//            if (response.isSuccessful) {
//                val regencies = response.body() ?: emptyList()
//                val adapter = ArrayAdapter(this@EditProfileActivity, android.R.layout.simple_spinner_dropdown_item, regencies.map { it.name })
//                binding.spinnerRegency.adapter = adapter
//            }
//        }
//    }


    private fun updateProfile() {
        binding.btnSave.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()

//            val currentPassword = binding.edtCurrentPassword.text.toString()
//            val newPassword = binding.edtNewPassword.text.toString()
//            val passwordConfirmation = binding.edtPasswordConfirm.text.toString()

            val fullname = binding.etFullname.text.toString()
            val phoneNumber = binding.etPhoneNumber.text.toString()

            val profileImg = "profile.jpg"
            val provinceId = binding.etProvince.id
            val regencyId = binding.etCity.id
            val districtId = binding.etDistrict.id

            val fullAddress = binding.etFullAddress.text.toString()
            val kodePos = binding.etPostalCode.text.toString()
            val agama = binding.etReligion.text.toString()
            val nisn = binding.etNisn.text.toString()
            val tempat = binding.etBirthPlace.text.toString()
            val tanggalLahir = binding.etBirthDate.text.toString()
            val gender = binding.etGender.text.toString()
            val schollOrigin = binding.etSchoolOrigin.text.toString()
            val tahunLulus = binding.etGraduationYear.text.toString().toInt()

            binding.btnUpload.setOnClickListener {
                pickImageFromGallery()
            }

            viewModel.updateProfile(
                username, email,
//                currentPassword, newPassword, passwordConfirmation,
                fullname, phoneNumber, profileImg, provinceId, regencyId, districtId,
                fullAddress, kodePos, agama, nisn, tempat, tanggalLahir, gender,
                schollOrigin, tahunLulus
            ).observe(this) { result ->
                when (result) {
                    is Result.Success -> showToast("Profil berhasil diperbarui!")
                    is Result.Error -> showToast("Gagal memperbarui profil: ${result.message}")
                    is Result.Loading -> showToast("Memproses pembaruan profil...")
                }
            }
        }

    }

    companion object {
        private const val PROFILE_DATA = "profile_data"
        private const val REQUEST_CAPTURE_IMAGE = 1000
        private const val REQUEST_PICK_IMAGE = 1000

        fun newIntent(context: Context, profile: ProfileUser): Intent {
            return Intent(context, EditProfileActivity::class.java).apply {
                putExtra(PROFILE_DATA, profile)
            }
        }
    }

}