package com.imaba.imabajogja.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.imaba.imabajogja.R
import com.imaba.imabajogja.databinding.ActivityAboutBinding
import androidx.core.net.toUri
import com.imaba.imabajogja.data.utils.ReleaseManager

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val versionName = ReleaseManager.getLocalVersionName(this)
        binding.tvVersion.text = getString(R.string.app_version, versionName)


        binding.tvGithub.setOnClickListener {
            val url = "https://github.com/Azrxr"
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
        }
        binding.tvLinkedIn.setOnClickListener {
            val url = "https://www.linkedin.com/in/moh-asrori/"
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
        }
        binding.tvEmail.setOnClickListener {
            val email = "irvanea1@gmail.com"
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:$email".toUri()
            }
            startActivity(intent)
        }
        binding.btnGithubAndroid.setOnClickListener{
        val url = "https://imabayogyakarta.com/"
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
        }
        binding.btnGithubWebService.setOnClickListener {
            val url = "https://github.com/Azrxr/IMABAJOGJA-MOBILE"
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
        }

        binding.textView11.setOnClickListener {
        val url = "https://github.com/Azrxr/IMABAJOGJA-WEB-RESTAPI"
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
        }
    }
}