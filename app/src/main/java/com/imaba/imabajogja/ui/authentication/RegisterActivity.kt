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
import com.imaba.imabajogja.databinding.ActivityRegisterBinding
import com.imaba.imabajogja.ui.MainActivity
import com.imaba.imabajogja.data.utils.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel : AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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
            val password = binding.edtPassword.text.toString()
            val passwordConfirmation = binding.edtPasswordConfirm.text.toString()

            if (username.isEmpty()) {
                binding.tilUsername.error = "Username tidak boleh kosong"
                binding.tilUsername.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            } else {
                binding.tilUsername.error = null
            }

            if (email.isEmpty()) {
                binding.tilEmail.error = "Email tidak boleh kosong"
                binding.tilEmail.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilEmail.error = "Email tidak valid"
                binding.tilEmail.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            } else {
                binding.tilEmail.error = null
            }

            if (password.isEmpty()) {
                binding.tilPassword.error = "Password tidak boleh kosong"
                binding.tilPassword.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            } else {
                binding.tilPassword.error = null
            }

            if (passwordConfirmation.isEmpty()) {
                binding.tilPasswordConfirm.error = "Konfirmasi password tidak boleh kosong"
                binding.tilPasswordConfirm.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            } else if (password != passwordConfirmation) {
                binding.tilPasswordConfirm.error = "Password dan konfirmasi password harus sama"
                binding.tilPasswordConfirm.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                return@setOnClickListener
            } else {
                binding.tilPasswordConfirm.error = null
            }
            register(username, email, password, passwordConfirmation)

        }
    }
    private fun register(
        username: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ) {
        viewModel.register(username, email, password, passwordConfirmation).observe(this) {
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
                                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                                finish()
                            }
                            create()
                            show()
                        }
                    }

                    is Result.Error -> {
                        showLoading(false)
                        AlertDialog.Builder(this).apply {
                            setTitle("Oops!")
                            setMessage(it.message)
                            setPositiveButton("OK") { _, _ -> }
                            create()
                            show()
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
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.tilEmail, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.tilPassword, View.ALPHA, 1f).setDuration(100)
        val passwordConfirmEditTextLayout =
            ObjectAnimator.ofFloat(binding.tilPasswordConfirm, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.btnDaftar, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(100)
        val regAdmin = ObjectAnimator.ofFloat(binding.btnRegisterAdmin, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                title,
                desc,
                usernameEditTextLayout,
                emailEditTextLayout,
                passwordEditTextLayout,
                passwordConfirmEditTextLayout,
                signup,
                login,
                regAdmin
            )
            startDelay = 100
        }.start()
    }
}