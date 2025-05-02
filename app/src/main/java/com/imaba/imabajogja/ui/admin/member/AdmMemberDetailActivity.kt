package com.imaba.imabajogja.ui.admin.member

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.imaba.imabajogja.data.response.DataItemMember
import com.imaba.imabajogja.databinding.ActivityAdmMemberDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdmMemberDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdmMemberDetailBinding

    companion object {
        const val EXTRA_MEMBER = "extra_member"
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

        val member = intent.getParcelableExtra<DataItemMember>(EXTRA_MEMBER)
        member?.let {
            binding.tvName.text = it.fullname
            // tampilkan field lainnya
        }
    }
}