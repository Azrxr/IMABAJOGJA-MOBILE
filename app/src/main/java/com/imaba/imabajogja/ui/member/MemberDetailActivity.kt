package com.imaba.imabajogja.ui.member

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.response.DataItemMember
import com.imaba.imabajogja.databinding.ActivityMemberDetailBinding
import com.imaba.imabajogja.ui.admin.member.AdmMemberDetailActivity.Companion.EXTRA_MEMBER

class MemberDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMemberDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMemberDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setDetails()
    }

    private fun setDetails() {
        val member = intent.getParcelableExtra<DataItemMember>(EXTRA_MEMBER)
        member?.let { profile ->
            binding.tvName.text = profile.fullname ?: "belum ada"
            binding.tvNoMember.text = profile.noMember ?: "belum ada"
            binding.tvPhoneNumber.text = profile.phoneNumber ?: "belum ada"
            binding.tvPlaceDate.text = listOfNotNull(
                profile.tempat?.takeIf { it.isNotBlank() },
                profile.tanggalLahir?.takeIf { it.isNotBlank() }
            ).takeIf { it.isNotEmpty() }?.joinToString(", ") ?: "belum ada"

            binding.memberType.text = profile.memberType ?: "belum ada"
            binding.tvAddress.text = listOfNotNull(
                profile.fullAddress?.takeIf { it.isNotBlank() },
                profile.district?.takeIf { it.isNotBlank() },
                profile.regency?.takeIf { it.isNotBlank() },
                profile.province?.takeIf { it.isNotBlank() }
            ).takeIf { it.isNotEmpty() }?.joinToString(", ") ?: "belum ada"
            binding.tvAngkatan.text = profile.angkatan ?: "belum ada"

            binding.tvUniversityCurrent.text = profile.studyMembers?.firstOrNull()?.university ?: "belum ada"
            binding.tvFacultyCurrent.text = profile.studyMembers?.firstOrNull()?.faculty ?: "belum ada"
            binding.tvProgramStudy.text = profile.studyMembers?.firstOrNull()?.programStudy ?: "belum ada"

            Glide.with(this)
                .load(profile.profileImgUrl)
                .placeholder(R.drawable.ic_user) // 🔥 Set placeholder jika gambar kosong
                .error(R.drawable.ic_image_broken) // 🔥 Jika gagal load, tampilkan default
                .into(binding.ivProfile)
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object{
        const val EXTRA_MEMBER = "extra_member"
    }
}