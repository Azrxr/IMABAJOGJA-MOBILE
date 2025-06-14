package com.imaba.imabajogja.ui.admin.member

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.response.DataItemMember
import com.imaba.imabajogja.data.utils.Dropdown
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.data.utils.reduceFileImage
import com.imaba.imabajogja.data.utils.showLoading
import com.imaba.imabajogja.data.utils.showToast
import com.imaba.imabajogja.data.utils.uriToFile
import com.imaba.imabajogja.databinding.ActivityAdmMemberDetailBinding
import com.imaba.imabajogja.ui.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.Calendar

@AndroidEntryPoint
class AdmMemberDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdmMemberDetailBinding
    private val viewModel: AdmMemberViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private var selectedImageFile: File? = null

    private lateinit var provinceAdapter: ArrayAdapter<String>
    private lateinit var regencyAdapter: ArrayAdapter<String>
    private lateinit var districtAdapter: ArrayAdapter<String>

    private var selectedProvinceId: Int? = null
    private var selectedRegencyId: Int? = null
    private var selectedDistrictId: Int? = null

    companion object {
        const val EXTRA_MEMBER = "extra_member"
        private const val REQUEST_PICK_IMAGE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdmMemberDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnEdit.setOnClickListener {
            setEditMode(true)
        }
        binding.btnCancel.setOnClickListener {
            showMemberDetails()
        }
        updateProfile()
        showMemberDetails()
        setupDropdowns()
        pickImageFromGallery()
        setupDatePicker()
    }

    private fun setupDropdowns() {
        setupProvinceDropdown()
        setupGenderDropDown()
        setupReligionDropDown()
        setupMemberTypeDropDown()
        setupYearDropDown()
    }

    private fun setEditMode(isEditMode: Boolean) {
        // Atur semua EditText agar bisa / tidak bisa diedit
        binding.etFullname.isEnabled = isEditMode
        binding.etProvince.isEnabled = isEditMode
        binding.etCity.isEnabled = isEditMode
        binding.etDistrict.isEnabled = isEditMode
        binding.etPhoneNumber.isEnabled = isEditMode
        binding.etFullAddress.isEnabled = isEditMode
        binding.etPostalCode.isEnabled = isEditMode
        binding.etReligion.isEnabled = isEditMode
        binding.etNisn.isEnabled = isEditMode
        binding.etBirthPlace.isEnabled = isEditMode
        binding.etBirthDate.isEnabled = isEditMode
        binding.etGender.isEnabled = isEditMode
        binding.etSchoolOrigin.isEnabled = isEditMode
        binding.etGraduationYear.isEnabled = isEditMode
        binding.etGeneration.isEnabled = isEditMode
        binding.etMemberType.isEnabled = isEditMode
        binding.etNoMember.isEnabled = isEditMode

        // Tombol upload hanya aktif saat mode edit
        binding.btnUpload.isEnabled = isEditMode

        // Atur visibilitas tombol
        binding.btnEdit.visibility =
            if (isEditMode) android.view.View.GONE else android.view.View.VISIBLE
        binding.btnSave.visibility =
            if (isEditMode) android.view.View.VISIBLE else android.view.View.GONE
        binding.btnDelete.visibility =
            if (isEditMode) android.view.View.VISIBLE else android.view.View.GONE
        binding.btnCancel.visibility =
            if (isEditMode) android.view.View.VISIBLE else android.view.View.GONE
        binding.btnUpload.visibility =
            if (isEditMode) android.view.View.VISIBLE else android.view.View.GONE

    }


    private fun showMemberDetails() {
        setEditMode(false)
        val member = intent.getParcelableExtra<DataItemMember>(EXTRA_MEMBER)
        member?.let { profile ->
            binding.etFullname.setText(profile.fullname)
            binding.etNoMember.setText(profile.noMember)

            binding.etProvince.setText(profile.province)
            selectedProvinceId = profile.provinceId
            binding.etCity.setText(profile.regency)
            selectedRegencyId = profile.regencyId
            binding.etDistrict.setText(profile.district)
            selectedDistrictId = profile.districtId

            binding.etFullname.setText(profile.fullname)
            binding.etPhoneNumber.setText(
                profile.phoneNumber?.toString()
            )
            binding.etFullAddress.setText(
                profile.fullAddress
            )
            binding.etPostalCode.setText(
                profile.kodePos?.toString()
            )
            setupReligionDropDown(profile.agama)
            binding.etNisn.setText(profile.nisn?.toString())
            binding.etBirthPlace.setText(profile.tempat)
            binding.etBirthDate.setText(
                profile.tanggalLahir?.toString()
            )
            setupGenderDropDown(profile.gender)
            binding.etSchoolOrigin.setText(
                profile.schollOrigin?.toString()
            )
            binding.etGraduationYear.setText(
                profile.tahunLulus?.toString()
            )

            binding.etGeneration.setText(
                profile.angkatan?.toString()
            )
            setupYearDropDown(profile.angkatan)
            binding.etMemberType.setText(profile.memberType)
            setupMemberTypeDropDown(profile.memberType)

            Glide.with(this)
                .load(profile.profileImgUrl)
                .placeholder(R.drawable.ic_user) // 🔥 Set placeholder jika gambar kosong
                .error(R.drawable.ic_image_broken) // 🔥 Jika gagal load, tampilkan default
                .into(binding.ivProfile)

            binding.btnDelete.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Konfirmasi")
                    .setMessage("Apakah kamu yakin ingin menghapus anggota '${member.fullname}'?")
                    .setPositiveButton("Ya") { _, _ ->
                        profile.id?.let { memberId ->
                            viewModel.deleteMember(memberId).observe(this) { result ->
                                when (result) {
                                    is Result.Success -> {
                                        this.showToast("Berhasil menghapus anggota")
                                    }

                                    is Result.Error -> {
                                        this.showToast("Gagal menghapus anggota: ${result.message}")
                                        Log.d("member", "error MemberFragment: ${result.message}")
                                    }

                                    is Result.Loading -> Unit
                                }
                            }
                        }
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            } ?: this.showToast("ID anggota tidak ditemukan")
        }
    }

    private fun pickImageFromGallery() {
        binding.btnUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_PICK_IMAGE)
            setEditMode(false)
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

    private fun setupGenderDropDown(selectedGender: String? = null) {
        Dropdown.setSimpleDropdown(
            this,
            binding.etGender,
            listOf("Laki-laki", "Perempuan"),
            selectedGender
        )
    }

    private fun setupMemberTypeDropDown(selectedMemberType: String? = null) {
        Dropdown.setSimpleDropdown(
            this,
            binding.etMemberType,
            listOf("camaba", "pengurus", "anggota", "demissioner", "istimewa"),
            selectedMemberType
        )
        binding.etMemberType.error = null
    }

    private fun setupReligionDropDown(selectedReligion: String? = null) {
        Dropdown.setSimpleDropdown(
            this,
            binding.etReligion,
            listOf("Islam", "Kristen", "Katolik", "Hindu", "Budha", "Konghucu", "Lainnya"),
            selectedReligion
        )
        binding.etReligion.error = null
    }

    private fun setupYearDropDown(selectedYear: String? = null) {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val startYear = 2010
        val endYear = currentYear + 2 // bisa ditambah lebih banyak tahun ke depan

        val years = (startYear..endYear).map { it.toString() }.reversed()

        Dropdown.setSimpleDropdown(
            this,
            binding.etGeneration,
            years,
            selectedYear
        )
        binding.etGeneration.error = null
        binding.etGraduationYear.error = null
    }

    private fun setupDatePicker(): String {
        var selectedDate = ""
        binding.etBirthDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate =
                    String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                binding.etBirthDate.setText(selectedDate) // 🔥 Menampilkan tanggal yang dipilih di EditText
            }, year, month, day)

            datePicker.show()
        }
        return selectedDate
        binding.tilBirthDate.error = null
    }

    private fun uploadPhoto() {
        val member = intent.getParcelableExtra<DataItemMember>(EXTRA_MEMBER)
        val memberId = member?.id ?: 0

        selectedImageFile?.let { file ->
            viewModel.updateMemberPhotoProfile(memberId, file).observe(this) { result ->
                when (result) {
                    is Result.Success -> {
                        showLoading(binding.progressIndicator, false)
                        showToast("Foto profil berhasil diperbarui!")
                        Log.d("UploadPhoto", "Berhasil: ${result.data}")

                    }

                    is Result.Error -> {
                        showToast("Gagal mengupload foto: ${result.message}")
                        Log.e("UploadPhoto", "Error: ${result.message}")
                        showLoading(binding.progressIndicator, false)
                    }

                    is Result.Loading -> {
                        showToast("Mengunggah foto profil...")
                        showLoading(binding.progressIndicator, true)
                    }
                }
            }
        } ?: showToast("Pilih gambar terlebih dahulu")
    }

    private fun updateProfile() {
        val member = intent.getParcelableExtra<DataItemMember>(EXTRA_MEMBER)
        val memberId = member?.id ?: 0
        binding.btnSave.setOnClickListener {

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
            val tahunLulus = binding.etGraduationYear.text.toString().toIntOrNull()
            val angkatan = binding.etGeneration.text.toString()
            val memberType = binding.etMemberType.text.toString()
            val noMember = binding.etNoMember.text.toString()
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)


            if (fullname.isEmpty()) {
                binding.etFullname.error = "Nama lengkap tidak boleh kosong"
                return@setOnClickListener
            }

            if (noMember.isEmpty()) {
                binding.etNoMember.error = "Nomor anggota tidak boleh kosong"
                return@setOnClickListener
            }
            //TODO: cek duplikat no member

            if (phoneNumber.isEmpty() || !phoneNumber.matches(Regex("^\\d{10,13}\$"))) {
                binding.etPhoneNumber.error = "Nomor telepon tidak valid"
                return@setOnClickListener
            }
            if (fullAddress.isEmpty()) {
                binding.etFullAddress.error = "Alamat lengkap tidak boleh kosong"
                return@setOnClickListener
            }
            if (kodePos.isEmpty() || !kodePos.matches(Regex("^\\d{5}\$"))) {
                binding.etPostalCode.error = "Kode pos tidak valid"
                return@setOnClickListener
            }
            if (agama.isEmpty()) {
                binding.etReligion.error = "Agama tidak boleh kosong"
                return@setOnClickListener
            } else { binding.etReligion.error = null }
            if (nisn.isEmpty() || !nisn.matches(Regex("^\\d{10}\$"))) {
                binding.etNisn.error = "NISN 10 digit"
                return@setOnClickListener
            }
            if (tempat.isEmpty()) {
                binding.etBirthPlace.error = "Tempat lahir tidak boleh kosong"
                return@setOnClickListener
            }
            if (tanggalLahir.isEmpty() || tanggalLahir > currentYear.toString()) {
                binding.tilBirthDate.error = "Tanggal lahir tidak valid"
                return@setOnClickListener
            }else { binding.tilBirthDate.error = null }
            if (gender.isEmpty()) {
                binding.etGender.error = "Jenis kelamin tidak boleh kosong"
                return@setOnClickListener
            } else { binding.etGender.error = null }
            if (schollOrigin.isEmpty()) {
                binding.etSchoolOrigin.error = "Asal sekolah tidak boleh kosong"
                return@setOnClickListener
            }
            if (tahunLulus == null || tahunLulus > currentYear) {
                binding.etGraduationYear.error =
                    "Tahun lulus tidak valid atau melebihi tahun saat ini"
                return@setOnClickListener
            }
            if (angkatan.isEmpty() || angkatan > currentYear.toString()) {
                binding.etGeneration.error = "Angkatan tidak valid"
                return@setOnClickListener
            } else {
                binding.etGeneration.error = null
            }
            if (memberType.isEmpty()) {
                binding.etMemberType.error = "Tipe anggota tidak boleh kosong"
                return@setOnClickListener
            } else { binding.etMemberType.error = null }
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
            saveMember(
                memberId,
                fullname,
                phoneNumber,
                fullAddress,
                kodePos,
                agama,
                nisn,
                tempat,
                tanggalLahir,
                gender,
                schollOrigin,
                tahunLulus,
                angkatan,
                memberType,
                noMember
            )
        }
    }

    private fun saveMember(
        memberId: Int,
        fullname: String, phoneNumber: String, fullAddress: String, kodePos: String,
        agama: String, nisn: String, tempat: String, tanggalLahir: String, gender: String,
        schollOrigin: String, tahunLulus: Int, angkatan: String, memberType: String, noMember: String? = null
    ) {
        viewModel.updateMemberAdm(
            memberId,
            fullname,
            phoneNumber,
            selectedProvinceId ?: 0,
            selectedRegencyId ?: 0,
            selectedDistrictId ?: 0,
            fullAddress,
            kodePos,
            agama,
            nisn,
            tempat,
            tanggalLahir,
            gender,
            schollOrigin,
            tahunLulus,
            angkatan,
            memberType, noMember
        ).observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    showLoading(binding.progressIndicator, false)
                    Log.d("UpdateProfile", "Profil berhasil diperbarui!")
                    showToast("Profil berhasil diperbarui!")
                    setEditMode(false)
                    val intent = Intent()
                    setResult(RESULT_OK, intent)
                }

                is Result.Error -> {
                    showLoading(binding.progressIndicator, false)
                    Log.e("UpdateProfile", "Gagal memperbarui profil: ${result.message}")
//                    showToast("Gagal memperbarui profil: ${result.message}")
                    val message = result.message.lowercase()
                    when {
                        "the no member has already been taken." in message -> {
                            binding.etNoMember.error = "no member sudah digunakan"
                        }

                        else -> {
                            AlertDialog.Builder(this).apply {
                                setTitle("Oops!")
                                setMessage(message)
                                setPositiveButton("OK") { _, _ -> }
                                create()
                                show()
                            }
                        }
                    }
                }

                is Result.Loading -> {
                    showLoading(binding.progressIndicator, true)
                    Log.d("UpdateProfile", "Memproses pembaruan profil...")
                    showToast("Memproses pembaruan profil...")
                }
            }
        }
    }

    private fun setupProvinceDropdown() {
        profileViewModel.getProvinces().observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    val provinceNames = result.data.map { it.name }
                    provinceAdapter =
                        ArrayAdapter(
                            this,
                            android.R.layout.simple_dropdown_item_1line,
                            provinceNames
                        )
                    binding.etProvince.setAdapter(provinceAdapter)

                    binding.etProvince.setOnItemClickListener { _, _, position, _ ->
                        selectedProvinceId = result.data[position].id
                        setupRegencyDropdown()
                    }
                }

                is Result.Error -> binding.progressIndicator.visibility = View.GONE
                is Result.Loading -> binding.progressIndicator.visibility = View.VISIBLE
            }
        }
        binding.etProvince.error = null
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
            profileViewModel.getRegencies(provinceId).observe(this) { result ->
                when (result) {
                    is Result.Success -> {
                        binding.progressIndicator.visibility = View.GONE
                        val regencyNames = result.data.map { it.name }
                        regencyAdapter = ArrayAdapter(
                            this,
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

                    is Result.Error -> binding.progressIndicator.visibility = View.GONE
                    is Result.Loading -> binding.progressIndicator.visibility = View.VISIBLE
                }
            }
        }
        binding.etCity.error = null
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
            profileViewModel.getDistricts(regencyId).observe(this) { result ->
                when (result) {
                    is Result.Success -> {
                        binding.progressIndicator.visibility = View.GONE
                        val districtNames = result.data.map { it.name }
                        districtAdapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_dropdown_item_1line,
                            districtNames
                        )
                        binding.etDistrict.setAdapter(districtAdapter)

                        binding.etDistrict.setOnItemClickListener { _, _, position, _ ->
                            selectedDistrictId = result.data[position].id
                        }
                    }

                    is Result.Error -> binding.progressIndicator.visibility = View.GONE
                    is Result.Loading -> binding.progressIndicator.visibility = View.VISIBLE
                }
            }
        }
        binding.etDistrict.error = null
    }
}