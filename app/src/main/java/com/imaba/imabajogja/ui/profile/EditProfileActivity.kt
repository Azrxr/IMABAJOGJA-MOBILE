package com.imaba.imabajogja.ui.profile

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.api.ApiService
import com.imaba.imabajogja.data.response.ProfileUser
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.data.utils.createCustomTempFile
import com.imaba.imabajogja.data.utils.reduceFileImage
import com.imaba.imabajogja.data.utils.setTextOrPlaceholder
import com.imaba.imabajogja.data.utils.showToast
import com.imaba.imabajogja.data.utils.uriToFile
import com.imaba.imabajogja.databinding.ActivityEditProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Calendar

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private var profileData: ProfileUser? = null
    private var selectedImageFile: File? = null

    private lateinit var provinceAdapter: ArrayAdapter<String>
    private lateinit var regencyAdapter: ArrayAdapter<String>
    private lateinit var districtAdapter: ArrayAdapter<String>

    private var selectedProvinceId: Int? = null
    private var selectedRegencyId: Int? = null
    private var selectedDistrictId: Int? = null

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
        setupDropdowns()
        setupDatePicker()
        pickImageFromGallery()

    }

    private fun showProfileData(data: ProfileUser) {
        val profile = data

        binding.etUsername.setTextOrPlaceholder(profile.username, "Masukkan username")
        binding.etEmail.setTextOrPlaceholder(profile.email, "Masukkan email")

        binding.etProvince.setTextOrPlaceholder(profile.province, "Provinsi")
        selectedProvinceId = profile.provinceId
        binding.etCity.setTextOrPlaceholder(profile.regency, "kota")
        selectedRegencyId = profile.regencyId
        binding.etDistrict.setTextOrPlaceholder(profile.district, "kecamatan")
        selectedDistrictId = profile.districtId

        binding.etFullname.setTextOrPlaceholder(profile.fullname, "Masukkan nama lengkap")
        binding.etPhoneNumber.setTextOrPlaceholder(profile.phoneNumber?.toString(), "Masukkan nomor telepon")
        binding.etFullAddress.setTextOrPlaceholder(profile.fullAddress, "Masukkan alamat lengkap")
        binding.etPostalCode.setTextOrPlaceholder(profile.kodePos?.toString(), "Masukkan kode pos")
        setupReligionDropDown(profile.agama)
        binding.etNisn.setTextOrPlaceholder(profile.nisn?.toString(), "Masukkan NISN")
        binding.etBirthPlace.setTextOrPlaceholder(profile.tempat, "Masukkan tempat lahir")
        binding.etBirthDate.setTextOrPlaceholder(profile.tanggalLahir?.toString(), "Masukkan tanggal lahir")
        setupGenderDropDown(profile.gender)
        binding.etSchoolOrigin.setTextOrPlaceholder(profile.schollOrigin?.toString(), "Masukkan asal sekolah")
        binding.etGraduationYear.setTextOrPlaceholder(profile.tahunLulus?.toString(), "Masukkan tahun lulus")

        Glide.with(this)
            .load(profile.profileImgUrl)
            .placeholder(R.drawable.ic_user) // 🔥 Set placeholder jika gambar kosong
            .error(R.drawable.ic_image_broken) // 🔥 Jika gagal load, tampilkan default
            .into(binding.ivProfile)
    }

    private fun pickImageFromGallery() {
        binding.btnUpload.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_PICK_IMAGE)
        }

    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                // 🔥 Tampilkan gambar yang dipilih
                binding.ivProfile.setImageURI(selectedImageUri)

                // 🔥 Konversi URI ke File & Kompres sebelum upload
                selectedImageFile = uriToFile(selectedImageUri, this).reduceFileImage()

                // 🔥 Langsung kirim ke server setelah dipilih
                uploadPhoto()
            } else {
                showToast("Gagal memilih gambar")
            }
        }
    }

    private fun setupGenderDropDown(selectedGender : String? = null) {
        val genderOptions = listOf("Laki-laki", "Perempuan")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genderOptions)
        binding.etGender.setAdapter(adapter)

        selectedGender?.let {
            if (genderOptions.contains(it)){
                binding.etGender.setText(it, false)
            }
        }
    }

    private fun setupReligionDropDown(selectedReligion : String? = null) {
        val religionOptions = listOf("Islam", "Kristen", "Katolik", "Hindu", "Budha", "Konghucu", "Lainnya")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, religionOptions)
        binding.etReligion.setAdapter(adapter)

        selectedReligion?.let { apiReligion ->
            val matchedReligion = religionOptions.find { it.equals(apiReligion, ignoreCase = true) }
            matchedReligion?.let { binding.etReligion.setText(it, false) }
        }
    }

    private fun setupProvinceDropdown() {
        viewModel.getProvinces().observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val provinceNames = result.data.map { it.name }
                    provinceAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, provinceNames)
                    binding.etProvince.setAdapter(provinceAdapter)

                    binding.etProvince.setOnItemClickListener { _, _, position, _ ->
                        selectedProvinceId = result.data[position].id
                        setupRegencyDropdown()
                    }
                }
                is Result.Error -> showToast("Gagal mengambil provinsi: ${result.message}")
                is Result.Loading -> showToast("Memuat data provinsi...")
            }
        }
    }

    private fun setupRegencyDropdown() {
        selectedProvinceId?.let { provinceId ->
            viewModel.getRegencies(provinceId).observe(this) { result ->
                when (result) {
                    is Result.Success -> {
                        val regencyNames = result.data.map { it.name }
                        regencyAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, regencyNames)
                        binding.etCity.setAdapter(regencyAdapter)

                        binding.etCity.setOnItemClickListener { _, _, position, _ ->
                            selectedRegencyId = result.data[position].id
                            setupDistrictDropdown()
                        }
                    }
                    is Result.Error -> showToast("Gagal mengambil kabupaten/kota: ${result.message}")
                    is Result.Loading -> showToast("Memuat data kabupaten/kota...")
                }
            }
        }
    }

    private fun setupDistrictDropdown() {
        selectedRegencyId?.let { regencyId ->
            viewModel.getDistricts(regencyId).observe(this) { result ->
                when (result) {
                    is Result.Success -> {
                        val districtNames = result.data.map { it.name }
                        districtAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, districtNames)
                        binding.etDistrict.setAdapter(districtAdapter)

                        binding.etDistrict.setOnItemClickListener { _, _, position, _ ->
                            selectedDistrictId = result.data[position].id
                        }
                    }
                    is Result.Error -> showToast("Gagal mengambil kecamatan: ${result.message}")
                    is Result.Loading -> showToast("Memuat data kecamatan...")
                }
            }
        }
    }

    private fun setupDropdowns() {
        setupProvinceDropdown()
        setupRegencyDropdown()
        setupDistrictDropdown()
        setupGenderDropDown()
        setupReligionDropDown()
    }

    private fun setupDatePicker(): String {
        var selectedDate = ""
        binding.etBirthDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                binding.etBirthDate.setText(selectedDate) // 🔥 Menampilkan tanggal yang dipilih di EditText
            }, year, month, day)

            datePicker.show()
        }
        return selectedDate
    }

    private fun uploadPhoto(){
        selectedImageFile?.let { file ->
            viewModel.updatePhotoProfile(file).observe(this) { result ->
                when (result) {
                    is Result.Success -> {
                        showToast("Foto profil berhasil diperbarui!")
                        Log.d("UploadPhoto", "Berhasil: ${result.data}")
                    }

                    is Result.Error -> {
                        showToast("Gagal mengupload foto: ${result.message}")
                        Log.e("UploadPhoto", "Error: ${result.message}")
                    }

                    is Result.Loading -> {
                        showToast("Mengunggah foto profil...")
                    }
                }
            }
        } ?: showToast("Pilih gambar terlebih dahulu")
    }

    private fun updateProfile() {
        binding.btnSave.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()

            val fullname = binding.etFullname.text.toString()
            val phoneNumber = binding.etPhoneNumber.text.toString()

            val fullAddress = binding.etFullAddress.text.toString()
            val kodePos = binding.etPostalCode.text.toString()
            val agama = binding.etReligion.text.toString()
            val nisn = binding.etNisn.text.toString()
            val tempat = binding.etBirthPlace.text.toString()
            val tanggalLahir = binding.etBirthDate.text.toString()
            val gender = binding.etGender.text.toString()
            val schollOrigin = binding.etSchoolOrigin.text.toString()
            val tahunLulus = binding.etGraduationYear.text.toString().toInt()

            viewModel.updateProfile(
                username, email,
                fullname, phoneNumber, selectedProvinceId ?: 0, selectedRegencyId ?: 0, selectedDistrictId ?: 0,
                fullAddress, kodePos, agama, nisn, tempat, tanggalLahir, gender,
                schollOrigin, tahunLulus
            ).observe(this) { result ->
                when (result) {
                    is Result.Success -> {
                        Log.d("UpdateProfile", "Profil berhasil diperbarui!")
                        showToast("Profil berhasil diperbarui!")
                        val intent = Intent()
                        setResult(Activity.RESULT_OK, intent) // Kirim sinyal ke fragment
                        finish()
                    }

                    is Result.Error -> {
                        Log.e("UpdateProfile", "Gagal memperbarui profil: ${result.message}")
                        showToast("Gagal memperbarui profil: ${result.message}")
                    }

                    is Result.Loading -> {
                        Log.d("UpdateProfile", "Memproses pembaruan profil...")
                        showToast("Memproses pembaruan profil...")
                    }
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