package com.imaba.imabajogja.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.utils.ReleaseManager
import com.imaba.imabajogja.databinding.ActivitySplashScreenBinding


@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Set theme splash sebelum super.onCreate()
        setTheme(R.style.Theme_IMABAJogja_Splash)
        super.onCreate(savedInstanceState)

        // Gunakan layout khusus splash (bukan empty binding)
        setContentView(R.layout.activity_splash_screen)

        // Navigasi setelah delay singkat
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 1500) // 1.5 detik

        val logo = findViewById<ImageView>(R.id.logo)
        logo.alpha = 0f
        logo.animate().alpha(1f).setDuration(1000).start()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        val versionName = ReleaseManager.getLocalVersionName(this)
        findViewById<TextView>(R.id.appVersion).text = getString(R.string.app_version, versionName)
    }
}