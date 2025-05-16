package com.imaba.imabajogja.data.api

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.imaba.imabajogja.data.model.UserPreference
import com.imaba.imabajogja.data.repository.LoginRepository
import com.imaba.imabajogja.ui.welcome.WelcomeActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenExpiredCallbackImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreference: UserPreference
) : ApiConfig.TokenExpiredCallback {
    override fun onTokenExpired() {
        // Logout user

        CoroutineScope(Dispatchers.IO).launch {
            userPreference.logout()
        }

        // Tampilkan dialog
        val dialog = AlertDialog.Builder(context)
            .setTitle("Sesi Expired")
            .setMessage("Sesi anda telah expired, silakan login ulang.")
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                val intent = Intent(context, WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                context.startActivity(intent)
            }
            .create()
        dialog.show()
    }
}