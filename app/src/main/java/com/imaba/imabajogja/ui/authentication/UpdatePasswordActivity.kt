package com.imaba.imabajogja.ui.authentication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.data.utils.showToast
import com.imaba.imabajogja.databinding.ActivityUpdatePasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdatePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdatePasswordBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        updatePassword()
        playAnimation()
        binding.btnForgotPassword.setOnClickListener{
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun updatePassword() {
        binding.btnUpdate.setOnClickListener {
            val currentPassword = binding.edtPwCurrent.text.toString().trim()
            val newPassword = binding.edtPassword.text.toString().trim()
            val passwordConfirmation = binding.edtPasswordConfirm.text.toString().trim()

            var isValid = true

            // Reset Error Messages
            binding.tilPwCurrent.error = null
            binding.tilPassword.error = null
            binding.tilPasswordConfirm.error = null

            // 1️⃣ Validasi Password Saat Ini (Harus diisi, dan validasi dari backend)
            if (currentPassword.isEmpty()) {
                binding.tilPwCurrent.error = "Password saat ini tidak boleh kosong"
                isValid = false
            }

            // 2️⃣ Validasi Password Baru Minimal 8 Karakter
            if (newPassword.length < 8) {
                binding.tilPassword.error = "Password baru harus minimal 8 karakter"
                isValid = false
            }

            // 3️⃣ Validasi Password Baru dan Konfirmasi Harus Sama
            if (newPassword != passwordConfirmation) {
                binding.tilPasswordConfirm.error = "Password konfirmasi tidak cocok"
                isValid = false
            }

            // Jika ada error, hentikan proses
            if (!isValid) return@setOnClickListener

            // 🔹 Panggil API untuk update password hanya jika semua validasi lolos
            viewModel.updatePassword(currentPassword, newPassword, passwordConfirmation).observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.progressIndicator.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressIndicator.visibility = View.GONE
                        showToast("Password berhasil diperbarui")
                        finish() // Menutup Activity setelah sukses
                    }
                    is Result.Error -> {
                        binding.progressIndicator.visibility = View.GONE

                        if (result.message.contains("password saat ini salah", true)) {
                            binding.tilPwCurrent.error = "Password saat ini salah"
                        } else {
                            showToast(result.message)
                        }
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.logo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1f).setDuration(150)
        val message =
            ObjectAnimator.ofFloat(binding.tvLoginDesc, View.ALPHA, 1f).setDuration(150)
        val pwCurrentlEditTextLayout =
            ObjectAnimator.ofFloat(binding.tilPwCurrent, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.tilPassword, View.ALPHA, 1f).setDuration(100)
         val passwordConfirmEditTextLayout =
            ObjectAnimator.ofFloat(binding.tilPasswordConfirm, View.ALPHA, 1f).setDuration(100)

        val update = ObjectAnimator.ofFloat(binding.btnUpdate, View.ALPHA, 1f).setDuration(200)
        val forgotPassword = ObjectAnimator.ofFloat(binding.btnForgotPassword, View.ALPHA, 1f).setDuration(200)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                pwCurrentlEditTextLayout,
                passwordEditTextLayout,
                passwordConfirmEditTextLayout,
                update,
                forgotPassword,
            )
            startDelay = 200
        }.start()
    }

}