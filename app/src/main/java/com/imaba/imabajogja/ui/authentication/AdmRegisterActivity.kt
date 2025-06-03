package com.imaba.imabajogja.ui.authentication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.databinding.ActivityAdmRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdmRegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdmRegisterBinding
    private val viewModel : AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdmRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        playAnimation()
        intentHandler()
    }

    private fun intentHandler() {
        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.btnDaftar.setOnClickListener {
            val username = binding.edtUsername.text.toString()
            val email = binding.edtEmail.text.toString()
            val fullname = binding.edtFullname.text.toString()
            val phoneNumber = binding.edtPhoneNumber.text.toString()
            val password = binding.edtPassword.text.toString()
            val passwordConfirmation = binding.edtPasswordConfirm.text.toString()

            // Clear previous errors
            binding.tilUsername.error = null
            binding.tilEmail.error = null
            binding.tilPassword.error = null
            binding.tilPasswordConfirm.error = null
            binding.tilFullname.error = null
            binding.tilPhoneNumber.error = null

            if (fullname.isEmpty()){
                binding.tilFullname.error = "Nama lengkap tidak boleh kosong"
                binding.tilFullname.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            } else if (fullname.contains(" ")) {
                binding.tilFullname.error = "Nama lengkap tidak boleh mengandung spasi"
                binding.tilFullname.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            }

            if (phoneNumber.isEmpty()){
                binding.tilPhoneNumber.error = "Nomor telepon tidak boleh kosong"
                binding.tilPhoneNumber.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            } else if (!phoneNumber.matches(Regex("^[0-9]+$"))) {
                binding.tilPhoneNumber.error = "Nomor telepon hanya boleh angka"
                binding.tilPhoneNumber.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            } else if (phoneNumber.length < 10 || phoneNumber.length > 15) {
                binding.tilPhoneNumber.error = "Nomor telepon harus antara 10-15 digit"
                binding.tilPhoneNumber.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            }

            if (username.isEmpty()) {
                binding.tilUsername.error = "Username tidak boleh kosong"
                binding.tilUsername.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            } else if (username.contains(" ")) {
                binding.tilUsername.error = "Username tidak boleh mengandung spasi"
                binding.tilUsername.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.tilEmail.error = "Email tidak boleh kosong"
                binding.tilEmail.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilEmail.error = "Email tidak valid"
                binding.tilEmail.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.tilPassword.error = "Password tidak boleh kosong"
                binding.tilPassword.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            }

            if (passwordConfirmation.isEmpty()) {
                binding.tilPasswordConfirm.error = "Konfirmasi password tidak boleh kosong"
                binding.tilPasswordConfirm.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            } else if (password != passwordConfirmation) {
                binding.tilPasswordConfirm.error = "Password dan konfirmasi password harus sama"
                binding.tilPasswordConfirm.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            }
            registerAdm(username, fullname, phoneNumber, email, password, passwordConfirmation)

        }
    }
    private fun registerAdm(
        username: String,
        fullname: String,
        phoneNumber: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ) {
        viewModel.registerAdm(username, fullname, phoneNumber, email, password, passwordConfirmation).observe(this) {
            if (it != null) {
                when (it) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        AlertDialog.Builder(this).apply {
                            setTitle("Yeah!")
                            setMessage(it.data.message)
                            setPositiveButton("Lanjut") { _, _ ->
                                startActivity(Intent(this@AdmRegisterActivity, LoginActivity::class.java))
                                finish()
                            }
                            create()
                            show()
                        }
                    }

                    is Result.Error -> {
                        showLoading(false)
                        val message = it.message.lowercase()
                        when {
                            "the username has already been taken" in message -> {
                                binding.tilUsername.error = "username sudah terdaftar"
                            }

                            "the email has already been taken" in message -> {
                                binding.tilEmail.error = "email sudah terdaftar"
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
                }

            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.logo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(100)
        val desc =
            ObjectAnimator.ofFloat(binding.tvDesc, View.ALPHA, 1f).setDuration(100)
        val usernameEditTextLayout =
            ObjectAnimator.ofFloat(binding.tilUsername, View.ALPHA, 1f).setDuration(100)
        val fullnameEditTextLayout =
            ObjectAnimator.ofFloat(binding.tilFullname, View.ALPHA, 1f).setDuration(100)
        val phoneNumberEditTextLayout =
            ObjectAnimator.ofFloat(binding.tilPhoneNumber, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.tilEmail, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.tilPassword, View.ALPHA, 1f).setDuration(100)
        val passwordConfirmEditTextLayout =
            ObjectAnimator.ofFloat(binding.tilPasswordConfirm, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.btnDaftar, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(100)
        val regAnggota = ObjectAnimator.ofFloat(binding.btnRegisterAnggota, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                title,
                desc,
                usernameEditTextLayout,
                fullnameEditTextLayout,
                phoneNumberEditTextLayout,
                emailEditTextLayout,
                passwordEditTextLayout,
                passwordConfirmEditTextLayout,
                signup,
                login,
                regAnggota
            )
            startDelay = 100
        }.start()
    }
}