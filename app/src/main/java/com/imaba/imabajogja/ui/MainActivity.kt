package com.imaba.imabajogja.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.utils.ReleaseManager
import com.imaba.imabajogja.databinding.ActivityMainBinding
import com.imaba.imabajogja.ui.admin.campuse.AdmCampuseFragment
import com.imaba.imabajogja.ui.admin.home.AdmHomeFragment
import com.imaba.imabajogja.ui.admin.member.AdmMemberFragment
import com.imaba.imabajogja.ui.admin.profile.AdmProfileFragment
import com.imaba.imabajogja.ui.campus.CampuseFragment
import com.imaba.imabajogja.ui.home.HomeFragment
import com.imaba.imabajogja.ui.member.MemberFragment
import com.imaba.imabajogja.ui.profile.ProfileFragment
import com.imaba.imabajogja.ui.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var currentFragmentTag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_IMABAJogja)
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Restore fragment terakhir jika ada
        if (savedInstanceState != null) {
            currentFragmentTag = savedInstanceState.getString("CURRENT_FRAGMENT")
        }
        setupUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("CURRENT_FRAGMENT", currentFragmentTag)
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            // Kembali ke fragment sebelumnya dalam stack
            supportFragmentManager.popBackStack()
            // Update currentFragmentTag dari fragment yang sekarang aktif
            currentFragmentTag = supportFragmentManager.fragments.lastOrNull()?.tag
        } else {
            super.onBackPressed()
        }
    }

    private fun showUpdateDialog() {
        AlertDialog.Builder(this)
            .setTitle("Update Tersedia")
            .setMessage("Versi terbaru aplikasi Imaba Jogja sudah tersedia. Silakan update untuk mendapatkan fitur terbaru.")
            .setCancelable(true)
            .setPositiveButton("Update") { _, _ ->
                val appPackageName = packageName
                try {
                    startActivity(Intent(Intent.ACTION_VIEW,
                        "market://details?id=$appPackageName".toUri()))
                } catch (e: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW,
                        "https://play.google.com/store/apps/details?id=$appPackageName".toUri()))
                }
            }
            .setNegativeButton("Nanti Saja") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun setupUI() {
        // Deteksi mode terang/gelap
        val isDarkMode =
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> true
                else -> false
            }

        // Ambil warna sesuai mode
        val resolvedColor = ContextCompat.getColor(
            this,
            if (isDarkMode) R.color.maroon_primary_dark else R.color.maroon_primary
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                // Bersihkan flag transparan
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                // Aktifkan menggambar sistem bar
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

                statusBarColor = resolvedColor
                navigationBarColor = resolvedColor
            }
        }


        // Untuk Android M+ (ikon status bar terang/gelap)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            WindowCompat.getInsetsController(
                window,
                window.decorView
            )?.isAppearanceLightStatusBars = !isDarkMode
        }
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                setupBottomNav(user.role == "admin")
            }
        }
    }

    private fun setupBottomNav(isAdmin: Boolean) {
        val navView = if (isAdmin) binding.admBottomNav else binding.bottomNav
        val menuRes = if (isAdmin) R.menu.adm_bottom_nav_menu else R.menu.bottom_nav_menu

        // Sembunyikan semua bottom nav dulu
        binding.bottomNav.visibility = View.GONE
        binding.admBottomNav.visibility = View.GONE

        navView.visibility = View.VISIBLE
        navView.menu.clear()
        navView.inflateMenu(menuRes)

        navView.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> if (isAdmin) AdmHomeFragment() else HomeFragment()
                R.id.nav_member -> if (isAdmin) AdmMemberFragment() else MemberFragment()
                R.id.nav_study -> if (isAdmin) AdmCampuseFragment() else CampuseFragment()
                R.id.nav_profile -> if (isAdmin) AdmProfileFragment() else ProfileFragment()
                else -> null
            }
            fragment?.let { loadFragment(it, item.itemId.toString()) }
            true
        }

        // Load fragment terakhir atau default
        if (currentFragmentTag == null) {
            navView.selectedItemId = R.id.nav_home // Default fragment
        } else {
            // Coba restore fragment terakhir
            val lastFragment = supportFragmentManager.findFragmentByTag(currentFragmentTag)
            if (lastFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, lastFragment, currentFragmentTag)
                    .commit()
            } else {
                navView.selectedItemId = R.id.nav_home
            }
        }
        ReleaseManager.checkForAppUpdate(this) {
            showUpdateDialog()
        }
    }

    private fun loadFragment(fragment: Fragment, tag: String) {
        currentFragmentTag = tag // Simpan tag fragment terakhir

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment, tag)
            .commit()
    }
}