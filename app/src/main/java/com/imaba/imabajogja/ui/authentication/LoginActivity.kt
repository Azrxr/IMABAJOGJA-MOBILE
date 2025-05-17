package com.imaba.imabajogja.ui.authentication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.imaba.imabajogja.ui.MainActivity
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.model.UserModel
import com.imaba.imabajogja.data.response.LoginResponse
import com.imaba.imabajogja.databinding.ActivityLoginBinding
import com.imaba.imabajogja.data.utils.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        intentHandler()
        playAnimation()
    }

    private fun intentHandler() {
        binding.btnLogin.setOnClickListener {
            val credential = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString()
            when {
                credential.isEmpty() -> {
                    binding.tilEmail.error = "Email atau username tidak boleh kosong"
                }

                password.isEmpty() -> {
                    binding.tilPassword.error = "Password tidak boleh kosong"
                }

                password.length < 8 -> {
                    binding.tilPassword.error = "Password minimal 8 karakter"
                }

                else -> {
                    Log.d("login", "email: $credential")
                    Log.d("login", "password: $password")
                    login(credential, password)
                }
            }

        }
        setupTextWatchers()
        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        binding.btnDaftar.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }
        binding.btnForgotPassword.setOnClickListener {
            val forgotPasswordIntent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(forgotPasswordIntent)
        }
    }

    private fun goToHome() {
        AlertDialog.Builder(this).apply {
            setTitle("Yeah!")
            setMessage("Anda berhasil login. Sudah tidak sabar ya?")
            setPositiveButton("Lanjut") { _, _ ->
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun login(credential: String, password: String) {
        viewModel.login(credential, password).observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> { showLoading(false)
                    goToHome()
                    val loginResponse = result.data
                    saveUserData(loginResponse)

                }

                is Result.Error -> { showLoading(false)
                    val message = result.message.lowercase()
                    when {
                        "incorrect password" in message -> {
                            binding.tilPassword.error = "password salah"
                            binding.tilEmail.error = null
                        }

                        "email not found" in message -> {
                            binding.tilEmail.error = "email atau username tidak terdaftar"
                            binding.tilPassword.error = null
                        }

                        else -> {
                            // Untuk error lain
                            binding.tilPassword.error = null
                            binding.tilEmail.error = null
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                            Log.d("login", "Error: ${message}")
                        }
                    }
                }
            }
        }

    }

    private fun setupTextWatchers() {
        binding.edtEmail.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilEmail.error = null
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
        binding.edtPassword.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilPassword.error = null
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }


    private fun saveUserData(dataUser: LoginResponse) {
        val credential = binding.edtEmail.text.toString()
//        val role = dataUser.loginResult.role
        val isLogin = true
        viewModel.saveSession(
            UserModel(
                credential,
                dataUser.loginResult.token,
                dataUser.loginResult.role,
                isLogin
            )
        )

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.tilEmail, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.tilPassword, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(200)
        val forgotPassword =
            ObjectAnimator.ofFloat(binding.btnForgotPassword, View.ALPHA, 1f).setDuration(200)
        val btnDaftar = ObjectAnimator.ofFloat(binding.btnDaftar, View.ALPHA, 1f).setDuration(200)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailEditTextLayout,
                passwordEditTextLayout,
                login,
                forgotPassword,
                btnDaftar
            )
            startDelay = 200
        }.start()
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}