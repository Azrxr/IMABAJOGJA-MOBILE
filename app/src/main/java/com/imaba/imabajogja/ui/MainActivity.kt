package com.imaba.imabajogja.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.imaba.imabajogja.R
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

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var currentFragmentTag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    }

    private fun loadFragment(fragment: Fragment, tag: String) {
        currentFragmentTag = tag // Simpan tag fragment terakhir

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment, tag)
            .commit()
    }
}